package com.rupeek.maid.domain.service;

import com.rupeek.maid.domain.model.*;
import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import com.rupeek.maid.domain.repository.BookingRepository;
import com.rupeek.maid.domain.strategy.booking.BookingStrategy;
import com.rupeek.maid.domain.strategy.cancellation.CancellationPolicy;
import com.rupeek.maid.domain.strategy.pricing.PricingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingOccurrenceRepository occurrenceRepository;

    @Autowired
    private MaidService maidService;

    @Autowired
    private List<BookingStrategy> bookingStrategies;

    @Autowired
    private PricingStrategy pricingStrategy;

    @Autowired
    private CancellationPolicy cancellationPolicy;

    private Map<BookingType, BookingStrategy> strategyMap;

    private Map<BookingType, BookingStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = bookingStrategies.stream()
                .collect(Collectors.toMap(BookingStrategy::getBookingType, Function.identity()));
        }
        return strategyMap;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking createBooking(Booking booking, String idempotencyKey) {
        if (idempotencyKey != null) {
            return bookingRepository.findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> createBookingInternal(booking, idempotencyKey));
        }
        return createBookingInternal(booking, UUID.randomUUID().toString());
    }

    private Booking createBookingInternal(Booking booking, String idempotencyKey) {
        log.info("Creating new booking for maid: {}, type: {}", booking.getMaid().getId(), booking.getBookingType());

        Maid maid = maidService.getMaidById(booking.getMaid().getId());
        booking.setMaid(maid);

        BookingStrategy strategy = getStrategyMap().get(booking.getBookingType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported booking type: " + booking.getBookingType());
        }

        strategy.validateBooking(booking);

        if (!strategy.isAvailable(maid.getId(), booking.getStartDateTime(), booking.getEndDateTime())) {
            throw new IllegalStateException("Maid is not available for the requested time slot");
        }

        BigDecimal calculatedPrice = pricingStrategy.calculatePrice(
            maid, booking.getStartDateTime(), booking.getEndDateTime()
        );
        booking.setTotalAmount(calculatedPrice);
        booking.setPaidAmount(BigDecimal.ZERO);

        strategy.processBooking(booking);

        String bookingRef = generateBookingReference();
        booking.setBookingReference(bookingRef);
        booking.setIdempotencyKey(idempotencyKey);

        try {
            return bookingRepository.save(booking);
        } catch (OptimisticLockingFailureException e) {
            log.error("Concurrent booking detected for maid: {}", maid.getId());
            throw new IllegalStateException("This time slot was just booked by another customer. Please try a different slot.");
        }
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + reference));
    }

    @Transactional(readOnly = true)
    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getMaidBookings(Long maidId) {
        return bookingRepository.findByMaidId(maidId);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.confirm();
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (!cancellationPolicy.canCancel(booking)) {
            throw new IllegalStateException("Booking cannot be cancelled according to policy");
        }

        booking.cancel();

        if (booking.isRecurring()) {
            booking.getOccurrences().forEach(occurrence -> {
                if (occurrence.isActive()) {
                    occurrence.cancel();
                }
            });
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public BookingOccurrence cancelOccurrence(Long occurrenceId) {
        BookingOccurrence occurrence = occurrenceRepository.findById(occurrenceId)
            .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        occurrence.cancel();
        return occurrenceRepository.save(occurrence);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateRefundAmount(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        return cancellationPolicy.calculateRefundAmount(booking);
    }

    private String generateBookingReference() {
        return "BKG-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(Long maidId, LocalDateTime startTime, LocalDateTime endTime, BookingType bookingType) {
        BookingStrategy strategy = getStrategyMap().get(bookingType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported booking type: " + bookingType);
        }
        return strategy.isAvailable(maidId, startTime, endTime);
    }
}
