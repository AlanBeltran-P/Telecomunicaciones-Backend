package com.telecom.usuarios.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.telecom.seguridad.SeguridadJwt;
import com.telecom.seguridad.TokenDeServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Login y logout no leen la cookie: una sesión vencida no impide volver a entrar
    @Bean
    @Order(1)
    SecurityFilterChain autenticacion(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/auth/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // Gestión de usuarios: solo el administrador
    @Bean
    @Order(2)
    SecurityFilterChain gestionDeUsuarios(HttpSecurity http) throws Exception {
        http
                // La cookie es SameSite=Strict: el navegador no la envía desde otros sitios
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        // Va antes que /api/usuarios/**: el perfil propio lo ve cualquier rol
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/yo").authenticated()
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/clientes/**").hasRole("EMPLEADO")
                        .requestMatchers("/api/tecnicos/**").hasRole("EMPLEADO")
                        // Solo otros microservicios; el Gateway no expone estas rutas
                        .requestMatchers("/api/internos/**").hasRole(TokenDeServicio.ROL)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(SeguridadJwt.tokenDesdeLaCookie());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("${jwt.secreto}") String secreto) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(SeguridadJwt.clave(secreto)));
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        return SeguridadJwt.decodificador(secreto);
    }
}
