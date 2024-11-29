import { Component } from '@angular/core';

import { Booking, User } from '../../../../types';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserClientService } from '../../../../shared/user-client.service';
import { BookingClientService } from '../../../../shared/booking-client.service';

type state = 'all' | 'active' | 'inactive';

@Component({
  selector: 'app-user-booking-list',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './user-booking-list.component.html',
  styleUrl: './user-booking-list.component.css',
})
export class UserBookingListComponent {
  selectedState: state = 'all';
  search = false;
  bookings: Booking[] = [];
  userId: number = 0;
  user?: User;

  constructor(
    private userClient: UserClientService,
    private bookingClient: BookingClientService,
    private route: ActivatedRoute
  ) {
    this.route.paramMap.subscribe({
      next: (params) => {
        this.userId = Number(params.get('id'));
        this.updateBookings();
      },
    });
    this.userClient
      .getUser(this.userId)
      .subscribe({ next: (user) => (this.user = user) });
  }

  ngOnInit() {
    this.updateBookings();
  }

  updateBookings() {
    this.bookingClient.getBookingsByUser(this.userId).subscribe({
      next: (bookings) => {
        this.search = true;
        switch (this.selectedState) {
          case 'all':
            this.bookings = bookings;
            break;
          case 'active':
            this.bookings = bookings.filter(
              (booking) => this.genBookingState(booking) === 'Reserva activa'
            );
            break;
          case 'inactive':
            this.bookings = bookings.filter(
              (booking) => this.genBookingState(booking) === 'Reserva inactiva'
            );
            break;
        }
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  genBookingState(booking: Booking) {
    return new Date(booking.endDate).getTime() < Date.now()
      ? 'Reserva inactiva'
      : 'Reserva activa';
  }

  deleteBooking(bookingId: number) {
    this.bookingClient.deleteBooking(bookingId).subscribe({
      next: () => {
        this.updateBookings();
        this.updateUserStatus();
      },
      error: (err) => {
        console.error('Error al eliminar una reserva', err);
      },
    });
  }

  updateUserStatus() {
    this.bookingClient.getBookingsByUser(this.userId).subscribe({
      next: (bookings) => {
        const withActive = bookings.find(
          (booking) => this.genBookingState(booking) === 'Reserva activa'
        );
        const withInactive = bookings.find(
          (booking) => this.genBookingState(booking) === 'Reserva inactiva'
        );
        if (withActive) {
          this.userClient
            .alterUserStatus(this.userId, 'WITH_ACTIVE_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log('Cambio de estado en el usuario a activo correcto');
              },
              error: (err) => {
                console.error('Error al cambiar de estado al usuario a activo');
              },
            });
        } else if (withInactive) {
          this.userClient
            .alterUserStatus(this.userId, 'WITH_INACTIVE_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log(
                  'Cambio de estado en el usuario a inactivo correcto'
                );
              },
              error: (err) => {
                console.error(
                  'Error al cambiar de estado al usuario a inactivo'
                );
              },
            });
        } else {
          this.userClient
            .alterUserStatus(this.userId, 'NO_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log(
                  'Cambio de estado en el usuario a sin reservas correcto'
                );
              },
              error: (err) => {
                console.error(
                  'Error al cambiar de estado al usuario sin reservas'
                );
              },
            });
        }
      },
    });
  }
}
