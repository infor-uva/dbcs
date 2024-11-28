import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthClientService } from '../../shared/auth-client.service';
@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css',
})
export class NavigationComponent {
  isLogged = true;
  userName = 'User';
  dropdownOpen = false;

  constructor(private authClient: AuthClientService) {}

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
    this.authClient.login('migudel@dev.com', 'NQSASorry').subscribe({
      next: (response) => {
        console.log(response);
        alert('OKEY! You are logged');
      },
      error: (error) => {
        console.error(error);
        alert('Error! You are not logged');
      },
    });
  }
}
