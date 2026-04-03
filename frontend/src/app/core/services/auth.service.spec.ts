import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store token', () => {
    const mockResponse = { token: 'mock-token', email: 'user@test.com', role: 'USER', name: 'Alice' };
    service.login({ email: 'user@test.com', password: 'pass' }).subscribe(res => {
      expect(res.token).toBe('mock-token');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
    expect(service.isLoggedIn).toBeTruthy();
  });

  it('should register and store token', () => {
    const mockResponse = { token: 'new-token', email: 'new@test.com', role: 'USER', name: 'Bob' };
    service.register({ name: 'Bob', email: 'new@test.com', password: 'pass123' }).subscribe(res => {
      expect(res.token).toBe('new-token');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');
    req.flush(mockResponse);
  });

  it('should logout and clear token', () => {
    spyOn(localStorage, 'removeItem');
    service.logout();
    expect(service.isLoggedIn).toBeFalsy();
  });

  it('isAdmin should return true when role is ADMIN', () => {
    spyOn(service as any, 'getStoredUser').and.returnValue({
      token: 'tok', email: 'a@b.com', role: 'ADMIN', name: 'Admin'
    });
    const svc = new AuthService(null as any, null as any);
    expect(svc.isAdmin).toBeTruthy();
  });
});
