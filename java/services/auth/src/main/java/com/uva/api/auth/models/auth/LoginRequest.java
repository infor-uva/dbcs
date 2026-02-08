package com.uva.api.auth.models.auth;

import org.springframework.lang.NonNull;

public record LoginRequest(
        @NonNull
        String email,
        @NonNull
        String password) {
}
