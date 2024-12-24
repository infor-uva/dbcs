package com.uva.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Devuelve un 404 cuando se lanza la excepción
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("User not found with id: " + id);
    }
}
