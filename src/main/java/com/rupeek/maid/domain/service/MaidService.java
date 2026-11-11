package com.rupeek.maid.domain.service;

import com.rupeek.maid.domain.model.AvailabilitySlot;
import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.repository.MaidRepository;
import com.rupeek.maid.domain.specification.MaidSearchFilter;
import com.rupeek.maid.domain.specification.MaidSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MaidService {

    @Autowired
    private MaidRepository maidRepository;

    @Transactional
    public Maid createMaid(Maid maid) {
        log.info("Creating new maid: {}", maid.getName());
        return maidRepository.save(maid);
    }

    @Transactional
    public Maid addAvailabilitySlots(Long maidId, Set<AvailabilitySlot> slots) {
        Maid maid = maidRepository.findById(maidId)
            .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + maidId));

        slots.forEach(maid::addAvailabilitySlot);
        return maidRepository.save(maid);
    }

    @Transactional(readOnly = true)
    public Maid getMaidById(Long id) {
        return maidRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Maid> searchMaids(MaidSearchFilter filter) {
        log.info("Searching maids with filter: {}", filter);
        return maidRepository.search(filter);
    }

    @Transactional(readOnly = true)
    public List<Maid> getAllAvailableMaids() {
        return maidRepository.findByAvailableTrue();
    }

    @Transactional
    public Maid updateMaid(Long id, Maid updatedMaid) {
        Maid maid = getMaidById(id);
        maid.setName(updatedMaid.getName());
        maid.setPhoneNumber(updatedMaid.getPhoneNumber());
        maid.setLocality(updatedMaid.getLocality());
        maid.setCity(updatedMaid.getCity());
        maid.setState(updatedMaid.getState());
        maid.setPincode(updatedMaid.getPincode());
        maid.setGender(updatedMaid.getGender());
        maid.setHourlyRate(updatedMaid.getHourlyRate());
        maid.setDescription(updatedMaid.getDescription());
        maid.setAvailable(updatedMaid.getAvailable());
        return maidRepository.save(maid);
    }

    @Transactional
    public void deleteMaid(Long id) {
        Maid maid = getMaidById(id);
        maidRepository.deleteById(id);
    }
}
