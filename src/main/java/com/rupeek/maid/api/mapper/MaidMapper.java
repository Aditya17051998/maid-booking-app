package com.rupeek.maid.api.mapper;

import com.rupeek.maid.api.dto.CreateMaidRequest;
import com.rupeek.maid.api.dto.MaidResponse;
import com.rupeek.maid.domain.model.Maid;
import org.springframework.stereotype.Component;

@Component
public class MaidMapper {

    public Maid toEntity(CreateMaidRequest request) {
        Maid maid = new Maid();
        maid.setName(request.getName());
        maid.setPhoneNumber(request.getPhoneNumber());
        maid.setLocality(request.getLocality());
        maid.setCity(request.getCity());
        maid.setState(request.getState());
        maid.setPincode(request.getPincode());
        maid.setGender(request.getGender());
        maid.setServices(request.getServices());
        maid.setHourlyRate(request.getHourlyRate());
        maid.setDescription(request.getDescription());
        maid.setAvailable(true);
        maid.setRating(0.0);
        maid.setTotalReviews(0);
        return maid;
    }

    public MaidResponse toResponse(Maid maid) {
        return MaidResponse.builder()
            .id(maid.getId())
            .name(maid.getName())
            .phoneNumber(maid.getPhoneNumber())
            .locality(maid.getLocality())
            .city(maid.getCity())
            .state(maid.getState())
            .pincode(maid.getPincode())
            .gender(maid.getGender())
            .services(maid.getServices())
            .hourlyRate(maid.getHourlyRate())
            .rating(maid.getRating())
            .totalReviews(maid.getTotalReviews())
            .available(maid.getAvailable())
            .description(maid.getDescription())
            .build();
    }
}
