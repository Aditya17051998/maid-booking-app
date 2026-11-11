package com.rupeek.maid.database.inmemory;

import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.model.ServiceType;
import com.rupeek.maid.domain.repository.MaidRepository;
import com.rupeek.maid.domain.specification.MaidSearchFilter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryMaidRepository implements MaidRepository {

    private final Map<Long, Maid> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Maid save(Maid maid) {
        if (maid.getId() == null) {
            maid.setId(idGenerator.getAndIncrement());
            maid.setCreatedAt(LocalDateTime.now());
        }
        maid.setUpdatedAt(LocalDateTime.now());
        storage.put(maid.getId(), maid);
        return maid;
    }

    @Override
    public List<Maid> saveAll(Iterable<Maid> maids) {
        List<Maid> savedMaids = new ArrayList<>();
        maids.forEach(maid -> savedMaids.add(save(maid)));
        return savedMaids;
    }

    @Override
    public Optional<Maid> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Maid> findAll() {
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
    public List<Maid> findByLocalityAndAvailableTrue(String locality) {
        return storage.values().stream()
            .filter(maid -> maid.getAvailable())
            .filter(maid -> maid.getLocality().equalsIgnoreCase(locality))
            .collect(Collectors.toList());
    }

    @Override
    public List<Maid> findByAvailableTrue() {
        return storage.values().stream()
            .filter(Maid::getAvailable)
            .collect(Collectors.toList());
    }

    @Override
    public List<Maid> search(MaidSearchFilter filter) {
        return storage.values().stream()
            .filter(maid -> matchesFilter(maid, filter))
            .collect(Collectors.toList());
    }

    private boolean matchesFilter(Maid maid, MaidSearchFilter filter) {
        if (filter.getLocality() != null && !maid.getLocality().equalsIgnoreCase(filter.getLocality())) {
            return false;
        }

        if (filter.getCity() != null && !maid.getCity().equalsIgnoreCase(filter.getCity())) {
            return false;
        }

        if (filter.getServiceType() != null && !maid.getServices().contains(filter.getServiceType())) {
            return false;
        }

        if (filter.getGender() != null && maid.getGender() != filter.getGender()) {
            return false;
        }

        if (filter.getMinPrice() != null && maid.getHourlyRate().compareTo(filter.getMinPrice()) < 0) {
            return false;
        }

        if (filter.getMaxPrice() != null && maid.getHourlyRate().compareTo(filter.getMaxPrice()) > 0) {
            return false;
        }

        if (filter.getMinRating() != null && maid.getRating() < filter.getMinRating()) {
            return false;
        }

        if (filter.getAvailableOnly() != null && filter.getAvailableOnly() && !maid.getAvailable()) {
            return false;
        }

        return true;
    }
}
