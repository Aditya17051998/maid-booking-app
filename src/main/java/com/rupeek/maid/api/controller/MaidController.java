package com.rupeek.maid.api.controller;

import com.rupeek.maid.api.dto.CreateMaidRequest;
import com.rupeek.maid.api.dto.MaidResponse;
import com.rupeek.maid.api.dto.SearchMaidRequest;
import com.rupeek.maid.api.mapper.MaidMapper;
import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.service.MaidService;
import com.rupeek.maid.domain.specification.MaidSearchFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maids")
@Tag(name = "Maid Management", description = "APIs for maid onboarding and discovery")
public class MaidController {

    @Autowired
    private MaidService maidService;

    @Autowired
    private MaidMapper maidMapper;

    @PostMapping
    @Operation(summary = "Create a new maid", description = "Onboard a new maid to the platform")
    public ResponseEntity<MaidResponse> createMaid(@Valid @RequestBody CreateMaidRequest request) {
        Maid maid = maidMapper.toEntity(request);
        Maid createdMaid = maidService.createMaid(maid);
        return ResponseEntity.status(HttpStatus.CREATED).body(maidMapper.toResponse(createdMaid));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maid by ID", description = "Retrieve maid details by ID")
    public ResponseEntity<MaidResponse> getMaidById(@PathVariable Long id) {
        Maid maid = maidService.getMaidById(id);
        return ResponseEntity.ok(maidMapper.toResponse(maid));
    }

    @GetMapping
    @Operation(summary = "Get all available maids", description = "Retrieve all available maids")
    public ResponseEntity<List<MaidResponse>> getAllMaids() {
        List<Maid> maids = maidService.getAllAvailableMaids();
        List<MaidResponse> responses = maids.stream()
            .map(maidMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    @Operation(summary = "Search maids", description = "Search maids with filters")
    public ResponseEntity<List<MaidResponse>> searchMaids(@RequestBody SearchMaidRequest request) {
        MaidSearchFilter filter = MaidSearchFilter.builder()
            .locality(request.getLocality())
            .city(request.getCity())
            .serviceType(request.getServiceType())
            .gender(request.getGender())
            .minPrice(request.getMinPrice())
            .maxPrice(request.getMaxPrice())
            .minRating(request.getMinRating())
            .availableFrom(request.getAvailableFrom())
            .availableTo(request.getAvailableTo())
            .availableOnly(request.getAvailableOnly())
            .build();

        List<Maid> maids = maidService.searchMaids(filter);
        List<MaidResponse> responses = maids.stream()
            .map(maidMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update maid", description = "Update maid details")
    public ResponseEntity<MaidResponse> updateMaid(@PathVariable Long id, @Valid @RequestBody CreateMaidRequest request) {
        Maid updatedMaid = maidMapper.toEntity(request);
        Maid maid = maidService.updateMaid(id, updatedMaid);
        return ResponseEntity.ok(maidMapper.toResponse(maid));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete maid", description = "Delete a maid from the system")
    public ResponseEntity<Void> deleteMaid(@PathVariable Long id) {
        maidService.deleteMaid(id);
        return ResponseEntity.noContent().build();
    }
}
