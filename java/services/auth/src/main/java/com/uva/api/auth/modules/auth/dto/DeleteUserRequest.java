package com.uva.api.auth.modules.auth.dto;

import org.springframework.lang.NonNull;

public record DeleteUserRequest(
        @NonNull String password
) {
}
