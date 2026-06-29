package com.marketplace.ms_autenticacion.repository;
import com.marketplace.ms_autenticacion.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SesionRepository extends JpaRepository<Sesion,Long> {
    Optional<Sesion> findByToken(String token);
    Optional<Sesion> findByUsuarioIdAndActivoTrue(Long usuarioId);
}
