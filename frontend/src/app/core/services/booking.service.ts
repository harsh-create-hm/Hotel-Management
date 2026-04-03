import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Booking, BookingRequest } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private apiUrl = `${environment.apiUrl}/bookings`;

  constructor(private http: HttpClient) {}

  createBooking(request: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, request);
  }

  getUserBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/user`);
  }

  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.apiUrl);
  }

  cancelBooking(id: number): Observable<Booking> {
    return this.http.put<Booking>(`${this.apiUrl}/${id}/cancel`, {});
  }
}
