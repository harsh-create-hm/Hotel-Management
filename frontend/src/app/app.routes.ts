import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'hotels', pathMatch: 'full' },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./auth/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: 'hotels',
    children: [
      {
        path: '',
        loadComponent: () => import('./hotels/hotel-list/hotel-list.component').then(m => m.HotelListComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./hotels/hotel-detail/hotel-detail.component').then(m => m.HotelDetailComponent)
      }
    ]
  },
  {
    path: 'bookings',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./bookings/booking-list/booking-list.component').then(m => m.BookingListComponent)
      },
      {
        path: 'new',
        loadComponent: () => import('./bookings/booking-form/booking-form.component').then(m => m.BookingFormComponent)
      }
    ]
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    children: [
      {
        path: 'hotels/new',
        loadComponent: () => import('./admin/hotel-form/hotel-form.component').then(m => m.HotelFormComponent)
      },
      {
        path: 'hotels/:id/edit',
        loadComponent: () => import('./admin/hotel-form/hotel-form.component').then(m => m.HotelFormComponent)
      },
      {
        path: 'hotels/:hotelId/rooms/new',
        loadComponent: () => import('./admin/room-form/room-form.component').then(m => m.RoomFormComponent)
      },
      {
        path: 'rooms/:roomId/edit',
        loadComponent: () => import('./admin/room-form/room-form.component').then(m => m.RoomFormComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'hotels' }
];
