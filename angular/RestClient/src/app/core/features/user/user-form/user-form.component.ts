import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserClientService } from '../../../../shared/user-client.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
})
export class UserFormComponent implements OnInit {
  userForm!: FormGroup;
  isEditing = false;

  constructor(private fb: FormBuilder, private userService: UserClientService) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserData();
  }

  private initializeForm(): void {
    this.userForm = this.fb.group({
      name: [{ value: '', disabled: true }, Validators.required],
      email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
      currentPassword: [''], // Solo habilitado en modo edición
      newPassword: [''], // Solo habilitado en modo edición
    });
  }

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe((user) => {
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
