import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Payment } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  processPayment(bookingId: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/booking/${bookingId}`, {});
  }

  getPayment(bookingId: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/booking/${bookingId}`);
  }
}
