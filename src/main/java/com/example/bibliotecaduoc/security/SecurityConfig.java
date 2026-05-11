package com.example.bibliotecaduoc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security.
 *
 * Define:
 * - Qué rutas son públicas y cuáles requieren autenticación.
 * - Qué rol puede acceder a cada tipo de operación.
 * - Sessión stateless (sin cookies de sesión, solo JWT).
 * - Registro del JwtFilter en la cadena de filtros.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF (no es necesario en APIs REST stateless con JWT)
            .csrf(csrf -> csrf.disable())

            // Sin sesiones en servidor: cada request se autentica solo con el JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorización por ruta y método HTTP
            .authorizeHttpRequests(auth -> auth
                // Endpoints de autenticación: públicos (no requieren token)
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Lectura (GET): cualquier usuario autenticado (USER o ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole("USER", "ADMIN")

                // Escritura (POST, PUT, DELETE): solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // Agrega el filtro JWT antes del filtro de autenticación por usuario/contraseña
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt para encriptar contraseñas. Factor de costo 10 (por defecto).
     * NUNCA se guarda la contraseña en texto plano en la BD.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager como bean para usarlo en AuthController.
     * Spring lo configura automáticamente usando UserDetailsServiceImpl y PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
