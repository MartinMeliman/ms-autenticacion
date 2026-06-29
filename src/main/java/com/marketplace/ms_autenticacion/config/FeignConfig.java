package com.marketplace.ms_autenticacion.config;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
@Configuration @EnableFeignClients(basePackages="com.marketplace.ms_autenticacion.client")
public class FeignConfig {}
