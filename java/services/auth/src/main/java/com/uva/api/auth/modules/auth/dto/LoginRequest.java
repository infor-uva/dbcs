package com.uva.api.auth.modules.auth.dto;

import org.springframework.lang.NonNull;

public record LoginRequest(
        @NonNull
        String email,
        @NonNull
        String password) {
}
