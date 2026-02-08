package com.uva.api.auth.modules.auth.dto;

import jakarta.validation.constraints.Email;
import org.springframework.lang.NonNull;

public record ChangePasswordRequest(
        @NonNull @Email String email,
        @NonNull String oldPassword,
        @NonNull String newPassword
) {
}
