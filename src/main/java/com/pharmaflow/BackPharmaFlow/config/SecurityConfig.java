package com.pharmaflow.BackPharmaFlow.config;

import com.pharmaflow.BackPharmaFlow.security.JwtAuthenticationFilter;
import com.pharmaflow.BackPharmaFlow.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().and() // Habilitar CORS
            .authorizeHttpRequests()            .requestMatchers("/auth/**").permitAll() // Public endpoints for authentication
            .requestMatchers("/api/auth/**").permitAll() // Public endpoints for authentication (alternative path)
            .requestMatchers("/api/usuarios/**").permitAll() // Permitir registro de usuarios
            .requestMatchers("/api/setup/**").permitAll() // Permitir endpoint de setup
            .anyRequest().authenticated() // All other endpoints require authentication
            .and()
            .addFilterBefore(jwtAuthenticationFilter(jwtTokenProvider()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }
}
