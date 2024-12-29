import { Component, OnInit } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from '@features/bookings';
import { User } from '@features/users';
import { LocalStorageService } from '../../../core/services/storage/local-storage.service';
import { BookingClientService } from '../../../core/services/api/bookings/booking-client.service';
import { UserClientService } from '../../../core/services/api/users/user-client.service';
import { SessionService } from '../../../core/services/session/session.service';

type communication = {
  roomId: number;
  start: Date;
  end: Date;
  hotelId: number;
  managerId: number;
};

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
})
export class BookingComponent {
  user: User = { id: 0, email: '', name: '', rol: 'CLIENT' };
  bookingForm: FormGroup;
  bookingLocal: {
    roomId: number;
    start: Date;
    end: Date;
    hotelId: number;
    managerId: number;
  } = {
    roomId: 0,
    end: new Date(),
    start: new Date(),
    hotelId: 0,
    managerId: 0,
  };

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private sessionService: SessionService,
    private bookingClient: BookingClientService,
    private storage: LocalStorageService
  ) {
    // Inicialización del formulario con validaciones
    this.bookingForm = this.fb.group({
      roomId: [{ value: '', disabled: true }, Validators.required],
      start: [{ value: '', disabled: true }, Validators.required],
      end: [{ value: '', disabled: true }, Validators.required],
    });
    const localBooking = storage.read<communication | null>('booking-data');
    if (localBooking === null) {
      this.router.navigate(['/booking', 'search']);
      return;
    }
    console.log({ localBooking });

    this.bookingLocal = localBooking!;
    this.route.queryParams.subscribe((params) => {
      const roomId = Number(params['roomId']);
      if (this.bookingLocal.roomId !== roomId) {
        this.router.navigate(['/bookings', 'search']);
        return;
      }
      this.bookingLocal = {
        ...this.bookingLocal,
        start: new Date(this.bookingLocal.start),
        end: new Date(this.bookingLocal.end),
      };
      this.loadBooking();
    });
    this.sessionService.getSession().subscribe({
      next: (session) => {
        if (session) this.user = session;
      },
    });
  }

  private formatDate(date: Date) {
    console.log(date);
    return date.toISOString().split('T')[0].split('-').reverse().join('-');
  }

  loadBooking() {
    const booking = this.bookingLocal;
    if (!booking) return;
    const start = this.formatDate(booking.start);
    const end = this.formatDate(booking.end);
    console.log({ start, end });

    this.bookingForm = this.fb.group({
      roomId: [{ value: booking.roomId, disabled: true }, Validators.required],
      start: [{ value: start, disabled: true }, Validators.required],
      end: [{ value: end, disabled: true }, Validators.required],
    });
  }

  submitBooking() {
    const { id: userId } = this.user;
    const bookingRequest: any = {
      ...this.bookingLocal,
      userId,
    };

    // Llama al servicio para crear una nueva reserva
    console.log(bookingRequest);

    this.bookingClient.createBooking(bookingRequest).subscribe({
      next: (response) => {
        console.log('Reserva creada con éxito', response);
        this.storage.remove('booking-data');
        this.router.navigate(['/me', 'bookings']);
      },
      error: (error) => {
        console.error('Error al crear la reserva', error);
        // Manejo de errores
      },
    });
  }
}
