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

---

## 🧪 Testing Guide (UI & Postman)

> ⚠️ **Important**: The H2 database is in-memory (`create-drop`). All data is wiped on restart. You must re-seed data every time the backend restarts.

---

### 🚀 Start the Application

**Backend:**
```bash
cd backend
mvn spring-boot:run
# Runs at http://localhost:8080
```

**Frontend:**
```bash
cd frontend
ng serve
# Runs at http://localhost:4200
```

---

### 📬 Postman Testing — Step-by-Step

Set `{{baseUrl}} = http://localhost:8080/api` as a Postman environment variable.

---

#### Step 1 — Register Users

**Register a Regular User:**
```
POST {{baseUrl}}/auth/register
Content-Type: application/json

{
  "name": "Alice User",
  "email": "alice@example.com",
  "password": "password123"
}
```
Expected: `200 OK` with `{ "token": "...", "role": "USER" }`

**Register an Admin User:**
```
POST {{baseUrl}}/auth/register
Content-Type: application/json

{
  "name": "Admin Bob",
  "email": "admin@example.com",
  "password": "admin123"
}
```

> 📌 **Promote to ADMIN**: Open the H2 Console at `http://localhost:8080/h2-console`
> (JDBC URL: `jdbc:h2:mem:hoteldb`, Username: `sa`, Password: *(empty)*) and run:
> ```sql
> UPDATE USERS SET ROLE = 'ADMIN' WHERE EMAIL = 'admin@example.com';
> ```

---

#### Step 2 — Login & Get JWT Token

**Login as Admin:**
```
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```
Response:
```json
{
  "token": "eyJhbGci...",
  "email": "admin@example.com",
  "role": "ADMIN",
  "name": "Admin Bob"
}
```
💾 Save this token as `{{adminToken}}` in your Postman environment.

**Login as Regular User:**
```
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "password123"
}
```
💾 Save this token as `{{userToken}}`.

---

#### Step 3 — Create Hotels (Admin only)

```
POST {{baseUrl}}/hotels
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "name": "Grand City Hotel",
  "location": "New York",
  "description": "A 5-star luxury hotel in downtown Manhattan"
}
```
Expected: `200 OK` with `{ "id": 1, "name": "Grand City Hotel", ... }`

Create a second hotel:
```json
{
  "name": "Beach Paradise Resort",
  "location": "Miami",
  "description": "Beachfront resort with ocean views"
}
```

---

#### Step 4 — Create Rooms (Admin only)

```
POST {{baseUrl}}/hotels/1/rooms
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
  "type": "SINGLE",
  "pricePerNight": 99.99,
  "availableCount": 10
}
```

Additional room types to create:
```json
{ "type": "DOUBLE",  "pricePerNight": 149.99, "availableCount": 5 }
{ "type": "DELUXE",  "pricePerNight": 299.99, "availableCount": 3 }
```

Valid room types: `SINGLE`, `DOUBLE`, `DELUXE`

---

#### Step 5 — Browse Hotels & Rooms (Public)

```
GET {{baseUrl}}/hotels                          # All hotels
GET {{baseUrl}}/hotels?location=New York        # Filter by location
GET {{baseUrl}}/hotels/1                        # Hotel details by ID
GET {{baseUrl}}/hotels/1/rooms                  # All rooms for hotel 1
GET {{baseUrl}}/hotels/1/rooms?availableOnly=true  # Available rooms only
```

---

#### Step 6 — Create a Booking (Authenticated User)

```
POST {{baseUrl}}/bookings
Authorization: Bearer {{userToken}}
Content-Type: application/json

{
  "roomId": 1,
  "checkIn": "2025-06-01",
  "checkOut": "2025-06-05"
}
```
Expected response includes `"status": "CREATED"` and `"totalAmount": 399.96` (4 nights × $99.99)

---

#### Step 7 — Process Payment

```
POST {{baseUrl}}/payments/booking/1
Authorization: Bearer {{userToken}}
```
Expected: `{ "status": "COMPLETED", "amount": 399.96 }`

---

#### Step 8 — View Bookings

```
# User's own bookings
GET {{baseUrl}}/bookings/user
Authorization: Bearer {{userToken}}

# All bookings (Admin only)
GET {{baseUrl}}/bookings
Authorization: Bearer {{adminToken}}
```

---

#### Step 9 — Cancel a Booking

```
PUT {{baseUrl}}/bookings/1/cancel
Authorization: Bearer {{userToken}}
```
Expected: `"status": "CANCELLED"`

---

#### Step 10 — Write a Review

```
POST {{baseUrl}}/reviews
Authorization: Bearer {{userToken}}
Content-Type: application/json

{
  "hotelId": 1,
  "rating": 5,
  "comment": "Absolutely wonderful stay, highly recommended!"
}
```

**View reviews for a hotel (public):**
```
GET {{baseUrl}}/reviews/hotel/1
```

---

#### Step 11 — User Profile

```
# Get current user
GET {{baseUrl}}/users/me
Authorization: Bearer {{userToken}}

# Update name
PUT {{baseUrl}}/users/me
Authorization: Bearer {{userToken}}
Content-Type: application/json
{ "name": "Alice Updated" }

# Get all users (Admin only)
GET {{baseUrl}}/users
Authorization: Bearer {{adminToken}}
```

---

### ❌ Error Cases to Test

| Scenario | Expected Response |
|----------|------------------|
| Login with wrong password | `401 Unauthorized` |
| Access `/api/bookings` without token | `403 Forbidden` |
| `POST /api/hotels` as regular user | `403 Forbidden` |
| `rating` outside 1–5 in review | `400 Bad Request` |
| `checkOut` before `checkIn` in booking | `400 Bad Request` |
| Missing required field (e.g. no `email`) | `400 Bad Request` |
| Duplicate payment for same booking | Error / conflict |
| `availableCount: -1` for room | `400 Bad Request` |

---

### 🖥️ UI Testing — Step-by-Step (`http://localhost:4200`)

#### Step 1 — Register & Login
1. Go to `http://localhost:4200/auth/register`
2. Fill in: Name = `Alice User`, Email = `alice@example.com`, Password = `password123`
3. Submit — you will be redirected to the hotels list
4. Logout (if available in nav), then go to `/auth/login`
5. Login with your credentials

#### Step 2 — Browse Hotels (Public)
1. Visit `http://localhost:4200/hotels` — view the hotel list
2. Use the location search/filter to search `New York`
3. Click a hotel card to see its detail page at `/hotels/1`

#### Step 3 — Admin: Create Hotels & Rooms
1. Promote your admin user via H2 Console (see Step 1 in Postman section)
2. Login as `admin@example.com`
3. Navigate to `http://localhost:4200/admin/hotels/new`
4. Fill in the hotel form and submit
5. Then go to `http://localhost:4200/admin/hotels/1/rooms/new`
6. Fill in room details (type: `DELUXE`, price: `299.99`, count: `5`) and submit

#### Step 4 — Make a Booking (Logged-in User)
1. Login as `alice@example.com`
2. Go to `http://localhost:4200/hotels/1` — view hotel details and rooms
3. Navigate to `http://localhost:4200/bookings/new`
4. Enter check-in: `2025-06-01`, check-out: `2025-06-05`
5. Submit — booking is created with status `CREATED`

#### Step 5 — View & Cancel Bookings
1. Navigate to `http://localhost:4200/bookings`
2. View your bookings list with their statuses
3. Click **Cancel** on a booking — status changes to `CANCELLED`

#### Step 6 — Write a Review
1. Go to the hotel detail page at `/hotels/1`
2. Fill in a rating (1–5) and a comment
3. Submit — the review appears on the page with updated average rating

---

### 🗄️ H2 Database Console

Visit `http://localhost:8080/h2-console` to inspect data directly.

| Setting  | Value                      |
|----------|----------------------------|
| JDBC URL | `jdbc:h2:mem:hoteldb`      |
| Username | `sa`                       |
| Password | *(leave empty)*            |

Useful queries:
```sql
SELECT * FROM USERS;
SELECT * FROM HOTELS;
SELECT * FROM ROOMS;
SELECT * FROM BOOKINGS;
SELECT * FROM PAYMENTS;
SELECT * FROM REVIEWS;

-- Promote a user to ADMIN
UPDATE USERS SET ROLE = 'ADMIN' WHERE EMAIL = 'admin@example.com';
```