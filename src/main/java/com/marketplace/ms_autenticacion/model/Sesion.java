package com.marketplace.ms_autenticacion.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Entity @Table(name="sesiones")
public class Sesion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="usuario_id", nullable=false) private Long usuarioId;
    @Column(name="email_usuario", nullable=false, length=150) private String emailUsuario;
    @Column(nullable=false, unique=true, length=500) private String token;
    @Column(name="expira_en", nullable=false) private LocalDateTime expiraEn;
    @Column(nullable=false) private boolean activo=true;
    @Column(name="creado_en", updatable=false) private LocalDateTime creadoEn;
    @PrePersist public void pre(){ creadoEn=LocalDateTime.now(); }
}
