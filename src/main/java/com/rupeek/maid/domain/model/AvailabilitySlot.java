package com.rupeek.maid.domain.model;

import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"maid"})
public class AvailabilitySlot {

    private Long id;
    private Maid maid;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean overlaps(DayOfWeek day, LocalTime start, LocalTime end) {
        if (!this.dayOfWeek.equals(day)) {
            return false;
        }
        return !end.isBefore(this.startTime) && !start.isAfter(this.endTime);
    }

    public boolean contains(DayOfWeek day, LocalTime time) {
        if (!this.dayOfWeek.equals(day)) {
            return false;
        }
        return !time.isBefore(this.startTime) && !time.isAfter(this.endTime);
    }
}
