package com.marketplace.ms_autenticacion.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.*;
// Llama a ms-usuarios para verificar que el usuario existe
@FeignClient(name="ms-usuarios", url="${ms.usuarios.url}")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/buscar") List<Map<String,Object>> buscarPorNombre(@RequestParam String nombre);
}
