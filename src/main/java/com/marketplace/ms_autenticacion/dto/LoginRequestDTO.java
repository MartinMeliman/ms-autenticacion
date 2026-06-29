package com.marketplace.ms_autenticacion.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message="El email es obligatorio") @Email(message="Formato de email invalido") private String email;
    @NotBlank(message="La contrasena es obligatoria") private String password;
}
