import { Component, OnInit } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { Booking, User } from '../../../../types';
import { LocalStorageService } from '../../../../shared/local-storage.service';
import { BookingClientService } from '../../../../shared/booking-client.service';
import { UserClientService } from '../../../../shared/user-client.service';
import { SessionService } from '../../../../shared/session.service';

type communication = {
  roomId: number;
  startDate: Date;
  endDate: Date;
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
  bookingLocal: { roomId: number; startDate: Date; endDate: Date } = {
    roomId: 0,
    endDate: new Date(),
    startDate: new Date(),
  };
  roomId: number = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private sessionService: SessionService,
    private bookingClient: BookingClientService,
    private userClient: UserClientService,
    private storage: LocalStorageService
  ) {
    // Inicialización del formulario con validaciones
    this.bookingForm = this.fb.group({
      roomId: [{ value: '', disabled: true }, Validators.required],
      startDate: [{ value: '', disabled: true }, Validators.required],
      endDate: [{ value: '', disabled: true }, Validators.required],
    });
    const localBooking = storage.read<communication | null>('booking-data');
    if (localBooking === null) {
      this.router.navigate(['/booking', 'search']);
      return;
    }
    this.bookingLocal = localBooking!;
    this.route.queryParams.subscribe((params) => {
      const roomId = Number(params['roomId']);
      this.roomId = roomId;
      if (this.bookingLocal.roomId !== roomId) {
        this.router.navigate(['/bookings', 'search']);
        return;
      }
      this.bookingLocal = {
        ...this.bookingLocal,
        startDate: new Date(this.bookingLocal.startDate),
        endDate: new Date(this.bookingLocal.endDate),
      };
      this.loadBooking();
    });
    this.sessionService.getSession().subscribe({
      next: (session) => {
        if (session) this.user = session;
      },
    });
  }

  loadBooking() {
    const booking = this.bookingLocal;
    if (!booking) return;
    const start = new Date(booking.startDate).toISOString();
    const end = new Date(booking.endDate).toISOString();
    this.bookingForm = this.fb.group({
      roomId: [{ value: booking.roomId, disabled: true }, Validators.required],
      startDate: [{ value: start, disabled: true }, Validators.required],
      endDate: [{ value: end, disabled: true }, Validators.required],
    });
  }

  submitBooking() {
    const { id } = this.user;
    const bookingRequest: any = {
      ...this.bookingLocal,
      userId: { id },
      roomId: { id: this.roomId },
    };

    // Llama al servicio para crear una nueva reserva
    this.bookingClient.createBooking(bookingRequest).subscribe({
      next: (response) => {
        console.log('Reserva creada con éxito', response);
        // Llama al servicio para actualizar el estado del usuario
        this.userClient.alterUserStatus(id, 'WITH_ACTIVE_BOOKINGS').subscribe({
          next: (response) => {
            console.log('Estado de usuario actualizado con exito', response);
            this.storage.remove('booking-data');
            this.router.navigate(['/me', 'bookings']);
          },
          error: (error) => {
            console.error('Error al cambiar el estado del usuario', error);
          },
        });
      },
      error: (error) => {
        console.error('Error al crear la reserva', error);
        // Manejo de errores
      },
    });
  }
}
