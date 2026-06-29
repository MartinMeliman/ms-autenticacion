package com.marketplace.ms_autenticacion.service;
import com.marketplace.ms_autenticacion.client.UsuarioClient;
import com.marketplace.ms_autenticacion.dto.*;
import com.marketplace.ms_autenticacion.model.*;
import com.marketplace.ms_autenticacion.repository.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor
public class AutenticacionService {
    private final SesionRepository sesionRepository;
    private final LoginIntentoRepository loginIntentoRepository;
    private final UsuarioClient usuarioClient;

    public LoginResponseDTO login(LoginRequestDTO dto){
        log.info("Intento de login: {}", dto.getEmail());
        // Regla de negocio: max 5 intentos fallidos en 15 minutos
        long intentos = loginIntentoRepository.contarIntentosFallidos(dto.getEmail(), LocalDateTime.now().minusMinutes(15));
        if(intentos>=5) throw new RuntimeException("Cuenta bloqueada temporalmente. Intente en 15 minutos.");
        Map<String,Object> usuario = verificarUsuario(dto.getEmail());
        registrarIntento(dto.getEmail(), true);
        sesionRepository.findByUsuarioIdAndActivoTrue(Long.valueOf(usuario.get("id").toString())).ifPresent(s->{ s.setActivo(false); sesionRepository.save(s); });
        String token = UUID.randomUUID().toString().replace("-","") + UUID.randomUUID().toString().replace("-","");
        LocalDateTime expira = LocalDateTime.now().plusHours(24);
        Sesion s = new Sesion(); s.setUsuarioId(Long.valueOf(usuario.get("id").toString()));
        s.setEmailUsuario(dto.getEmail()); s.setToken(token); s.setExpiraEn(expira);
        sesionRepository.save(s);
        log.info("Login exitoso: {}", dto.getEmail());
        return new LoginResponseDTO(token, Long.valueOf(usuario.get("id").toString()), dto.getEmail(), (String)usuario.get("rol"), expira, "Login exitoso");
    }

    public void logout(String token){ sesionRepository.findByToken(token).ifPresent(s->{ s.setActivo(false); sesionRepository.save(s); }); }

    public Optional<Sesion> validarToken(String token){ return sesionRepository.findByToken(token).filter(s->s.isActivo()&&s.getExpiraEn().isAfter(LocalDateTime.now())); }

    private Map<String,Object> verificarUsuario(String email){
        try{
            List<Map<String,Object>> usuarios = usuarioClient.buscarPorNombre(email);
            if(usuarios==null||usuarios.isEmpty()){ registrarIntento(email,false); throw new RuntimeException("Credenciales incorrectas"); }
            return usuarios.get(0);
        }catch(FeignException e){ throw new RuntimeException("No se puede conectar con ms-usuarios"); }
    }
    private void registrarIntento(String email, boolean exitoso){ LoginIntento i=new LoginIntento(); i.setEmail(email); i.setExitoso(exitoso); loginIntentoRepository.save(i); }
}
