package com.rupeek.maid.config;

import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import com.rupeek.maid.domain.repository.BookingRepository;
import com.rupeek.maid.domain.repository.MaidRepository;
import com.rupeek.maid.domain.repository.PaymentRepository;
import com.rupeek.maid.database.inmemory.InMemoryBookingOccurrenceRepository;
import com.rupeek.maid.database.inmemory.InMemoryBookingRepository;
import com.rupeek.maid.database.inmemory.InMemoryMaidRepository;
import com.rupeek.maid.database.inmemory.InMemoryPaymentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Current Implementation: In-Memory (using ConcurrentHashMap)
 * Future Extensions: MySQL or MongoDBwithout changing domain logic
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "IN_MEMORY")
    public MaidRepository maidRepository() {
        return new InMemoryMaidRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "IN_MEMORY")
    public BookingRepository bookingRepository() {
        return new InMemoryBookingRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "IN_MEMORY")
    public PaymentRepository paymentRepository() {
        return new InMemoryPaymentRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "database.type", havingValue = "IN_MEMORY")
    public BookingOccurrenceRepository bookingOccurrenceRepository() {
        return new InMemoryBookingOccurrenceRepository();
    }

    // @Bean
    // @ConditionalOnProperty(name = "database.type", havingValue = "MYSQL")
    // public MaidRepository mysqlMaidRepository() {
    //     return new MySQLMaidRepository();
    // }

}
