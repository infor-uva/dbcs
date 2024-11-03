import { Component, OnInit } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';

import { BookingService } from '../../../../shared/booking.service'; // Asegúrate de que el servicio exista
import { ActivatedRoute, Router } from '@angular/router';
import { Booking, User } from '../../../../../types';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
})
export class BookingComponent implements OnInit {
  users: User[] = [];
  bookingForm: FormGroup;
  bookingLocal: { roomId: number; startDate: Date; endDate: Date };
  roomId: number = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private bookingService: BookingService,
    private client: ClienteApiRestService
  ) {
    // Inicialización del formulario con validaciones
    this.bookingForm = this.fb.group({
      userId: ['', Validators.required],
      roomId: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });
    const localBookingStr = localStorage.getItem('booking-data');
    if (localBookingStr === null) {
      this.router.navigate(['/booking', 'search']);
    }
    const localBooking = JSON.parse(localBookingStr!);
    this.bookingLocal = localBooking;
    this.route.queryParams.subscribe((params) => {
      const roomId = Number(params['roomId']);
      this.roomId = roomId;
      if (localBooking.roomId !== roomId) {
        this.router.navigate(['/bookings', 'search']);
        this.loadBooking(localBooking);
      }
    });
    this.client.getAllUsers().subscribe({
      next: (resp) => {
        this.users = resp;
      },
    });
  }

  get userId() {
    return this.bookingForm.get('userId')!.value;
  }
  get isUserSelected() {
    return !isNaN(this.userId);
  }

  ngOnInit() {
    this.loadBooking(this.bookingLocal);
  }

  loadBooking(booking: { roomId: number; startDate: Date; endDate: Date }) {
    const start = new Date(booking.startDate).toISOString().split('T')[0];
    const end = new Date(booking.endDate).toISOString().split('T')[0];
    this.bookingForm = this.fb.group({
      userId: [Validators.required],
      roomId: [booking.roomId, Validators.required],
      startDate: [start, Validators.required],
      endDate: [end, Validators.required],
    });
    this.bookingForm.get('roomId')?.disable();
    this.bookingForm.get('startDate')?.disable();
    this.bookingForm.get('endDate')?.disable();
  }

  submitBooking() {
    if (this.bookingForm.valid) {
      const formValue = this.bookingForm.value;
      const userId = Number(formValue.userId);
      const bookingRequest: any = {
        ...this.bookingLocal,
        userId: { id: userId },
        roomId: { id: this.roomId },
      };

      // Llama al servicio para crear una nueva reserva
      this.bookingService.createBooking(bookingRequest).subscribe({
        next: (response) => {
          console.log('Reserva creada con éxito', response);
          // Llama al servicio para actualizar el estado del usuario
          this.client
            .alterUserStatus(userId, 'WITH_ACTIVE_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log(
                  'Estado de usuario actualizado con exito',
                  response
                );
                localStorage.removeItem('booking-data');
                this.router.navigate(['/user', userId, 'bookings']);
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
    } else {
      console.warn('El formulario no es válido');
      // Puedes mostrar un mensaje al usuario sobre la validez del formulario
    }
  }
}
