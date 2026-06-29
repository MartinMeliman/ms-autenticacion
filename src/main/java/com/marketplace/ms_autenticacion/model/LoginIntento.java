package com.marketplace.ms_autenticacion.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Entity @Table(name="login_intentos")
public class LoginIntento {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=150) private String email;
    @Column(nullable=false) private boolean exitoso=false;
    @Column(name="intentado_en", updatable=false) private LocalDateTime intentadoEn;
    @PrePersist public void pre(){ intentadoEn=LocalDateTime.now(); }
}
