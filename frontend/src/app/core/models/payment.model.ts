export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';

export interface Payment {
  id: number;
  bookingId: number;
  status: PaymentStatus;
  amount: number;
}
