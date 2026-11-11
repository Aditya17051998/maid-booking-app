package com.rupeek.maid.config;

import com.rupeek.maid.domain.model.*;
import com.rupeek.maid.domain.repository.MaidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private MaidRepository maidRepository;

    @Override
    public void run(String... args) {
        if (maidRepository.count() == 0) {
            log.info("Loading sample maid data...");
            loadSampleData();
            log.info("Sample data loaded successfully");
        }
    }

    private void loadSampleData() {
        Maid maid1 = createMaid(
            "Lakshmi Devi",
            "9876543210",
            "Koramangala",
            "Bangalore",
            "Karnataka",
            "560034",
            Gender.FEMALE,
            new HashSet<>(Arrays.asList(ServiceType.CLEANING, ServiceType.COOKING)),
            new BigDecimal("200.00"),
            "Experienced maid with 5 years in household work",
            4.5,
            20
        );
        addWeekdayAvailability(maid1);

        Maid maid2 = createMaid(
            "Ramesh Kumar",
            "9876543211",
            "Indiranagar",
            "Bangalore",
            "Karnataka",
            "560038",
            Gender.MALE,
            new HashSet<>(Arrays.asList(ServiceType.DISHWASHING, ServiceType.LAUNDRY)),
            new BigDecimal("150.00"),
            "Reliable and punctual",
            4.0,
            15
        );
        addWeekdayAvailability(maid2);

        Maid maid3 = createMaid(
            "Savita Sharma",
            "9876543212",
            "Whitefield",
            "Bangalore",
            "Karnataka",
            "560066",
            Gender.FEMALE,
            new HashSet<>(Arrays.asList(ServiceType.COOKING, ServiceType.BABYSITTING)),
            new BigDecimal("250.00"),
            "Specialized in cooking and childcare",
            4.8,
            35
        );
        addFullWeekAvailability(maid3);

        Maid maid4 = createMaid(
            "Anjali Reddy",
            "9876543213",
            "HSR Layout",
            "Bangalore",
            "Karnataka",
            "560102",
            Gender.FEMALE,
            new HashSet<>(Arrays.asList(ServiceType.CLEANING, ServiceType.DISHWASHING, ServiceType.LAUNDRY)),
            new BigDecimal("180.00"),
            "All-round household services",
            4.2,
            12
        );
        addWeekdayAvailability(maid4);

        Maid maid5 = createMaid(
            "Gita Kumari",
            "9876543214",
            "Koramangala",
            "Bangalore",
            "Karnataka",
            "560034",
            Gender.FEMALE,
            new HashSet<>(Arrays.asList(ServiceType.ELDERLY_CARE, ServiceType.COOKING)),
            new BigDecimal("300.00"),
            "Trained in elderly care with patience and compassion",
            4.7,
            25
        );
        addFullWeekAvailability(maid5);

        maidRepository.saveAll(Arrays.asList(maid1, maid2, maid3, maid4, maid5));
    }

    private Maid createMaid(String name, String phone, String locality, String city,
                           String state, String pincode, Gender gender,
                           Set<ServiceType> services, BigDecimal hourlyRate,
                           String description, double rating, int reviews) {
        Maid maid = new Maid();
        maid.setName(name);
        maid.setPhoneNumber(phone);
        maid.setLocality(locality);
        maid.setCity(city);
        maid.setState(state);
        maid.setPincode(pincode);
        maid.setGender(gender);
        maid.setServices(services);
        maid.setHourlyRate(hourlyRate);
        maid.setDescription(description);
        maid.setRating(rating);
        maid.setTotalReviews(reviews);
        maid.setAvailable(true);
        return maid;
    }

    private void addWeekdayAvailability(Maid maid) {
        Arrays.asList(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ).forEach(day -> {
            maid.addAvailabilitySlot(createSlot(maid, day, LocalTime.of(8, 0), LocalTime.of(12, 0)));
            maid.addAvailabilitySlot(createSlot(maid, day, LocalTime.of(14, 0), LocalTime.of(18, 0)));
        });
    }

    private void addFullWeekAvailability(Maid maid) {
        Arrays.asList(DayOfWeek.values()).forEach(day -> {
            maid.addAvailabilitySlot(createSlot(maid, day, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        });
    }

    private AvailabilitySlot createSlot(Maid maid, DayOfWeek day, LocalTime start, LocalTime end) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setMaid(maid);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        return slot;
    }
}
