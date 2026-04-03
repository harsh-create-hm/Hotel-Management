export interface Review {
  id: number;
  userId: number;
  userName: string;
  hotelId: number;
  hotelName: string;
  rating: number;
  comment: string;
}

export interface ReviewRequest {
  hotelId: number;
  rating: number;
  comment: string;
}
