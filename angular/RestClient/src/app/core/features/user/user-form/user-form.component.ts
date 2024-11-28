import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule,
} from '@angular/forms';
import { UserClientService } from '../../../../shared/user-client.service';
import { users } from '../../../../../mocks/users';
import { ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
  imports: [ReactiveFormsModule, FormsModule],
})
export class UserFormComponent implements OnInit {
  userForm!: FormGroup;
  isEditing = false;
  id = 0;

  constructor(
    private fb: FormBuilder,
    private userService: UserClientService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.route.paramMap.subscribe({
      next: (params) => {
        const id = Number(params.get('id'));
        if (id) {
          this.id = id;
          this.isEditing = true;
          this.loadUserData();
        }
      },
    });
  }

  private initializeForm(): void {
    this.userForm = this.fb.group({
      name: [{ value: '', disabled: true }, Validators.required],
      email: [
        { value: '', disabled: true },
        [Validators.required, Validators.email],
      ],
      currentPassword: [''], // Solo habilitado en modo edición
      newPassword: [''], // Solo habilitado en modo edición
    });
  }

  private loadUserData(): void {
    // this.userService.getCurrentUser().subscribe((user) => {
    console.log({ id: this.id });
    users
      .filter((u) => u.id == this.id)
      .slice(0)
      .map((user) => {
        this.userForm.patchValue({
          name: user.name,
          email: user.email,
        });
      });
  }

  toggleEdit(): void {
    this.isEditing = true;
    this.userForm.get('name')?.enable();
    this.userForm.get('email')?.enable();
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.loadUserData(); // Volver a cargar los datos originales
    this.userForm.get('name')?.disable();
    this.userForm.get('email')?.disable();
  }

  saveChanges(): void {
    if (this.userForm.valid) {
      const updatedData = this.userForm.value;
      this.userService.updateUser(updatedData).subscribe(() => {
        this.isEditing = false;
        this.loadUserData();
      });
    }
  }
}
