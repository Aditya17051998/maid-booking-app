package com.rupeek.maid.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"availabilitySlots"})
public class Maid {

    private Long id;
    private String name;
    private String phoneNumber;
    private String locality;
    private String city;
    private String state;
    private String pincode;
    private Gender gender;
    private Set<ServiceType> services = new HashSet<>();
    private BigDecimal hourlyRate;
    private Double rating = 0.0;
    private Integer totalReviews = 0;
    private Boolean available = true;
    private Set<AvailabilitySlot> availabilitySlots = new HashSet<>();
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addService(ServiceType serviceType) {
        this.services.add(serviceType);
    }

    public void removeService(ServiceType serviceType) {
        this.services.remove(serviceType);
    }

    public void addAvailabilitySlot(AvailabilitySlot slot) {
        this.availabilitySlots.add(slot);
        slot.setMaid(this);
    }

    public void updateRating(double newRating) {
        double totalRating = this.rating * this.totalReviews;
        this.totalReviews++;
        this.rating = (totalRating + newRating) / this.totalReviews;
    }
}
