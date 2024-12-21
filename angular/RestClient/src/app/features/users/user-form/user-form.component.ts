import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule,
} from '@angular/forms';
import { UserClientService } from '../../../core/services/api/users/user-client.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SessionService } from '../../../core/services/session/session.service';
import { Session, UserRol, UserRolesArray } from '../../../core/models';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import { getBasePath } from '../../../utils/utils';
import { environment } from '../../../../environments/environment';

type EditMode =
  | 'Login'
  | 'Register'
  | 'ViewUser'
  | 'EditUser'
  | 'ChangePassword'
  | 'Other';
const defaultUser: Session = {
  id: 0,
  name: 'test',
  email: 'test@dev.com',
  rol: 'ADMIN',
};

@Component({
  standalone: true,
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatSlideToggleModule,
    MatCardModule,
    MatInputModule,
    MatSelectModule,
    MatFormFieldModule,
  ],
})
export class UserFormComponent implements OnInit {
  userForm!: FormGroup;
  rolOptions: UserRol[] = UserRolesArray;
  mode: EditMode = 'Other';
  isMeRoute = false;

  /** is editing the user data */
  isEditing: boolean = false;
  /** is showing the user data */
  isViewUser: boolean = false;
  /** is trying to do an auth action*/
  isAuth: boolean = false;
  /** is trying to login */
  isLogin: boolean = false;
  /** is trying to register */
  isRegister: boolean = false;
  /** don't want to update the password */
  isChangePassword = false;

  titleText = 'Mis datos';
  submitButtonText = 'Submit';
  currentPasswordText = 'Contraseña actual';

  user = defaultUser;
  isHotelManager = false;
  isAdmin = false;

  constructor(
    private fb: FormBuilder,
    private sessionService: SessionService,
    private userService: UserClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  isEditRoute(urlSegments: any[], isMeRoute: boolean): boolean {
    return isMeRoute
      ? urlSegments.length >= 2 && urlSegments[1].path === 'edit'
      : urlSegments.length >= 4 && urlSegments[3].path === 'edit';
  }

  isChangePasswordRoute(urlSegments: any[], isMeRoute: boolean): boolean {
    return isMeRoute
      ? urlSegments.length >= 2 && urlSegments[1].path === 'change-passwd'
      : urlSegments.length >= 4 && urlSegments[3].path === 'change-passwd';
  }

  isViewUserRoute(urlSegments: any[], isMeRoute: boolean): boolean {
    return isMeRoute
      ? urlSegments.length === 1
      : (urlSegments.length === 1 && urlSegments[0].path === 'admin') ||
          urlSegments.length === 3;
  }

  isAuthRoute(urlSegments: any[], route: string): boolean {
    return urlSegments.length === 1 && urlSegments[0].path === route;
  }

  ngOnInit(): void {
    this.setUp();

    // const auth = this.session.getSession();
    // this.user = auth;
    // this.userForm.patchValue({
    //   name: this.user.name,
    //   email: this.user.email,
    // });
  }

  private initializeForm(): void {
    // Solicitar contraseña actual
    const confirmIdentity =
      !this.isViewUser &&
      (this.isChangePassword || this.isAuth) &&
      (this.isMeRoute || this.isAuth);
    // Solicitar nueva contraseña
    const isChangePassword = this.isChangePassword;
    // Solicitar confirmación de contraseña
    const confirmPassword =
      !this.isViewUser && (this.isChangePassword || this.isRegister);
    // Solicitar email
    const emailNotRequired = this.isViewUser || isChangePassword;
    // Solicitar nombre
    const nameNotRequired = emailNotRequired || this.isLogin;
    // Solicitar rol
    const rolNotRequired = !this.isRegister;
    // console.log({
    //   confirmIdentity,
    //   isChangePassword,
    //   confirmPassword,
    //   emailNotRequired,
    //   nameNotRequired,
    //   rolNotRequired,
    // });

    this.userForm = this.fb.group({
      name: [{ value: '', disabled: nameNotRequired }, Validators.required],
      email: [
        { value: '', disabled: emailNotRequired },
        [Validators.required, Validators.email],
      ],
      currentPassword: [
        { value: '', disabled: !confirmIdentity },
        Validators.required,
      ], // Solo habilitado en edición
      newPassword: [
        { value: '', disabled: !isChangePassword },
        Validators.required,
      ], // Solo habilitado en edición
      confirmPassword: [
        { value: '', disabled: !confirmPassword },
        Validators.required,
      ],
      rol: [{ value: '', disabled: rolNotRequired }, Validators.required], // Solo habilitado en registro
    });
  }

  setUp() {
    const urlSeg = this.route.snapshot.url;
    if (this.isAuthRoute(urlSeg, 'login')) {
      // Login
      this.isAuth = true;
      this.isLogin = true;
      this.mode = 'Login';
      this.currentPasswordText = 'Contraseña';
      this.submitButtonText = 'Login';
      this.titleText = 'Login';
    } else if (this.isAuthRoute(urlSeg, 'register')) {
      // Register
      this.isAuth = true;
      this.isRegister = true;
      this.mode = 'Register';
      this.currentPasswordText = 'Contraseña';
      this.submitButtonText = 'Create';
      this.titleText = 'Register';
    } else {
      // Identificar si estamos usando /me o /users/:id
      getBasePath(this.route);
      const isMeRoute = urlSeg[0].path === 'me';
      this.isMeRoute = isMeRoute;

      if (this.isEditRoute(urlSeg, isMeRoute)) {
        this.isEditing = true;
        this.mode = 'EditUser';
        this.titleText = 'Editar mis datos';
      } else if (this.isChangePasswordRoute(urlSeg, isMeRoute)) {
        this.mode = 'ChangePassword';
        this.isEditing = true;
        this.isChangePassword = true;
        this.currentPasswordText = 'Contraseña actual';
        this.titleText = 'Cambiar mi contraseña';
      } else if (this.isViewUserRoute(urlSeg, isMeRoute)) {
        this.mode = 'ViewUser';
        this.isViewUser = true;
        this.titleText = 'Mis datos';
      }

      this.submitButtonText = 'Update';
    }

    this.initializeForm();
    if (!this.isAuth) {
      this.loadUser();
    } else {
      this.sessionService.getSession().subscribe({
        next: (session) => {
          if (session) {
            this.router.navigateByUrl(
              this.sessionService.getMainPage(session.rol)
            );
          }
        },
      });
    }
  }

  getHotelsUri() {
    const basePath = getBasePath(this.route); // Obtener la base: '/me' o '/users/:id'
    return `${basePath}/hotels`;
  }

  getBookingsUri() {
    const basePath = getBasePath(this.route); // Obtener la base: '/me' o '/users/:id'
    return `${basePath}/bookings`;
  }

  togglePassword() {
    const basePath = getBasePath(this.route); // Obtener la base: '/me' o '/users/:id'

    if (this.mode === 'EditUser') {
      this.router.navigateByUrl(`${basePath}/change-passwd`);
    } else if (this.mode === 'ChangePassword') {
      this.router.navigateByUrl(`${basePath}/edit`);
    }
  }

  switchMode() {
    const basePath = getBasePath(this.route); // Obtener la base: '/me' o '/users/:id'
    console.log({ ...this });
    if (this.mode === 'EditUser') {
      this.router.navigateByUrl(basePath);
    } else if (this.mode === 'ViewUser') {
      this.router.navigateByUrl(`${basePath}/edit`);
    }
  }

  private resolve(): Observable<any> {
    const userId = this.route.snapshot.paramMap.get('id');
    console.log({ userId });
    return userId
      ? this.userService.getUser(Number(userId))
      : this.sessionService.getSession();
  }

  private loadUser() {
    // this.setData();
    this.resolve().subscribe({
      next: (user) => {
        this.user = user;
        this.isHotelManager = (user.rol as UserRol) === 'HOTEL_ADMIN';
        this.isAdmin = (user.rol as UserRol) === 'ADMIN';
        this.setData();
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  private setData() {
    this.userForm.patchValue({
      name: this.user.name,
      email: this.user.email,
      rol: this.user.rol,
    });
  }

  validForm() {
    const validForm = this.userForm.valid;
    const differentData =
      this.isEditing && !this.isChangePassword && this.modifiedData();
    const validatePassword = this.validatePassword();

    return validForm && (differentData || validatePassword || this.isLogin);
  }

  private modifiedData() {
    return (
      this.userForm.get('name')?.value !== this.user.name ||
      this.userForm.get('email')?.value !== this.user.email
    );
  }

  private validatePassword() {
    const { currentPassword, newPassword, confirmPassword } =
      this.userForm.value;
    const updatePasswordValidate =
      this.isEditing &&
      this.isChangePassword &&
      newPassword === confirmPassword &&
      currentPassword !== newPassword;
    const registerPasswordValidate =
      this.isRegister && currentPassword === confirmPassword;
    return updatePasswordValidate || registerPasswordValidate;
  }

  onSubmit() {
    const data = this.userForm.value;
    console.log({ data });

    switch (this.mode) {
      case 'Login':
        this.login(data.email, data.currentPassword);
        break;
      case 'Register':
        this.register(data.name, data.email, data.currentPassword, data.rol);
        break;
      case 'EditUser':
        this.updateUser(data.name, data.email);
        break;
      case 'ChangePassword':
        this.changePassword(data.currentPassword, data.newPassword);
        break;
      default:
        break;
    }
  }

  private login(email: string, password: string) {
    this.sessionService.login(email, password).subscribe({
      next: (r: any) => {
        this.router.navigateByUrl(r.mainPage);
      },
      error: (error) => {
        console.error(error);
        // this.toastr.error('Invalid email or password');
      },
    });
  }

  private register(
    name: string,
    email: string,
    password: string,
    rol: UserRol
  ) {
    console.log({ name, email, password, rol });
    this.sessionService.register(name, email, password, rol).subscribe({
      next: (r: any) => {
        this.router.navigateByUrl(r.mainPage);
      },
      error: (error) => {
        console.error(error);
        // this.toastr.error('Invalid email or password');
      },
    });
  }

  private updateUser(name: string, email: string) {
    this.userService.updateUser(this.user.id, { name, email }).subscribe({
      next: () => {
        this.router.navigateByUrl(getBasePath(this.route));
      },
      error: (error) => {
        console.error(error);
        // this.toastr.error('Invalid email or password');
      },
    });
  }

  private changePassword(password: string | undefined, newPassword: string) {
    alert('Unimplemented yet');
  }
}
