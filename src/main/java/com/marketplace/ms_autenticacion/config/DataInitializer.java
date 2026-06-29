package com.marketplace.ms_autenticacion.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
// IMPORTANTE: Arrancar PRIMERO ms-usuarios (puerto 8081)
@Slf4j @Component
public class DataInitializer implements CommandLineRunner {
    @Override public void run(String... args){ log.info(">>> ms-autenticacion listo en puerto 8082. Requiere ms-usuarios en 8081."); }
}
