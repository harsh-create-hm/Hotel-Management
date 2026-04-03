import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HotelService } from './hotel.service';
import { Hotel } from '../models/hotel.model';

describe('HotelService', () => {
  let service: HotelService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HotelService]
    });
    service = TestBed.inject(HotelService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all hotels', () => {
    const mockHotels: Hotel[] = [
      { id: 1, name: 'Grand Hotel', location: 'NY', description: 'Nice' }
    ];
    service.getHotels().subscribe(hotels => {
      expect(hotels.length).toBe(1);
      expect(hotels[0].name).toBe('Grand Hotel');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/hotels?');
    expect(req.request.method).toBe('GET');
    req.flush(mockHotels);
  });

  it('should search hotels by location', () => {
    const mockHotels: Hotel[] = [
      { id: 1, name: 'NYC Hotel', location: 'New York', description: '' }
    ];
    service.getHotels('New York').subscribe(hotels => {
      expect(hotels.length).toBe(1);
    });
    const req = httpMock.expectOne('http://localhost:8080/api/hotels?location=New+York');
    req.flush(mockHotels);
  });

  it('should get hotel by id', () => {
    const mockHotel: Hotel = { id: 1, name: 'Grand Hotel', location: 'NY', description: 'Nice' };
    service.getHotel(1).subscribe(hotel => {
      expect(hotel.id).toBe(1);
    });
    const req = httpMock.expectOne('http://localhost:8080/api/hotels/1');
    req.flush(mockHotel);
  });

  it('should create hotel', () => {
    const mockHotel: Hotel = { id: 2, name: 'New Hotel', location: 'LA', description: 'New' };
    service.createHotel({ name: 'New Hotel', location: 'LA', description: 'New' }).subscribe(hotel => {
      expect(hotel.id).toBe(2);
    });
    const req = httpMock.expectOne('http://localhost:8080/api/hotels');
    expect(req.request.method).toBe('POST');
    req.flush(mockHotel);
  });

  it('should delete hotel', () => {
    service.deleteHotel(1).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/hotels/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
