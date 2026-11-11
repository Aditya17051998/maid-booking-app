package com.rupeek.maid.domain.service;

import com.rupeek.maid.domain.model.Gender;
import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.model.ServiceType;
import com.rupeek.maid.domain.repository.MaidRepository;
import com.rupeek.maid.domain.specification.MaidSearchFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaidServiceTest {

    @Mock
    private MaidRepository maidRepository;

    @InjectMocks
    private MaidService maidService;

    private Maid maid;

    @BeforeEach
    void setUp() {
        Set<ServiceType> services = new HashSet<>();
        services.add(ServiceType.CLEANING);
        services.add(ServiceType.COOKING);

        maid = new Maid();
        maid.setId(1L);
        maid.setName("Test Maid");
        maid.setPhoneNumber("1234567890");
        maid.setLocality("Test Locality");
        maid.setCity("Test City");
        maid.setGender(Gender.FEMALE);
        maid.setServices(services);
        maid.setHourlyRate(new BigDecimal("200.00"));
        maid.setAvailable(true);
        maid.setRating(4.5);
        maid.setTotalReviews(10);
    }

    @Test
    void testCreateMaid() {
        when(maidRepository.save(any(Maid.class))).thenReturn(maid);

        Maid created = maidService.createMaid(maid);

        assertNotNull(created);
        assertEquals("Test Maid", created.getName());
        verify(maidRepository, times(1)).save(any(Maid.class));
    }

    @Test
    void testGetMaidById_Found() {
        when(maidRepository.findById(1L)).thenReturn(Optional.of(maid));

        Maid found = maidService.getMaidById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Test Maid", found.getName());
    }

    @Test
    void testGetMaidById_NotFound() {
        when(maidRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            maidService.getMaidById(999L)
        );
    }

    @Test
    void testSearchMaids() {
        MaidSearchFilter filter = MaidSearchFilter.builder()
            .locality("Test Locality")
            .serviceType(ServiceType.CLEANING)
            .build();

        maidService.searchMaids(filter);

        verify(maidRepository, times(1)).search(any(MaidSearchFilter.class));
    }

    @Test
    void testDeleteMaid() {
        when(maidRepository.findById(1L)).thenReturn(Optional.of(maid));

        maidService.deleteMaid(1L);

        verify(maidRepository, times(1)).deleteById(1L);
    }
}
