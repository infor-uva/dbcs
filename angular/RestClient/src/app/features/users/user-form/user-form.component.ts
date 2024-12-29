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
import { Session } from '@core/models';
import { UserRol, UserRolesArray } from '@features/users';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import {
  UserFormMode,
  UserFormRoute,
} from 'app/features/users/types/UserFormData';
import { getBasePath } from '@utils/utils';
import { AuthClientService } from '@core/services';

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
  mode: UserFormMode = 'OTHER';
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
  isClient = false;
  isManager = false;
  isAdmin = false;

  constructor(
    private fb: FormBuilder,
    private sessionService: SessionService,
    private authService: AuthClientService,
    private userService: UserClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

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
    const snapshot = this.route.snapshot;
    const { data } = snapshot as UserFormRoute;
    const mode = data!.mode!;

    console.log(mode);
    this.isAdmin = !!mode.admin && mode.admin === true;

    switch (mode.formMode) {
      case 'REGISTER':
        this.isAuth = true;
        this.isRegister = true;
        this.currentPasswordText = 'Contraseña';
        this.submitButtonText = 'Create';
        this.titleText = 'Register';
        break;
      case 'LOGIN':
        this.isAuth = true;
        this.isLogin = true;
        this.currentPasswordText = 'Contraseña';
        this.submitButtonText = 'Login';
        this.titleText = 'Login';
        break;
      case 'PASSWORD':
        this.isEditing = true;
        this.isChangePassword = true;
        this.currentPasswordText = 'Contraseña actual';
        this.titleText = 'Cambiar mi contraseña';
        this.submitButtonText = 'Update password';
        break;
      case 'VIEW':
        this.isViewUser = true;
        this.titleText = 'Mis datos';
        break;
      case 'EDIT':
        this.isEditing = true;
        this.titleText = 'Editar mis datos';
        this.submitButtonText = 'Update';
        break;
      case 'OTHER':
      default:
        break;
    }
    this.mode = mode.formMode;

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
    const basePath = getBasePath(this.router.url); // Obtener la base: '/me' o '/users/:id'
    return `${basePath}/hotels`;
  }

  getBookingsUri() {
    const basePath = getBasePath(this.router.url); // Obtener la base: '/me' o '/users/:id'
    return `${basePath}/bookings`;
  }

  togglePassword() {
    const basePath = getBasePath(this.router.url); // Obtener la base: '/me' o '/users/:id'
    console.log('->', basePath);
    if (this.mode === 'EDIT') {
      this.router.navigateByUrl(`${basePath}/change-passwd`);
    } else if (this.mode === 'PASSWORD') {
      this.router.navigateByUrl(`${basePath}/edit`);
    }
  }

  switchMode() {
    const basePath = getBasePath(this.router.url); // Obtener la base: '/me' o '/users/:id'
    console.log('->', { basePath });
    if (this.mode === 'EDIT' || this.mode === 'PASSWORD') {
      this.router.navigateByUrl(basePath);
    } else if (this.mode === 'VIEW') {
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
    this.resolve().subscribe({
      next: (user) => {
        this.user = user;
        this.isManager = (user.rol as UserRol) === 'MANAGER';
        this.isClient = (user.rol as UserRol) === 'CLIENT';
        this.isAdmin = this.isAdmin || (user.rol as UserRol) === 'ADMIN';
        this.setData();
      },
      error: (error) => {
        console.error('Error:', error);
        if (error.status == 404)
          this.router.navigateByUrl(
            getBasePath(this.router.url.split('/').slice(0, -1).join('/'))
          );
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
      case 'LOGIN':
        this.login(data.email, data.currentPassword);
        break;
      case 'REGISTER':
        this.register(data.name, data.email, data.currentPassword, data.rol);
        break;
      case 'EDIT':
        this.updateUser(data.name, data.email);
        break;
      case 'PASSWORD':
        this.updatePassword(data.currentPassword, data.newPassword);
        break;
      case 'VIEW':
        this.deleteUser(this.user.id);
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
        this.router.navigateByUrl(getBasePath(this.router.url));
      },
      error: (error) => {
        console.error(error);
        // this.toastr.error('Invalid email or password');
      },
    });
  }

  private updatePassword(password: string, newPassword: string) {
    if (this.isAdmin) password = '';
    this.authService
      .changePassword({ email: this.user.email, password, newPassword })
      .subscribe({
        next: () => {
          alert('PASSWORD CHANGE!');
          this.router.navigateByUrl(getBasePath(this.router.url));
        },
        error: (error) => {
          console.error(error);
          // this.toastr.error('Invalid email or password');
        },
      });
  }

  private deleteUser(userId: number) {
    const isAdmin = this.isAdmin;
    const isOwner = this.user.id == userId;
    const adminDel = isAdmin && !isOwner;

    const password = adminDel
      ? confirm('Desea eliminar el usuario')
        ? 'password'
        : undefined
      : prompt('Confirma tu contraseña actual');
    if (!!password && password.trim().length != 0)
      this.authService.deleteUser(userId, password).subscribe({
        next: () => {
          if (adminDel) this.router.navigate(['/admin', 'users']);
          else this.sessionService.logout();
        },
        error: (error) => {
          console.error(error);
          // this.toastr.error('Invalid email or password');
        },
      });
  }
}
