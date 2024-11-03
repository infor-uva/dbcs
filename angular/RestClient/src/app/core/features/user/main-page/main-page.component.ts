// main-page.component.ts
import { Component, OnInit } from '@angular/core';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';
import { User } from '../../../../../types';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import users from '../../../../../mocks/users.json';
@Component({
  standalone: true,
  imports: [FormsModule, CommonModule],
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],
})
export class MainPageComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  selectedStatus: string = 'all';

  constructor(private ClienteApiRestService: ClienteApiRestService) {}

  ngOnInit(): void {
    this.users = users as unknown as User[];
    this.ClienteApiRestService.getAllUsers().subscribe((data: User[]) => {
      this.users = data;
      this.filteredUsers = data; // Inicialmente, muestra todos los usuarios
    });
  }

  filterUsers(): void {
    if (this.selectedStatus === 'all') {
      this.filteredUsers = this.users;
    } else {
      this.filteredUsers = this.users.filter(
        (user) => user.status === this.selectedStatus
      );
    }
  }
}
