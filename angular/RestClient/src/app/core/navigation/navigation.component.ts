import { OnInit, Component, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { User, UserRol } from '../../types';
import { AuthInstance, SessionService } from '../../shared/session.service';
import { UserClientService } from '../../shared/user-client.service';
import { AuthClientService } from '../../shared/auth-client.service';

var comp_id = 0;

export function genId() {
  const comp = 'navigation';
  return `${comp}-${comp_id++}`;
}

interface Section {
  id: string;
  icon: string;
  text: string;
  link: string;
  allowRoles?: UserRol[];
}

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [RouterModule, MatButtonModule, MatMenuModule, MatIconModule],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css',
})
export class NavigationComponent implements OnInit {
  @ViewChild(MatMenuTrigger)
  trigger?: MatMenuTrigger;
  isLogged = false;
  user: AuthInstance = {
    id: 0,
    name: '',
    email: '',
    rol: 'CLIENT',
  };
  sections: Section[] = [];

  constructor(
    private sessionService: SessionService,
    private auth: AuthClientService
  ) {}

  ngOnInit() {
    this.loadUser();
  }

  loadUser() {
    const isLogged = this.sessionService.isValid();
    if (isLogged) {
      this.user = this.sessionService.getSession();
      console.log({ user: this.user });
      this.sections = this.genSections();
    }
    this.isLogged = isLogged;
  }

  toggleDropdown() {
    if (this.trigger) {
      if (this.trigger.menuOpen) this.trigger.closeMenu();
      else this.trigger.openMenu();
    }
  }

  schemaSections: Section[] = [
    {
      id: genId(),
      icon: 'person',
      text: 'Información personal',
      link: '/me',
    },
    {
      id: genId(),
      icon: 'calendar_today',
      text: 'Reservas',
      allowRoles: ['CLIENT'],
      link: '/users/1/bookings',
      // link: '/bookings',
    },
    {
      id: genId(),
      icon: 'hotel',
      text: 'Hoteles',
      allowRoles: ['HOTEL_ADMIN'],
      link: '/hotels',
    },
  ];

  genSections() {
    return this.schemaSections.filter(
      (section) =>
        this.user.rol === 'ADMIN' || // Es administrador del sistema
        !section.allowRoles ||
        section.allowRoles.length === 0 || // No tiene limitación
        section.allowRoles.includes(this.user.rol) // El rol del usuario es aceptado
    );
  }

  login() {
    const email = 'client@dev.com';
    this.auth.login(email, 'NQSASorry').subscribe({
      next: (data: any) => {
        console.log(email, data);
        this.sessionService.login(data);
        this.loadUser();
      },
    });
  }

  logout() {
    if (confirm('You are trying to logout')) {
      this.sessionService.logout();
      this.loadUser();
    }
  }
}
