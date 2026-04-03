export interface Hotel {
  id: number;
  name: string;
  location: string;
  description: string;
  averageRating?: number;
}

export interface HotelRequest {
  name: string;
  location: string;
  description: string;
}
