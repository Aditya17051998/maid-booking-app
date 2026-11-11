# Maid Booking System

Backend service for booking maids/household help. Built for a machine coding interview to demonstrate clean architecture and design patterns.

## Quick Start

```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Application runs on http://localhost:8080
```

**Java 17** required.

## What's Implemented

### Core Features
- **Maid Management** - Create maids with services, pricing, availability
- **Search** - Filter maids by location, service type, rating, price
- **Three Booking Types**:
  - Instant (book now)
  - Scheduled (book for future)
  - Recurring (daily/weekly/weekdays patterns)
- **Payment Processing** - Card, UPI, Wallet (mock implementation)
- **Cancellation** - Cancel bookings, get refunds

### Design Patterns Used
- **Strategy Pattern** - Different booking types, payment methods, pricing strategies
- **Repository Pattern** - Clean data access abstraction
- **Factory Pattern** - Map-based strategy selection for O(1) lookup
- **Specification Pattern** - Dynamic search filters

### Technical Highlights
- Prevents double-booking with optimistic locking
- Payment idempotency (safe retries)
- Dynamic pricing (peak hours, weekends)
- Global exception handler for consistent error responses
- In-memory database (easily switchable to MySQL/Postgres)

## What's NOT Implemented

- **Customer Management** - Just using customer IDs, no auth/user management
- **Real Payment Gateway** - Mock implementation only
- **Email/SMS Notifications** - No notification system
- **Admin Panel** - No UI, API only
- **Reviews/Ratings** - Can track ratings but no full review system
- **Advanced Search** - No fuzzy search or recommendations

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- H2 Database (in-memory)
- Maven
- Lombok

## Project Structure

```
src/main/java/com/rupeek/maid/
├── api/
│   ├── controller/          # REST endpoints
│   ├── dto/                 # Request/response objects
│   └── exception/           # Global exception handler
├── domain/
│   ├── model/               # Entities (Maid, Booking, Payment)
│   ├── repository/          # Data access interfaces
│   ├── service/             # Business logic
│   └── strategy/
│       ├── booking/         # InstantBooking, ScheduledBooking, RecurringBooking
│       ├── payment/         # CardPayment, UpiPayment, WalletPayment
│       ├── pricing/         # StandardPricing, DynamicPricing
│       └── cancellation/    # Refund policies
└── config/                  # Spring configuration
```

## API Examples

### Create Maid
```bash
curl -X POST http://localhost:8080/api/maids \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Priya Sharma",
    "phoneNumber": "9876543210",
    "locality": "Koramangala",
    "city": "Bangalore",
    "state": "Karnataka",
    "pincode": "560034",
    "gender": "FEMALE",
    "services": ["CLEANING", "COOKING"],
    "hourlyRate": 200.00,
    "description": "Experienced in cleaning and cooking"
  }'
```

### Search Maids
```bash
curl -X POST http://localhost:8080/api/maids/search \
  -H "Content-Type: application/json" \
  -d '{
    "locality": "Koramangala",
    "serviceType": "CLEANING",
    "minRating": 4.0
  }'
```

### Create Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "maidId": 1,
    "customerId": 100,
    "bookingType": "SCHEDULED",
    "startDateTime": "2026-09-15T10:00:00",
    "endDateTime": "2026-09-15T12:00:00"
  }'
```

### Recurring Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "maidId": 1,
    "customerId": 100,
    "bookingType": "RECURRING",
    "startDateTime": "2026-09-16T09:00:00",
    "endDateTime": "2026-09-16T11:00:00",
    "recurrencePattern": "WEEKLY:MONDAY,WEDNESDAY,FRIDAY",
    "recurrenceEndDate": "2026-09-30T11:00:00"
  }'
```

### Process Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "paymentMethod": "UPI",
    "amount": 400.00
  }'
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=RecurringBookingStrategyTest
```

Tests cover:
- Booking strategies (instant, scheduled, recurring)
- Payment methods
- Service layer logic
- Edge cases and validations

## Database

Uses **in-memory** database. Data is lost on restart.

### Sample Data
Application loads 5 sample maids on startup:
- Different locations (Koramangala, Indiranagar, HSR Layout, etc.)
- Different services (Cleaning, Cooking, Dishwashing)
- Varying ratings and prices

## Design Decisions

### Why In-Memory Database?
Faster for demo, easily switchable to real DB. Repository pattern keeps domain logic independent of persistence.

### Why Strategy Pattern?
Easy to add new booking types or payment methods without touching existing code. Each strategy is a separate class.

### Why Map-based Factory?
Better than switch/case for selecting strategies. O(1) lookup and follows Open/Closed Principle - add new strategy by just creating a new class, no factory changes needed.

### Why No Customer Entity?
Kept scope focused on booking domain. In real app, customer management would be a separate microservice. Just using customer IDs here.

### Concurrency Handling
Used optimistic locking (`@Version`) to prevent double-booking. Better than pessimistic locking for performance.

### Idempotency
Payments use idempotency keys to prevent duplicate charges on retry.

## Validations

- Start time must be in future (for scheduled/recurring)
- Recurring bookings validate day-of-week matches pattern
- No overlapping bookings for same maid
- Payment amount can't exceed booking total
- Proper error messages via global exception handler

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html

## Configuration

Edit `src/main/resources/application.yml`:

```yaml
# Switch pricing strategy
pricing:
  strategy: DYNAMIC  # or STANDARD

# Database (currently H2)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

## If I Had More Time

- Add customer authentication (JWT)
- Real payment gateway integration
- WebSocket for real-time availability updates
- Caching with Redis
- Docker setup
- Better test coverage
- Admin APIs for reports/analytics

## Notes

- All dates are in system timezone (would add timezone support in production)
- Payment processing is mocked (returns success after 1 second delay)
- No rate limiting or authentication (would add in production)
- Customer IDs are just numbers (would integrate with user service)

---

Built for machine coding interview. Focused on clean code, design patterns, and extensibility over feature completeness.
