package com.marketplace.ms_autenticacion.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO { private String token; private Long usuarioId; private String email; private String rol; private LocalDateTime expiraEn; private String mensaje; }
