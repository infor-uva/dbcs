import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
interface BookingRequest {
  userId: number;      // ID del usuario que realiza la reserva
  hotelId: number;     // ID del hotel en el que se realiza la reserva
  roomType: string;    // Tipo de habitación (single, double, suite)
  startDate: string;   // Fecha de inicio de la reserva
  endDate: string;     // Fecha de fin de la reserva// Asegúrate de ajustar la ruta
}
import { BookingService } from '../booking.service'; // Asegúrate de que el servicio exista

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css']
})
export class BookingComponent implements OnInit {
  bookingForm: FormGroup;

  constructor(private fb: FormBuilder, private bookingService: BookingService) {
    // Inicialización del formulario con validaciones
    this.bookingForm = this.fb.group({
      userId: ['', Validators.required],
      hotelId: ['', Validators.required],
      roomType: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {}

  submitBooking() {
    if (this.bookingForm.valid) {
      const bookingRequest: BookingRequest = this.bookingForm.value;

      // Llama al servicio para crear una nueva reserva
      this.bookingService.createBooking(bookingRequest).subscribe(
        response => {
          console.log('Reserva creada con éxito', response);
          // Aquí puedes redirigir al usuario o mostrar un mensaje de éxito
          this.bookingForm.reset(); // Opcional: resetea el formulario después de una reserva exitosa
        },
        error => {
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
