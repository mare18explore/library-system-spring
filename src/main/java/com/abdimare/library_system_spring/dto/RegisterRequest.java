package com.abdimare.library_system_spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// what the client sends to sign up, the plain password never gets saved, only its hash
public record RegisterRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Size(min = 8, message = "must be at least 8 characters") String password
) {}