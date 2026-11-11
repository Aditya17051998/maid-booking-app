package com.rupeek.maid.api.dto;

import com.rupeek.maid.domain.model.Gender;
import com.rupeek.maid.domain.model.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaidResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String locality;
    private String city;
    private String state;
    private String pincode;
    private Gender gender;
    private Set<ServiceType> services;
    private BigDecimal hourlyRate;
    private Double rating;
    private Integer totalReviews;
    private Boolean available;
    private String description;
}
