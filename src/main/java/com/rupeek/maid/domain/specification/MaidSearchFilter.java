package com.rupeek.maid.domain.specification;

import com.rupeek.maid.domain.model.Gender;
import com.rupeek.maid.domain.model.ServiceType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MaidSearchFilter {
    private String locality;
    private String city;
    private ServiceType serviceType;
    private Gender gender;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private Boolean availableOnly;
}
