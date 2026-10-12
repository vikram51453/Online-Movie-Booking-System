# Online Movie Ticket Booking System (CineSphere)

> **Phase 1: Full-Stack Application Development**  
> A production-grade cinema reservation platform built with Java 17, Spring Boot 3, Spring Data JPA, Spring Security, MySQL 8, and a modern responsive dark-themed frontend.

---

## 1. Abstract & Problem Statement

Traditional cinema ticket reservation workflows often suffer from inefficient seat booking processes, high latency during blockbuster releases, and the persistent risk of double booking when concurrent users compete for the same seats. Furthermore, many educational web applications present purely decorative user interfaces detached from rigorous transactional backend validations.

The **Online Movie Ticket Booking System** (**CineSphere**) is engineered to address these challenges. It provides an end-to-end, full-stack transactional solution featuring:
- Live multi-tier seating layouts (Silver, Gold, Platinum).
- Strict database-level and transaction-level concurrency safety preventing race conditions and double bookings.
- Authoritative backend pricing calculations.
- Role-Based Access Control (Customer vs. Administrator).
- A unified single-origin architecture serving a modern, high-fidelity dark-themed web client through Spring Boot static resources.

---

## 2. Project Objectives

1. **Transactional Integrity**: Guarantee zero duplicate seat reservations under concurrent customer booking attempts via transactional isolation and unique database constraints.
2. **Authoritative Billing**: Prevent client-side price tampering by computing all seat tier multipliers, subtotals, and total sums authoritatively on the backend.
3. **Robust Security**: Implement session-based authentication with BCrypt hashing, CSRF token validation (`CookieCsrfTokenRepository`), role-based endpoint authorization (`ROLE_CUSTOMER`, `ROLE_ADMIN`), and centralized exception handling.
4. **Intuitive User Experience**: Deliver 17 cohesive, dark-themed responsive pages featuring real-time seat status indicators, curved auditorium visualizations, digital boarding-pass tickets, and executive auditing dashboards.
5. **DevOps-Ready Architecture**: Structure the project cleanly for Phase 2 integration with Git, Jenkins, Docker containers, Ansible, and automated CI/CD pipelines.

---

## 3. Technology Stack

### Frontend
- **HTML5 & CSS3**: Custom design tokens, glassmorphic dark theme (`#07090e`), neon accents, and smooth transitions.
- **Bootstrap 5.3.3**: Responsive grid system, modals, and layout utilities.
- **Bootstrap Icons 1.11.3**: Crisp iconography.
- **JavaScript (ES6+)**: Vanilla client-side controller logic, session management, dynamic DOM rendering.
- **Fetch API**: Centralized REST client with automatic CSRF token header attachments (`X-XSRF-TOKEN`) and credential handling.

### Backend
- **Java 17 / 22**: Modern language features, strong typing, and record-ready data structures.
- **Spring Boot 3.2.5**: Core framework providing dependency injection, embedded Tomcat, and production configuration.
- **Spring Web (MVC)**: RESTful controllers and static resource routing.
- **Spring Data JPA & Hibernate 6**: Object-Relational Mapping, relational repository abstractions, and optimized JPQL queries.
- **Spring Security 6**: Session management, BCrypt password hashing, method-level security (`@PreAuthorize`), and CSRF protection.
- **Bean Validation (Hibernate Validator)**: Strict input validation on DTOs.
- **Apache Maven 3.9+**: Build automation and dependency lifecycle management.

### Database
- **MySQL 8.0**: Relational database with InnoDB engine, foreign keys, unique composite indexes, and ACID compliance.
- **H2 In-Memory Database (Test Scope)**: Isolated in-memory database in MySQL mode for deterministic automated testing.

### Testing
- **JUnit 5**: Modern unit and integration test runner.
- **Spring Boot Test & MockMvc**: Full application context testing, endpoint simulation, authorization checks, and concurrency conflict testing.

---

## 4. System Architecture

The backend adheres to a clean layered architecture:

```
src/main/java/com/cinemabooking/
├── config/                  # WebMvc, Static resource routing
├── controller/              # REST API controllers (Auth, Movie, Theatre, Show, Booking, Admin)
├── dto/                     # Strongly typed Request/Response data transfer objects
├── entity/                  # JPA entities (User, Movie, Theatre, Screen, Seat, Show, Booking, BookingSeat)
├── exception/               # Custom runtime exceptions & @RestControllerAdvice handler
├── repository/              # Spring Data JPA repositories with custom query methods
├── security/                # CustomUserDetails, CustomUserDetailsService, SecurityConfig, CsrfFilter
└── service/                 # Business logic, transactional operations, and data seeding
```

---

## 5. Database Schema & Relational Design

The system uses a MySQL 8 database named `movie_booking`.

### Tables & Relationships

1. **`users`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `full_name` (VARCHAR(100), NOT NULL)
   - `email` (VARCHAR(120), UNIQUE, NOT NULL)
   - `password` (VARCHAR(255), NOT NULL) - BCrypt hash
   - `phone` (VARCHAR(20))
   - `role` (VARCHAR(20), NOT NULL) - `ROLE_CUSTOMER`, `ROLE_ADMIN`
   - `created_at` (DATETIME, NOT NULL)

2. **`movies`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `title` (VARCHAR(150), NOT NULL)
   - `description` (TEXT)
   - `genre` (VARCHAR(100), NOT NULL)
   - `language` (VARCHAR(50), NOT NULL)
   - `duration_minutes` (INT, NOT NULL)
   - `release_date` (DATE)
   - `poster_url` (VARCHAR(500))
   - `trailer_url` (VARCHAR(500))
   - `rating` (DOUBLE)
   - `is_active` (BOOLEAN, NOT NULL)

3. **`theatres`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(120), NOT NULL)
   - `city` (VARCHAR(80), NOT NULL)
   - `address` (VARCHAR(255), NOT NULL)
   - `total_screens` (INT)
   - `is_active` (BOOLEAN, NOT NULL)

4. **`screens`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `theatre_id` (BIGINT, FK -> `theatres.id`, NOT NULL)
   - `name` (VARCHAR(80), NOT NULL)
   - `screen_type` (VARCHAR(50)) - *IMAX Laser 3D, Dolby Atmos 4K, Standard 2D*
   - `total_seats` (INT, NOT NULL)
   - `rows_count` (INT, NOT NULL)
   - `cols_count` (INT, NOT NULL)

5. **`seats`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `screen_id` (BIGINT, FK -> `screens.id`, NOT NULL)
   - `row_name` (VARCHAR(10), NOT NULL) - *A, B, C...*
   - `seat_number` (INT, NOT NULL) - *1, 2, 3...*
   - `seat_type` (VARCHAR(20), NOT NULL) - *SILVER (1.0x), GOLD (1.25x), PLATINUM (1.5x)*
   - **Unique Constraint**: `uk_screen_seat (screen_id, row_name, seat_number)`

6. **`shows`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `movie_id` (BIGINT, FK -> `movies.id`, NOT NULL)
   - `screen_id` (BIGINT, FK -> `screens.id`, NOT NULL)
   - `show_date` (DATE, NOT NULL)
   - `start_time` (TIME, NOT NULL)
   - `end_time` (TIME)
   - `base_price` (DECIMAL(10,2), NOT NULL)
   - `is_active` (BOOLEAN, NOT NULL)

7. **`bookings`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `booking_reference` (VARCHAR(40), UNIQUE, NOT NULL) - *e.g., MBK-20261011-9A3F1*
   - `user_id` (BIGINT, FK -> `users.id`, NOT NULL)
   - `show_id` (BIGINT, FK -> `shows.id`, NOT NULL)
   - `total_amount` (DECIMAL(10,2), NOT NULL)
   - `seat_count` (INT, NOT NULL)
   - `status` (VARCHAR(20), NOT NULL) - *CONFIRMED, CANCELLED*
   - `payment_status` (VARCHAR(20), NOT NULL) - *PAID, REFUNDED*
   - `booking_time` (DATETIME, NOT NULL)

8. **`booking_seats`**:
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `booking_id` (BIGINT, FK -> `bookings.id`, NOT NULL)
   - `show_id` (BIGINT, FK -> `shows.id`, NOT NULL)
   - `seat_id` (BIGINT, FK -> `seats.id`, NOT NULL)
   - `seat_number` (VARCHAR(15), NOT NULL)
   - `price` (DECIMAL(10,2), NOT NULL)
   - **Concurrency Safety Constraint**: `uk_show_seat_reservation (show_id, seat_id)`

---

## 6. REST API Endpoints Reference

| Method | Endpoint | Authorization | Description |
|---|---|---|---|
| **POST** | `/api/auth/register` | Public | Register new customer account |
| **POST** | `/api/auth/login` | Public | Authenticate user and initiate session |
| **POST** | `/api/auth/logout` | Public | Invalidate current session and clear security context |
| **GET** | `/api/auth/me` | Public | Retrieve authenticated profile details (or anonymous) |
| **GET** | `/api/auth/csrf` | Public | Retrieve active CSRF token parameter and token value |
| **GET** | `/api/movies` | Public | List active movies with optional search, genre, language filters |
| **GET** | `/api/movies/{id}` | Public | Retrieve single movie details |
| **GET** | `/api/movies/genres` | Public | Get list of distinct movie genres |
| **GET** | `/api/movies/languages` | Public | Get list of distinct movie languages |
| **POST** | `/api/movies` | Admin | Create a new movie record |
| **PUT** | `/api/movies/{id}` | Admin | Update existing movie details |
| **DELETE**| `/api/movies/{id}` | Admin | Deactivate movie (soft delete) |
| **GET** | `/api/theatres` | Public | Retrieve all active cinema theatres |
| **GET** | `/api/theatres/{id}` | Public | Retrieve theatre details by ID |
| **GET** | `/api/theatres/for-movie/{movieId}` | Public | Get theatres currently scheduling a specific movie |
| **GET** | `/api/theatres/{theatreId}/screens` | Public | List screens and capacities for a theatre |
| **POST** | `/api/theatres` | Admin | Register a new cinema theatre |
| **PUT** | `/api/theatres/{id}` | Admin | Update theatre details |
| **DELETE**| `/api/theatres/{id}` | Admin | Deactivate a theatre |
| **POST** | `/api/theatres/screens` | Admin | Create screen and auto-generate seat layout matrix |
| **GET** | `/api/shows` | Public | Query shows filtered by `movieId`, `theatreId`, `showDate` |
| **GET** | `/api/shows/{id}` | Public | Retrieve single show details |
| **GET** | `/api/shows/dates/{movieId}` | Public | List available calendar dates for a movie |
| **GET** | `/api/shows/{id}/seats` | Public | Interactive seat layout with real-time booked states & pricing |
| **POST** | `/api/shows` | Admin | Schedule a new showtime |
| **PUT** | `/api/shows/{id}` | Admin | Update scheduled showtime details |
| **DELETE**| `/api/shows/{id}` | Admin | Deactivate scheduled showtime |
| **POST** | `/api/bookings` | Customer / Admin | Submit seat reservation and authoritatively book tickets |
| **GET** | `/api/bookings/my` | Customer / Admin | Retrieve booking history for authenticated customer |
| **GET** | `/api/bookings/{id}` | Customer / Admin | Get booking details (customer restricted to own bookings) |
| **GET** | `/api/bookings/reference/{ref}`| Customer / Admin | Get booking by unique reference code |
| **POST** | `/api/bookings/{id}/cancel` | Customer / Admin | Cancel booking and release seats |
| **GET** | `/api/admin/stats` | Admin | Overview metrics: movies, theatres, shows, bookings, revenue |
| **GET** | `/api/admin/bookings` | Admin | List all bookings system-wide with optional status filter |
| **GET** | `/api/admin/movies` | Admin | Full movie catalog view including inactive titles |
| **GET** | `/api/admin/theatres` | Admin | Full theatres list including inactive venues |
| **GET** | `/api/admin/shows` | Admin | Full show schedules including inactive shows |

---

## 7. Frontend Pages (17 Complete Views)

All pages are served via Spring Boot's static resources handler:

1. **Home Page** (`/index.html`): Hero banner, instant search, genre pills, now-showing movie grid, cinema experience highlights.
2. **Registration Page** (`/register.html`): Customer account creation with client and server input validation.
3. **Login Page** (`/login.html`): Session login with preconfigured demo credentials buttons for quick testing.
4. **Movie Listing Page** (`/movies.html`): Search and filter catalog by genre, language, and sort by rating/release.
5. **Movie Details Page** (`/movie-details.html?id=X`): Poster, runtime, age ratings, synopsis, trailer links, and showtimes widget.
6. **Theatre Selection Page** (`/theatres.html`): Multiplex venue browser with location-aware movie filtering.
7. **Show Selection Page** (`/shows.html`): Showtime scheduling matrix filtered by movie, theatre, and calendar date.
8. **Interactive Seat Selection** (`/seat-selection.html?showId=Z`): Visual curved screen representation, Silver/Gold/Platinum seat matrix, sold out states, and live provisional calculator.
9. **Booking Summary Page** (`/booking-summary.html`): Itemized ticket review, seat badges, and simulated payment method selection.
10. **Booking Confirmation Page** (`/booking-confirmation.html?ref=REF`): Digital boarding pass ticket with QR entry code, reservation code, and print support (`window.print()`).
11. **My Bookings Page** (`/my-bookings.html`): Comprehensive customer booking history with cancellation and ticket view buttons.
12. **Customer Profile Page** (`/profile.html`): Personal account information and cumulative booking statistics.
13. **Admin Dashboard** (`/admin-dashboard.html`): High-level operational KPIs (Total Movies, Theatres, Shows, Bookings, Revenue, Customers) and quick actions.
14. **Movie Management** (`/admin-movies.html`): CRUD table to add, modify, upload posters, and deactivate movie titles.
15. **Theatre & Screen Management** (`/admin-theatres.html`): Venue management and interactive screen/seat grid generator.
16. **Show Scheduling Management** (`/admin-shows.html`): Program calendar showtimes with base pricing and screen associations.
17. **Booking Auditing Management** (`/admin-bookings.html`): Review all system reservations, filter by status, inspect tickets, and process refunds.

---

## 8. Prerequisites

- **Java Development Kit (JDK)**: Java 17 or higher (Verified on Java 22 / Java 21)
- **Apache Maven**: Version 3.8+ (Verified on Maven 3.9.16)
- **MySQL Server**: Version 8.0+ running on port `3306`

---

## 9. Database Setup & Environment Configuration

### Step 1: Initialize Database
Connect to your local MySQL instance and ensure the database exists:

```sql
CREATE DATABASE IF NOT EXISTS movie_booking;
```

### Step 2: Environment Variables (Optional Fallbacks Available)
The application is pre-configured with resilient defaults in `src/main/resources/application.properties`. You can override them using environment variables:

| Environment Variable | Default Value | Description |
|---|---|---|
| `SERVER_PORT` | `8081` | Web server port (`8081` chosen to avoid conflicts) |
| `DB_HOST` | `localhost` | MySQL host address |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `movie_booking` | Database schema name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `""` (empty) | Database password |
| `ADMIN_EMAIL` | `admin@cinemabooking.com` | Initial administrator login |
| `ADMIN_PASSWORD`| `Admin@123456` | Initial administrator password |
| `SEED_DATA_ENABLED`| `true` | Populate initial sample movies, theatres, and shows |

---

## 10. Application Execution & Verification

### Build Application
Compile all source code, execute automated unit tests, and assemble the executable JAR:

```powershell
mvn clean package
```

### Run Application
Run the packaged JAR:

```powershell
java -jar target/movie-booking-1.0.0.jar
```

Alternatively, run via Maven Spring Boot plugin:

```powershell
mvn spring-boot:run
```

Once started, open your web browser and navigate to:
```
http://localhost:8081
```

### Pre-Configured Test Accounts

| Role | Email | Password |
|---|---|---|
| **System Administrator** | `admin@cinemabooking.com` | `Admin@123456` |
| **Demo Customer** | `customer@cinemabooking.com` | `Customer@123` |

*(Quick-fill buttons for both accounts are available directly on the Login page)*

---

## 11. Test Execution Commands

The project includes an automated test suite verifying registration, authentication, role-based access restrictions, catalog filtering, seat availability, transactional booking, and concurrency conflicts using an isolated in-memory H2 database:

```powershell
mvn test
```

### Automated Tests Overview
- **`AuthIntegrationTest`**:
  - `testRegisterSuccess`: Customer account creation.
  - `testRegisterDuplicateEmail`: Duplicate email rejection with HTTP 400.
  - `testRegisterValidationFailure`: Missing and invalid fields rejection.
  - `testLoginSuccess`: Successful credential verification and session issuance.
  - `testLoginInvalidCredentials`: HTTP 401 on incorrect credentials.
  - `testCsrfEndpoint`: Validates CSRF token issuance.
- **`MovieControllerTest`**:
  - `testGetMoviesPublic`: Public unauthenticated movie catalog retrieval.
  - `testCreateMovieAdminSuccess`: Admin privilege movie creation.
  - `testCreateMovieForbiddenForCustomer`: Customer role rejection on admin endpoints (HTTP 403).
  - `testUpdateAndDeleteMovieAdmin`: Modification and soft deletion verification.
- **`BookingIntegrationTest`**:
  - `testGetShowSeats`: Show auditorium layout and booked seat status verification.
  - `testSuccessfulBooking`: Validates transactional booking and authoritative price calculation.
  - `testDuplicateSeatBookingPrevention`: Proves race-condition defense: concurrent booking on the same seat triggers HTTP 409 Conflict.
  - `testInvalidBookingEmptySeats`: Rejection of malformed booking requests.
  - `testCustomerAccessRestriction`: Enforces privacy; Customer A cannot access Customer B's reservation details.
  - `testAdminOperations`: Verifies system stats and auditing queries.

---

## 12. Concurrency Safety: How Double Booking is Prevented

To prevent double booking when multiple users attempt to reserve the same seat simultaneously:

1. **Transactional Boundary (`@Transactional(isolation = Isolation.READ_COMMITTED)`)**: Ensures seat availability checks and reservation insertions run within a coherent transaction.
2. **Pre-flight Active Check**: The service checks `bookingSeatRepository.findBookedSeatIdsForShow(...)` for all selected seats.
3. **Database Unique Constraint**: The `booking_seats` table enforces a composite unique constraint:
   ```sql
   CONSTRAINT uk_show_seat_reservation UNIQUE (show_id, seat_id)
   ```
4. **DataIntegrityViolation Translation**: If two concurrent transactions execute simultaneously and pass the pre-flight check at the same instant, the database engine enforces the constraint atomically on `INSERT`. The losing transaction is rolled back and caught by Spring's exception handler, returning a clean `409 Conflict` error to the client:
   > *"One or more selected seats were just reserved by another customer. Please select different seats."*

---

## 13. Troubleshooting

- **Port Conflict (`Port 8081 was already in use`)**:
  Override the port when launching:
  ```powershell
  java -Dserver.port=8085 -jar target/movie-booking-1.0.0.jar
  ```
- **MySQL Connection Refused**:
  Ensure MySQL 8 is running and listening on port 3306:
  ```powershell
  Get-Service MySQL80
  ```
- **Database Schema Updates**:
  Hibernate `ddl-auto` is set to `update`, which preserves existing data without wiping tables.

---

*Phase 1 of the Online Movie Ticket Booking System is complete, tested, and ready for Phase 2 DevOps integration.*
