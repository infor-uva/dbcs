package com.uva.api.auth.models.auth;

import org.springframework.lang.NonNull;

public record DeleteUserRequest(
        @NonNull String password
) {
}
