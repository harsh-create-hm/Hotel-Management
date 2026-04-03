import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../core/services/hotel.service';
import { RoomService } from '../../core/services/room.service';
import { BookingService } from '../../core/services/booking.service';
import { Room } from '../../core/models/room.model';

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './booking-form.component.html'
})
export class BookingFormComponent implements OnInit {
  form: FormGroup;
  room?: Room;
  loading = false;
  error = '';
  success = '';
  totalAmount = 0;
  today = new Date().toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private roomService: RoomService,
    private bookingService: BookingService
  ) {
    this.form = this.fb.group({
      checkIn: ['', Validators.required],
      checkOut: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const roomId = Number(this.route.snapshot.queryParamMap.get('roomId'));
    if (roomId) {
      this.roomService.getRoom(roomId).subscribe({
        next: room => this.room = room,
        error: () => this.error = 'Room not found'
      });
    }

    this.form.valueChanges.subscribe(() => this.calculateTotal());
  }

  calculateTotal(): void {
    if (!this.room) return;
    const checkIn = new Date(this.form.value.checkIn);
    const checkOut = new Date(this.form.value.checkOut);
    if (checkIn && checkOut && checkOut > checkIn) {
      const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24));
      this.totalAmount = nights * this.room.pricePerNight;
    } else {
      this.totalAmount = 0;
    }
  }

  submit(): void {
    if (this.form.invalid || !this.room) return;
    this.loading = true;
    this.error = '';
    this.bookingService.createBooking({
      roomId: this.room.id,
      checkIn: this.form.value.checkIn,
      checkOut: this.form.value.checkOut
    }).subscribe({
      next: booking => {
        this.success = `Booking #${booking.id} created! Status: ${booking.status}`;
        this.loading = false;
        setTimeout(() => this.router.navigate(['/bookings']), 2000);
      },
      error: err => {
        this.error = err.error?.message || 'Booking failed';
        this.loading = false;
      }
    });
  }
}
