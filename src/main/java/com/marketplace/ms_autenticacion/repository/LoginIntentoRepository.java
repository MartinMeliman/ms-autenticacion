package com.marketplace.ms_autenticacion.repository;
import com.marketplace.ms_autenticacion.model.LoginIntento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
public interface LoginIntentoRepository extends JpaRepository<LoginIntento,Long> {
    // Cuenta intentos fallidos en los ultimos 15 minutos (regla de negocio: max 5)
    @Query("SELECT COUNT(l) FROM LoginIntento l WHERE l.email=:email AND l.exitoso=false AND l.intentadoEn>=:desde")
    long contarIntentosFallidos(@Param("email") String email, @Param("desde") LocalDateTime desde);
}
