export type RoomType = 'SINGLE' | 'DOUBLE' | 'DELUXE';

export interface Room {
  id: number;
  hotelId: number;
  hotelName: string;
  type: RoomType;
  pricePerNight: number;
  availableCount: number;
}

export interface RoomRequest {
  type: RoomType;
  pricePerNight: number;
  availableCount: number;
}
