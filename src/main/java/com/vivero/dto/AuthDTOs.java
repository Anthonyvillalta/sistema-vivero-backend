package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "El nombre de usuario es requerido")
        private String username;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private String tokenType;
        private String username;
        private String fullName;
        private String role;

        public static AuthResponse of(String token, String username, String fullName, String role) {
            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .username(username)
                    .fullName(fullName)
                    .role(role)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "El nombre de usuario es requerido")
        private String username;

        @NotBlank(message = "La contraseña es requerida")
        private String password;

        @NotBlank(message = "El nombre completo es requerido")
        private String fullName;

        private String email;
        private String phone;
        private String roleName;
    }
}
