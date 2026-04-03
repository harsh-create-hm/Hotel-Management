export type BookingStatus = 'CREATED' | 'CONFIRMED' | 'CANCELLED';

export interface Booking {
  id: number;
  userId: number;
  userName: string;
  roomId: number;
  roomType: string;
  hotelId: number;
  hotelName: string;
  checkIn: string;
  checkOut: string;
  status: BookingStatus;
  totalAmount: number;
}

export interface BookingRequest {
  roomId: number;
  checkIn: string;
  checkOut: string;
}
