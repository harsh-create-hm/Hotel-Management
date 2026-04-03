import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Hotel } from '../../core/models/hotel.model';
import { HotelService } from '../../core/services/hotel.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-hotel-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './hotel-list.component.html'
})
export class HotelListComponent implements OnInit {
  hotels: Hotel[] = [];
  searchLocation = '';
  loading = false;
  error = '';

  constructor(
    private hotelService: HotelService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHotels();
  }

  loadHotels(): void {
    this.loading = true;
    this.hotelService.getHotels(this.searchLocation || undefined).subscribe({
      next: hotels => {
        this.hotels = hotels;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load hotels';
        this.loading = false;
      }
    });
  }

  search(): void {
    this.loadHotels();
  }

  viewHotel(id: number): void {
    this.router.navigate(['/hotels', id]);
  }
}
