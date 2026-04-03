import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../core/services/hotel.service';

@Component({
  selector: 'app-hotel-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './hotel-form.component.html'
})
export class HotelFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  hotelId?: number;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private hotelService: HotelService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.hotelId = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    this.isEdit = !!this.hotelId;

    if (this.isEdit && this.hotelId) {
      this.hotelService.getHotel(this.hotelId).subscribe({
        next: hotel => this.form.patchValue(hotel),
        error: () => this.error = 'Hotel not found'
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const obs = this.isEdit && this.hotelId
      ? this.hotelService.updateHotel(this.hotelId, this.form.value)
      : this.hotelService.createHotel(this.form.value);

    obs.subscribe({
      next: () => this.router.navigate(['/hotels']),
      error: err => {
        this.error = err.error?.message || 'Operation failed';
        this.loading = false;
      }
    });
  }
}
