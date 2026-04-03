import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoomService } from '../../core/services/room.service';

@Component({
  selector: 'app-room-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './room-form.component.html'
})
export class RoomFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  roomId?: number;
  hotelId?: number;
  loading = false;
  error = '';
  roomTypes = ['SINGLE', 'DOUBLE', 'DELUXE'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private roomService: RoomService
  ) {
    this.form = this.fb.group({
      type: ['SINGLE', Validators.required],
      pricePerNight: ['', [Validators.required, Validators.min(1)]],
      availableCount: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.hotelId = Number(this.route.snapshot.paramMap.get('hotelId')) || undefined;
    this.roomId = Number(this.route.snapshot.paramMap.get('roomId')) || undefined;
    this.isEdit = !!this.roomId;

    if (this.isEdit && this.roomId) {
      this.roomService.getRoom(this.roomId).subscribe({
        next: room => {
          this.hotelId = room.hotelId;
          this.form.patchValue(room);
        },
        error: () => this.error = 'Room not found'
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const obs = this.isEdit && this.roomId
      ? this.roomService.updateRoom(this.roomId, this.form.value)
      : this.roomService.createRoom(this.hotelId!, this.form.value);

    obs.subscribe({
      next: room => this.router.navigate(['/hotels', room.hotelId]),
      error: err => {
        this.error = err.error?.message || 'Operation failed';
        this.loading = false;
      }
    });
  }
}
