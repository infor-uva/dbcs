import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BookingComponent } from './booking.component';
import { BookingService } from '../shared/booking.service';
import { of } from 'rxjs';

class MockBookingService {
  createBooking() {
    return of({}); // Simula una respuesta exitosa
  }
}

describe('BookingComponent', () => {
  let component: BookingComponent;
  let fixture: ComponentFixture<BookingComponent>;
  let bookingService: BookingService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BookingComponent],
      imports: [ReactiveFormsModule],
      providers: [{ provide: BookingService, useClass: MockBookingService }],
    }).compileComponents();

    fixture = TestBed.createComponent(BookingComponent);
    component = fixture.componentInstance;
    bookingService = TestBed.inject(BookingService);
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have a valid form when all fields are filled', () => {
    component.bookingForm.controls['userId'].setValue(1);
    component.bookingForm.controls['hotelId'].setValue(1);
    component.bookingForm.controls['roomType'].setValue('single');
    component.bookingForm.controls['startDate'].setValue('2024-10-01');
    component.bookingForm.controls['endDate'].setValue('2024-10-05');

    expect(component.bookingForm.valid).toBeTrue();
  });

  it('should submit booking when form is valid', () => {
    spyOn(bookingService, 'createBooking').and.callThrough();
    component.bookingForm.controls['userId'].setValue(1);
    component.bookingForm.controls['hotelId'].setValue(1);
    component.bookingForm.controls['roomType'].setValue('single');
    component.bookingForm.controls['startDate'].setValue('2024-10-01');
    component.bookingForm.controls['endDate'].setValue('2024-10-05');

    component.submitBooking();
    expect(bookingService.createBooking).toHaveBeenCalled();
  });
});
