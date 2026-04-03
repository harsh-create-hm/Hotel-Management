import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookingService } from './booking.service';
import { Booking } from '../models/booking.model';

describe('BookingService', () => {
  let service: BookingService;
  let httpMock: HttpTestingController;

  const mockBooking: Booking = {
    id: 1, userId: 1, userName: 'Alice', roomId: 1,
    roomType: 'DOUBLE', hotelId: 1, hotelName: 'Grand Hotel',
    checkIn: '2025-06-01', checkOut: '2025-06-03',
    status: 'CREATED', totalAmount: 200
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookingService]
    });
    service = TestBed.inject(BookingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should create a booking', () => {
    service.createBooking({ roomId: 1, checkIn: '2025-06-01', checkOut: '2025-06-03' }).subscribe(b => {
      expect(b.id).toBe(1);
      expect(b.status).toBe('CREATED');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/bookings');
    expect(req.request.method).toBe('POST');
    req.flush(mockBooking);
  });

  it('should get user bookings', () => {
    service.getUserBookings().subscribe(bookings => {
      expect(bookings.length).toBe(1);
    });
    const req = httpMock.expectOne('http://localhost:8080/api/bookings/user');
    req.flush([mockBooking]);
  });

  it('should cancel a booking', () => {
    const cancelled = { ...mockBooking, status: 'CANCELLED' as const };
    service.cancelBooking(1).subscribe(b => {
      expect(b.status).toBe('CANCELLED');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/bookings/1/cancel');
    expect(req.request.method).toBe('PUT');
    req.flush(cancelled);
  });
});
