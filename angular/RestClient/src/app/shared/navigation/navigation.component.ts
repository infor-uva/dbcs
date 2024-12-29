import { OnInit, Component, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { Session } from '@core/models/Session.interface';
import { UserRol } from '@features/users';
import { SessionService } from '@core/services';

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
  user: Session = {
    id: 0,
    name: '',
    email: '',
    rol: 'CLIENT',
  };
  sections: Section[] = [];

  constructor(private sessionService: SessionService) {}

  ngOnInit() {
    this.loadUser();
  }

  loadUser() {
    this.sessionService.getSession().subscribe({
      next: (session) => {
        if (session) {
          this.user = session;
          this.isLogged = true;
          this.sections = this.genSections();
        } else {
          this.isLogged = false;
        }
      },
    });
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
      link: '/me/bookings',
    },
    {
      id: genId(),
      icon: 'hotel',
      text: 'Hoteles',
      allowRoles: ['MANAGER'],
      link: '/me/hotels',
    },
    {
      id: genId(),
      icon: 'fiber_new',
      text: 'Registrar hotel',
      allowRoles: ['MANAGER'],
      link: '/hotels/register',
    },
    {
      id: genId(),
      icon: 'settings',
      text: 'Admin Zone',
      allowRoles: ['ADMIN'],
      link: '/admin',
    },
    {
      id: genId(),
      icon: 'group',
      text: 'Users',
      allowRoles: ['ADMIN'],
      link: '/admin/users',
    },
  ];

  genSections() {
    return this.schemaSections.filter(
      (section) =>
        !section.allowRoles ||
        section.allowRoles.length === 0 || // No tiene limitación
        section.allowRoles.includes(this.user.rol) // El rol del usuario es aceptado
    );
  }

  login() {}

  logout() {
    // if (confirm('You are trying to logout'))
    this.sessionService.logout();
    this.loadUser();
  }
}
