import { Component, OnInit } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';

import { BookingService } from '../../../../shared/booking.service'; // Asegúrate de que el servicio exista
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from '../../../../../types';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
})
export class BookingComponent implements OnInit {
  bookingForm: FormGroup;
  roomId: number = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private bookingService: BookingService
  ) {
    // Inicialización del formulario con validaciones
    this.bookingForm = this.fb.group({
      userId: ['', Validators.required],
      roomId: ['', Validators.required],
      roomType: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.roomId = params['roomId'];
    });
  }

  submitBooking() {
    if (this.bookingForm.valid) {
      const formValue = this.bookingForm.value;
      const { userId } = formValue;
      const bookingRequest: Booking = {
        ...formValue,
        userId: { id: userId },
        roomId: { id: formValue.roomId },
      };
      console.warn(bookingRequest);

      // Llama al servicio para crear una nueva reserva
      this.bookingService.createBooking(bookingRequest).subscribe(
        (response) => {
          console.log('Reserva creada con éxito', response);
          // Llama al servicio para actualizar el estado del usuario
          this.bookingService.createBooking(bookingRequest).subscribe({
            next: (response) => {
              console.log('Reserva creada con éxito', response);
            },
            error: (error) => {
              console.error('Error al crear la reserva', error);
            },
          });
          this.router.navigate(['/user', userId, 'bookings']);
        },
        (error) => {
          console.error('Error al crear la reserva', error);
          // Manejo de errores
        }
      );
    } else {
      console.warn('El formulario no es válido');
      // Puedes mostrar un mensaje al usuario sobre la validez del formulario
    }
  }
}
