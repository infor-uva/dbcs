package com.uva.api.auth.modules.jwt.dto;

public record TokenPairResponse(String accessToken, String refreshToken, long expiresIn) {
}
