package com.marketplace.ms_autenticacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marketplace.ms_autenticacion.client.UsuarioClient;
import com.marketplace.ms_autenticacion.dto.LoginRequestDTO;
import com.marketplace.ms_autenticacion.dto.LoginResponseDTO;
import com.marketplace.ms_autenticacion.model.LoginIntento;
import com.marketplace.ms_autenticacion.model.Sesion;
import com.marketplace.ms_autenticacion.repository.LoginIntentoRepository;
import com.marketplace.ms_autenticacion.repository.SesionRepository;
import com.marketplace.ms_autenticacion.service.AutenticacionService;

/**
 * Pruebas unitarias para AutenticacionService.
 * El FeignClient (UsuarioClient) se simula con @Mock.
 * Patrón Given/When/Then con Mockito (sin BD ni red real).
 */
@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock private SesionRepository sesionRepository;
    @Mock private LoginIntentoRepository loginIntentoRepository;
    @Mock private UsuarioClient usuarioClient;
    @InjectMocks private AutenticacionService autenticacionService;

    private LoginRequestDTO loginDto;

    @BeforeEach
    void setUp() {
        loginDto = new LoginRequestDTO();
        loginDto.setEmail("martin@duoc.cl");
        loginDto.setPassword("clave123");
    }

    @Test
    @DisplayName("login: debería autenticar correctamente y generar token")
    void shouldLoginSuccessfully() {
        // GIVEN — pocos intentos fallidos, usuario existe via Feign
        when(loginIntentoRepository.contarIntentosFallidos(anyString(), any(LocalDateTime.class)))
            .thenReturn(0L);
        Map<String, Object> usuarioMock = Map.of("id", 1L, "rol", "COMPRADOR");
        when(usuarioClient.buscarPorNombre("martin@duoc.cl")).thenReturn(List.of(usuarioMock));
        when(loginIntentoRepository.save(any(LoginIntento.class))).thenReturn(new LoginIntento());
        when(sesionRepository.findByUsuarioIdAndActivoTrue(1L)).thenReturn(Optional.empty());
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(i -> i.getArgument(0));
        // WHEN
        LoginResponseDTO resultado = autenticacionService.login(loginDto);
        // THEN
        assertNotNull(resultado);
        assertNotNull(resultado.getToken());
        assertEquals("martin@duoc.cl", resultado.getEmail());
        assertEquals("COMPRADOR", resultado.getRol());
    }

    @Test
    @DisplayName("login: debería bloquear la cuenta tras 5 intentos fallidos")
    void shouldBlockAfterFiveFailedAttempts() {
        // GIVEN — regla de negocio: 5 intentos fallidos = bloqueo
        when(loginIntentoRepository.contarIntentosFallidos(anyString(), any(LocalDateTime.class)))
            .thenReturn(5L);
        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> autenticacionService.login(loginDto));
        assertTrue(ex.getMessage().contains("bloqueada"));
        // Nunca debió consultar usuarios ni crear sesión
        verify(sesionRepository, never()).save(any());
    }

    @Test
    @DisplayName("login: debería fallar cuando el usuario no existe")
    void shouldFailWhenUserNotFound() {
        // GIVEN — pocos intentos, pero el usuario no existe (lista vacía)
        when(loginIntentoRepository.contarIntentosFallidos(anyString(), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(usuarioClient.buscarPorNombre("martin@duoc.cl")).thenReturn(List.of());
        when(loginIntentoRepository.save(any(LoginIntento.class))).thenReturn(new LoginIntento());
        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> autenticacionService.login(loginDto));
        assertTrue(ex.getMessage().contains("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("validarToken: debería retornar la sesión cuando el token es válido y no expiró")
    void shouldValidateActiveToken() {
        // GIVEN — sesión activa que expira en el futuro
        Sesion sesion = new Sesion();
        sesion.setToken("token123");
        sesion.setActivo(true);
        sesion.setExpiraEn(LocalDateTime.now().plusHours(1));
        when(sesionRepository.findByToken("token123")).thenReturn(Optional.of(sesion));
        // WHEN
        Optional<Sesion> resultado = autenticacionService.validarToken("token123");
        // THEN
        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("validarToken: debería retornar vacío cuando el token expiró")
    void shouldRejectExpiredToken() {
        // GIVEN — sesión activa pero ya expirada
        Sesion sesion = new Sesion();
        sesion.setToken("token123");
        sesion.setActivo(true);
        sesion.setExpiraEn(LocalDateTime.now().minusHours(1));
        when(sesionRepository.findByToken("token123")).thenReturn(Optional.of(sesion));
        // WHEN
        Optional<Sesion> resultado = autenticacionService.validarToken("token123");
        // THEN
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("logout: debería desactivar la sesión")
    void shouldLogoutSuccessfully() {
        // GIVEN
        Sesion sesion = new Sesion();
        sesion.setToken("token123");
        sesion.setActivo(true);
        when(sesionRepository.findByToken("token123")).thenReturn(Optional.of(sesion));
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesion);
        // WHEN
        autenticacionService.logout("token123");
        // THEN
        assertFalse(sesion.isActivo());
        verify(sesionRepository, times(1)).save(sesion);
    }
}
