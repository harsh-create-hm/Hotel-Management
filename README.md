# 🏨 Hotel Management System

A full-stack hotel booking portal built with **Angular** (frontend) and **Spring Boot** (backend), featuring JWT-based authentication, role-based access control, and a clean layered architecture.

---

## 🏗️ Architecture

```
hotel-management/
├── backend/          # Spring Boot (Java 17+)
└── frontend/         # Angular 17
```

### Tech Stack

| Layer      | Technology                |
|------------|---------------------------|
| Frontend   | Angular 17 (Standalone Components) |
| Backend    | Spring Boot 3.2 (Java 17) |
| Database   | H2 (dev/test) / MySQL / PostgreSQL |
| Security   | Spring Security + JWT (jjwt) |
| Build Tool | Maven (backend) / Angular CLI (frontend) |

---

## ✨ Features

### 👤 User Management
- Register & Login with JWT tokens
- Role-based access: `USER` and `ADMIN`
- View & update profile

### 🏨 Hotel Management (Admin)
- Create, update, and delete hotels
- Hotel details: name, location, description
- Average rating display

### 🛏️ Room Management (Admin)
- Define rooms per hotel: Single, Double, Deluxe
- Price per night and availability count
- Optimistic locking to prevent double-booking

### 🔍 Search
- Search hotels by city/location
- View hotel details with available rooms and prices

### 📅 Booking System
- Book a room with check-in/check-out dates
- Booking status flow: `CREATED → CONFIRMED → CANCELLED`
- Cancel booking (restores room availability)

### 💳 Mock Payment
- Simulate payment for a booking
- Updates booking status to `CONFIRMED`

### ⭐ Reviews
- Authenticated users can submit ratings (1–5) and comments
- Average rating shown on hotel listing

---

## 🚀 Getting Started

### Backend

**Prerequisites:** Java 17+, Maven 3.8+

```bash
cd backend
mvn spring-boot:run
```

The API will start at `http://localhost:8080`.

By default, the app uses an **in-memory H2 database** (auto-reset on restart).

**Switch to MySQL/PostgreSQL** by editing `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hoteldb
    username: root
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### Frontend

**Prerequisites:** Node.js 18+, Angular CLI 17+

```bash
cd frontend
npm install
ng serve
```

The app will start at `http://localhost:4200`.

---

## 🔑 Default Configuration

| Setting | Value |
|---------|-------|
| API Base URL | `http://localhost:8080/api` |
| JWT Expiry | 24 hours |
| H2 Console | `http://localhost:8080/h2-console` |

---

## 🔐 API Endpoints

### Auth
```
POST /api/auth/register  - Register new user
POST /api/auth/login     - Login and get JWT token
```

### Hotels (Public GET, Admin POST/PUT/DELETE)
```
GET    /api/hotels                  - List all hotels (filter by ?location=)
GET    /api/hotels/{id}             - Get hotel details
POST   /api/hotels                  - Create hotel (ADMIN)
PUT    /api/hotels/{id}             - Update hotel (ADMIN)
DELETE /api/hotels/{id}             - Delete hotel (ADMIN)
```

### Rooms
```
GET    /api/hotels/{hotelId}/rooms  - List rooms for hotel (?availableOnly=true)
GET    /api/rooms/{id}              - Get room details
POST   /api/hotels/{hotelId}/rooms  - Create room (ADMIN)
PUT    /api/rooms/{id}              - Update room (ADMIN)
DELETE /api/rooms/{id}              - Delete room (ADMIN)
```

### Bookings (Authenticated)
```
POST /api/bookings              - Create booking
GET  /api/bookings/user         - Get current user's bookings
GET  /api/bookings              - Get all bookings (ADMIN)
PUT  /api/bookings/{id}/cancel  - Cancel booking
```

### Payments (Authenticated)
```
POST /api/payments/booking/{bookingId}  - Process payment (mock)
GET  /api/payments/booking/{bookingId}  - Get payment details
```

### Reviews (Authenticated)
```
POST /api/reviews               - Submit review
GET  /api/reviews/hotel/{id}    - Get hotel reviews
```

### Users (Authenticated)
```
GET /api/users/me     - Get current user profile
PUT /api/users/me     - Update name
GET /api/users        - List all users (ADMIN)
```

---

## 🧪 Running Tests

### Backend Unit Tests
```bash
cd backend
mvn test
```

Tests cover:
- `AuthService` - register, login
- `HotelService` - CRUD operations, search
- `BookingService` - create, cancel, availability checks

### Frontend Tests
```bash
cd frontend
npm test
```

Tests cover:
- `AuthService` - login, register, logout, token management
- `HotelService` - HTTP operations
- `BookingService` - booking CRUD

---

## 🗄️ Database Schema

```sql
users(id, name, email, password, role)
hotels(id, name, location, description)
rooms(id, hotel_id, type, price_per_night, available_count, version)
bookings(id, user_id, room_id, check_in, check_out, status)
payments(id, booking_id, status, amount)
reviews(id, user_id, hotel_id, rating, comment)
```

---

## 🔒 Security Notes

- Passwords are hashed with **BCrypt**
- JWT tokens are signed with **HS256**
- Role-based authorization via Spring Security's `@PreAuthorize`
- **Optimistic locking** on `Room.availableCount` (via `@Version`) prevents double-booking race conditions
- CORS configured to allow `http://localhost:4200`

---

## 📁 Project Structure

### Backend
```
src/main/java/com/hotel/management/
├── config/          - Security configuration
├── controller/      - REST controllers (Auth, Hotel, Room, Booking, Payment, Review, User)
├── dto/             - Request/Response DTOs
├── exception/       - Custom exceptions & GlobalExceptionHandler
├── model/           - JPA entities (User, Hotel, Room, Booking, Payment, Review)
├── repository/      - Spring Data JPA repositories
├── security/        - JWT provider, filter, UserDetailsService
└── service/         - Business logic (Auth, Hotel, Room, Booking, Payment, Review, User)
```

### Frontend
```
src/app/
├── auth/            - Login & Register components
├── hotels/          - Hotel list & detail components
├── bookings/        - Booking form & list components
├── admin/           - Hotel & room management forms (Admin only)
└── core/
    ├── guards/      - Auth & Admin guards
    ├── interceptors/- JWT interceptor
    ├── models/      - TypeScript interfaces
    └── services/    - HTTP services (Auth, Hotel, Room, Booking, Payment, Review)
```