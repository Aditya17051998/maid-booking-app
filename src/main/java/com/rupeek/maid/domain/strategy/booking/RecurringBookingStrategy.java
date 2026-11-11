package com.rupeek.maid.domain.strategy.booking;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingOccurrence;
import com.rupeek.maid.domain.model.BookingStatus;
import com.rupeek.maid.domain.model.BookingType;
import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import com.rupeek.maid.domain.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecurringBookingStrategy implements BookingStrategy {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingOccurrenceRepository occurrenceRepository;

    @Override
    public BookingType getBookingType() {
        return BookingType.RECURRING;
    }

    @Override
    public void validateBooking(Booking booking) {
        if (booking.getRecurrencePattern() == null || booking.getRecurrencePattern().isEmpty()) {
            throw new IllegalArgumentException("Recurrence pattern is required for recurring bookings");
        }
        if (booking.getRecurrenceEndDate() == null) {
            throw new IllegalArgumentException("Recurrence end date is required for recurring bookings");
        }

        LocalDateTime now = LocalDateTime.now();
        if (booking.getStartDateTime().isBefore(now)) {
            throw new IllegalArgumentException("Recurring booking start time must be in the future");
        }

        if (booking.getRecurrenceEndDate().isBefore(booking.getStartDateTime())) {
            throw new IllegalArgumentException("Recurrence end date must be after start date");
        }

        if (booking.getEndDateTime().isBefore(booking.getStartDateTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        validateRecurrencePattern(booking);
    }

    private void validateRecurrencePattern(Booking booking) {
        String pattern = booking.getRecurrencePattern();
        DayOfWeek startDay = booking.getStartDateTime().getDayOfWeek();

        if (pattern.startsWith("WEEKLY:")) {
            String[] parts = pattern.split(":");
            if (parts.length > 1) {
                String[] dayStrings = parts[1].split(",");
                List<DayOfWeek> specifiedDays = new ArrayList<>();

                for (String day : dayStrings) {
                    try {
                        specifiedDays.add(DayOfWeek.valueOf(day.trim()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid day of week in recurrence pattern: " + day);
                    }
                }

                if (!specifiedDays.contains(startDay)) {
                    throw new IllegalArgumentException(
                        String.format("Start date (%s) must fall on one of the specified days: %s",
                            startDay, String.join(", ", dayStrings))
                    );
                }
            }
        } else if (pattern.startsWith("WEEKDAYS")) {
            if (startDay == DayOfWeek.SATURDAY || startDay == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException(
                    "Start date must be a weekday (Monday-Friday) for WEEKDAYS recurrence pattern"
                );
            }
        }
    }

    @Override
    public void processBooking(Booking booking) {
        booking.setBookingType(BookingType.RECURRING);
        List<BookingOccurrence> occurrences = generateOccurrences(booking);
        occurrences.forEach(booking::addOccurrence);
    }

    private List<BookingOccurrence> generateOccurrences(Booking booking) {
        List<BookingOccurrence> occurrences = new ArrayList<>();
        String pattern = booking.getRecurrencePattern();

        if (pattern.startsWith("DAILY")) {
            generateDailyOccurrences(booking, occurrences);
        } else if (pattern.startsWith("WEEKLY")) {
            generateWeeklyOccurrences(booking, occurrences, pattern);
        } else if (pattern.startsWith("WEEKDAYS")) {
            generateWeekdaysOccurrences(booking, occurrences);
        } else {
            throw new IllegalArgumentException("Unsupported recurrence pattern: " + pattern);
        }

        return occurrences;
    }

    private void generateDailyOccurrences(Booking booking, List<BookingOccurrence> occurrences) {
        LocalDateTime current = booking.getStartDateTime();
        LocalDateTime endDate = booking.getRecurrenceEndDate();
        long durationMinutes = ChronoUnit.MINUTES.between(booking.getStartDateTime(), booking.getEndDateTime());

        while (!current.isAfter(endDate)) {
            occurrences.add(createOccurrence(current, current.plusMinutes(durationMinutes)));
            current = current.plusDays(1);
        }
    }

    private void generateWeeklyOccurrences(Booking booking, List<BookingOccurrence> occurrences, String pattern) {
        String[] parts = pattern.split(":");
        List<DayOfWeek> days = new ArrayList<>();

        if (parts.length > 1) {
            String[] dayStrings = parts[1].split(",");
            for (String day : dayStrings) {
                days.add(DayOfWeek.valueOf(day.trim()));
            }
        } else {
            days.add(booking.getStartDateTime().getDayOfWeek());
        }

        LocalDateTime current = booking.getStartDateTime();
        LocalDateTime endDate = booking.getRecurrenceEndDate();
        long durationMinutes = ChronoUnit.MINUTES.between(booking.getStartDateTime(), booking.getEndDateTime());

        while (!current.isAfter(endDate)) {
            if (days.contains(current.getDayOfWeek())) {
                occurrences.add(createOccurrence(current, current.plusMinutes(durationMinutes)));
            }
            current = current.plusDays(1);
        }
    }

    private void generateWeekdaysOccurrences(Booking booking, List<BookingOccurrence> occurrences) {
        LocalDateTime current = booking.getStartDateTime();
        LocalDateTime endDate = booking.getRecurrenceEndDate();
        long durationMinutes = ChronoUnit.MINUTES.between(booking.getStartDateTime(), booking.getEndDateTime());

        while (!current.isAfter(endDate)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                occurrences.add(createOccurrence(current, current.plusMinutes(durationMinutes)));
            }
            current = current.plusDays(1);
        }
    }

    private BookingOccurrence createOccurrence(LocalDateTime start, LocalDateTime end) {
        BookingOccurrence occurrence = new BookingOccurrence();
        occurrence.setOccurrenceStartTime(start);
        occurrence.setOccurrenceEndTime(end);
        occurrence.setStatus(BookingStatus.PENDING);
        return occurrence;
    }

    @Override
    public boolean isAvailable(Long maidId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<BookingStatus> excludedStatuses = List.of(BookingStatus.CANCELLED);

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
            maidId, startDateTime, endDateTime, excludedStatuses
        );

        List<BookingOccurrence> overlappingOccurrences = occurrenceRepository.findOverlappingOccurrences(
            maidId, startDateTime, endDateTime, excludedStatuses
        );

        return overlappingBookings.isEmpty() && overlappingOccurrences.isEmpty();
    }
}
