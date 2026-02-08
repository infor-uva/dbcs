package com.uva.api.auth.models.auth;

import com.uva.api.auth.models.remote.UserRol;
import org.springframework.lang.NonNull;

public record RegisterRequest(
        @NonNull String email,
        @NonNull String password,
        @NonNull UserRol rol,
        @NonNull String name
) {
}
