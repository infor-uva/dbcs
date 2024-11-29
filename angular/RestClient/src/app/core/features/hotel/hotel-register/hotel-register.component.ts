import { Component } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { CommonModule } from '@angular/common';
import { Address, Hotel, Room } from '../../../../types';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelClientService } from '../../../../shared/hotel-client.service';

const emptyRoom: Room = {
  id: 0,
  roomNumber: '',
  type: 'SINGLE',
  available: false,
};
const emptyHotel: Hotel = {
  id: 0,
  name: '',
  rooms: [emptyRoom],
  address: {
    id: 0,
    number: 0,
    streetKind: '',
    postCode: '',
    streetName: '',
  },
};

@Component({
  selector: 'app-hotel-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
  ],
  templateUrl: './hotel-register.component.html',
  styleUrl: './hotel-register.component.css',
})
export class HotelRegisterComponent {
  editMode: boolean;
  hotelForm: FormGroup;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private hotelClient: HotelClientService
  ) {
    this.hotelForm = this.setHotelForm();
    this.editMode = false;
    this.route.paramMap.subscribe({
      next: (params) => {
        const id = Number(params.get('id'));
        this.editMode = id !== 0;
        if (this.editMode) {
          this.hotelClient.getHotel(id).subscribe({
            next: (h) => this.setHotelForm(h),
            error: (error) => {
              this.router.navigate(['/hotels/new']);
            },
          });
        }
      },
    });
  }

  get rooms(): FormArray {
    return this.hotelForm.get('rooms') as FormArray;
  }

  // Agregar una nueva habitación
  addRoom(): void {
    const roomForm = this.fb.group({
      roomNumber: [emptyRoom.roomNumber, Validators.required],
      type: [emptyRoom.type, Validators.required],
      available: [emptyRoom.available],
    });
    this.rooms.push(roomForm);
  }

  // Eliminar habitación
  removeRoom(index: number): void {
    this.rooms.removeAt(index);
  }

  // Método de envío del formulario
  onSubmit(): void {
    if (this.hotelForm.valid) {
      const hotel = this.hotelForm.value as Hotel;
      this.hotelClient.addHotel(hotel).subscribe({
        next: (resp) => {
          if (resp.status < 400) {
            alert('Hotel guardado correctamente');
            this.router.navigate(['/hotels']);
          } else {
          }
        },
        error: (err) => {
          console.log('Error al borrar: ' + err.message);
          throw err;
        },
      });
    }
  }

  setHotelForm({
    name,
    address,
    rooms,
  }: {
    name: String;
    address: Address;
    rooms: Room[];
  } = emptyHotel) {
    const form = this.fb.group({
      name: [name, Validators.required],
      address: this.fb.group({
        streetKind: [address.streetKind, Validators.required],
        streetName: [address.streetName, Validators.required],
        number: [address.number, [Validators.required, Validators.min(1)]],
        postCode: [address.postCode, Validators.required],
        otherInfo: address.otherInfo,
      }),
      rooms: this.fb.array(
        rooms.map(
          (room) =>
            this.fb.group({
              roomNumber: [room.roomNumber, Validators.required],
              type: [room.type, Validators.required],
              available: [room.available],
            }),
          Validators.required
        )
      ),
    });
    if (this.editMode) form.disable();
    this.hotelForm = form;
    return form;
  }
}
