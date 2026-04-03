import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Hotel } from '../../core/models/hotel.model';
import { Room } from '../../core/models/room.model';
import { Review } from '../../core/models/review.model';
import { HotelService } from '../../core/services/hotel.service';
import { RoomService } from '../../core/services/room.service';
import { ReviewService } from '../../core/services/review.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-hotel-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './hotel-detail.component.html'
})
export class HotelDetailComponent implements OnInit {
  hotel?: Hotel;
  rooms: Room[] = [];
  reviews: Review[] = [];
  loading = false;
  reviewForm: FormGroup;
  reviewError = '';
  reviewSuccess = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hotelService: HotelService,
    private roomService: RoomService,
    private reviewService: ReviewService,
    public authService: AuthService,
    private fb: FormBuilder
  ) {
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['']
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadHotel(id);
    this.loadRooms(id);
    this.loadReviews(id);
  }

  loadHotel(id: number): void {
    this.loading = true;
    this.hotelService.getHotel(id).subscribe({
      next: hotel => { this.hotel = hotel; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadRooms(id: number): void {
    this.roomService.getRoomsByHotel(id).subscribe({
      next: rooms => this.rooms = rooms
    });
  }

  loadReviews(id: number): void {
    this.reviewService.getHotelReviews(id).subscribe({
      next: reviews => this.reviews = reviews
    });
  }

  bookRoom(roomId: number): void {
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.router.navigate(['/bookings/new'], { queryParams: { roomId } });
  }

  submitReview(): void {
    if (!this.hotel || this.reviewForm.invalid) return;
    this.reviewService.createReview({
      hotelId: this.hotel.id,
      ...this.reviewForm.value
    }).subscribe({
      next: () => {
        this.reviewSuccess = 'Review submitted!';
        this.reviewForm.reset({ rating: 5 });
        this.loadReviews(this.hotel!.id);
      },
      error: err => this.reviewError = err.error?.message || 'Failed to submit review'
    });
  }
}
