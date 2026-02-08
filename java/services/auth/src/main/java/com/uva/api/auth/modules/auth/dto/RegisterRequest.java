package com.uva.api.auth.modules.auth.dto;

import com.uva.api.auth.modules.internal.dto.UserRol;
import org.springframework.lang.NonNull;

public record RegisterRequest(
        @NonNull String email,
        @NonNull String password,
        @NonNull UserRol rol,
        @NonNull String name
) {
}
