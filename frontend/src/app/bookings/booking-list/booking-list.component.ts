import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../core/services/booking.service';
import { PaymentService } from '../../core/services/payment.service';
import { Booking } from '../../core/models/booking.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-list.component.html'
})
export class BookingListComponent implements OnInit {
  bookings: Booking[] = [];
  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private bookingService: BookingService,
    private paymentService: PaymentService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    const obs = this.authService.isAdmin
      ? this.bookingService.getAllBookings()
      : this.bookingService.getUserBookings();

    obs.subscribe({
      next: bookings => {
        this.bookings = bookings;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load bookings';
        this.loading = false;
      }
    });
  }

  cancelBooking(id: number): void {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    this.bookingService.cancelBooking(id).subscribe({
      next: () => {
        this.successMessage = 'Booking cancelled successfully';
        this.loadBookings();
      },
      error: err => this.error = err.error?.message || 'Failed to cancel booking'
    });
  }

  payBooking(id: number): void {
    this.paymentService.processPayment(id).subscribe({
      next: () => {
        this.successMessage = 'Payment successful! Booking confirmed.';
        this.loadBookings();
      },
      error: err => this.error = err.error?.message || 'Payment failed'
    });
  }
}
