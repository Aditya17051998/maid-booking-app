package com.rupeek.maid.domain.repository;

import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.specification.MaidSearchFilter;
import java.util.List;
import java.util.Optional;

public interface MaidRepository {
    Maid save(Maid maid);
    List<Maid> saveAll(Iterable<Maid> maids);
    Optional<Maid> findById(Long id);
    List<Maid> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();

    List<Maid> findByLocalityAndAvailableTrue(String locality);
    List<Maid> findByAvailableTrue();
    List<Maid> search(MaidSearchFilter filter);
}
