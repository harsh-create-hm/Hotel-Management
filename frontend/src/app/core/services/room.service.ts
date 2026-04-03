import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Room, RoomRequest } from '../models/room.model';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getRoomsByHotel(hotelId: number, availableOnly = false): Observable<Room[]> {
    const params = new HttpParams().set('availableOnly', availableOnly.toString());
    return this.http.get<Room[]>(`${this.apiUrl}/hotels/${hotelId}/rooms`, { params });
  }

  getRoom(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.apiUrl}/rooms/${id}`);
  }

  createRoom(hotelId: number, request: RoomRequest): Observable<Room> {
    return this.http.post<Room>(`${this.apiUrl}/hotels/${hotelId}/rooms`, request);
  }

  updateRoom(id: number, request: RoomRequest): Observable<Room> {
    return this.http.put<Room>(`${this.apiUrl}/rooms/${id}`, request);
  }

  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/rooms/${id}`);
  }
}
