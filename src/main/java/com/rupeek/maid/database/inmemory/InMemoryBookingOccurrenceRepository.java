package com.rupeek.maid.database.inmemory;

import com.rupeek.maid.domain.model.BookingOccurrence;
import com.rupeek.maid.domain.model.BookingStatus;
import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryBookingOccurrenceRepository implements BookingOccurrenceRepository {

    private final Map<Long, BookingOccurrence> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public BookingOccurrence save(BookingOccurrence occurrence) {
        if (occurrence.getId() == null) {
            occurrence.setId(idGenerator.getAndIncrement());
            occurrence.setCreatedAt(LocalDateTime.now());
        }
        occurrence.setUpdatedAt(LocalDateTime.now());
        storage.put(occurrence.getId(), occurrence);
        return occurrence;
    }

    @Override
    public Optional<BookingOccurrence> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BookingOccurrence> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public List<BookingOccurrence> findByBookingId(Long bookingId) {
        return storage.values().stream()
            .filter(occurrence -> occurrence.getBooking().getId().equals(bookingId))
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingOccurrence> findOverlappingOccurrences(
        Long maidId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<BookingStatus> excludedStatuses
    ) {
        return storage.values().stream()
            .filter(occurrence -> occurrence.getBooking().getMaid().getId().equals(maidId))
            .filter(occurrence -> !excludedStatuses.contains(occurrence.getStatus()))
            .filter(occurrence -> isOverlapping(
                occurrence.getOccurrenceStartTime(),
                occurrence.getOccurrenceEndTime(),
                startTime,
                endTime
            ))
            .collect(Collectors.toList());
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
