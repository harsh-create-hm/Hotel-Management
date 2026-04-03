import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Hotel, HotelRequest } from '../models/hotel.model';

@Injectable({ providedIn: 'root' })
export class HotelService {
  private apiUrl = `${environment.apiUrl}/hotels`;

  constructor(private http: HttpClient) {}

  getHotels(location?: string): Observable<Hotel[]> {
    let params = new HttpParams();
    if (location) {
      params = params.set('location', location);
    }
    return this.http.get<Hotel[]>(this.apiUrl, { params });
  }

  getHotel(id: number): Observable<Hotel> {
    return this.http.get<Hotel>(`${this.apiUrl}/${id}`);
  }

  createHotel(request: HotelRequest): Observable<Hotel> {
    return this.http.post<Hotel>(this.apiUrl, request);
  }

  updateHotel(id: number, request: HotelRequest): Observable<Hotel> {
    return this.http.put<Hotel>(`${this.apiUrl}/${id}`, request);
  }

  deleteHotel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
