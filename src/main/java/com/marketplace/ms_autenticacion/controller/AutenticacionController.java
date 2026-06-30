package com.marketplace.ms_autenticacion.controller;

import com.marketplace.ms_autenticacion.dto.*;
import com.marketplace.ms_autenticacion.model.Sesion;
import com.marketplace.ms_autenticacion.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login, sesiones y validación de tokens del marketplace EcoTrade")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @Operation(summary = "Iniciar sesión",
               description = "Autentica al usuario con email y contraseña. " +
                             "Bloquea la cuenta tras 5 intentos fallidos consecutivos. " +
                             "Retorna un token de sesión válido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token de sesión"),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "403", description = "Cuenta bloqueada por intentos fallidos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(autenticacionService.login(dto));
    }

    @Operation(summary = "Cerrar sesión",
               description = "Invalida el token de sesión del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Sesión cerrada correctamente"),
        @ApiResponse(responseCode = "404", description = "Token no encontrado")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token de sesión a invalidar") @RequestParam String token) {
        autenticacionService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar token de sesión",
               description = "Verifica si un token de sesión es válido y no ha expirado. " +
                             "Retorna la información de la sesión si es válido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido, retorna datos de la sesión"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/validar")
    public ResponseEntity<Sesion> validar(
            @Parameter(description = "Token de sesión a validar") @RequestParam String token) {
        return autenticacionService.validarToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}