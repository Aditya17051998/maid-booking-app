package com.rupeek.maid.domain.specification;

import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.model.ServiceType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class MaidSpecification {

    public static Specification<Maid> withFilter(MaidSearchFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getLocality() != null) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("locality")),
                    filter.getLocality().toLowerCase()
                ));
            }

            if (filter.getCity() != null) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("city")),
                    filter.getCity().toLowerCase()
                ));
            }

            if (filter.getServiceType() != null) {
                predicates.add(criteriaBuilder.isMember(filter.getServiceType(), root.get("services")));
            }

            if (filter.getGender() != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), filter.getGender()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("hourlyRate"),
                    filter.getMinPrice()
                ));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("hourlyRate"),
                    filter.getMaxPrice()
                ));
            }

            if (filter.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("rating"),
                    filter.getMinRating()
                ));
            }

            if (filter.getAvailableOnly() != null && filter.getAvailableOnly()) {
                predicates.add(criteriaBuilder.isTrue(root.get("available")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
