package com.marketplace.ms_autenticacion.controller;
import com.marketplace.ms_autenticacion.dto.*;
import com.marketplace.ms_autenticacion.model.Sesion;
import com.marketplace.ms_autenticacion.service.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AutenticacionController {
    private final AutenticacionService autenticacionService;
    @PostMapping("/login")   public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){ return ResponseEntity.ok(autenticacionService.login(dto)); }
    @PostMapping("/logout")  public ResponseEntity<Void> logout(@RequestParam String token){ autenticacionService.logout(token); return ResponseEntity.noContent().build(); }
    @GetMapping("/validar")  public ResponseEntity<Sesion> validar(@RequestParam String token){ return autenticacionService.validarToken(token).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); }
}
