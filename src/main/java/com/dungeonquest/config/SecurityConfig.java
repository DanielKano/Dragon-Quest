package com.dungeonquest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/css/**", "/js/**", "/assets/**", "/auth/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")                
                .requestMatchers("/misiones/nueva", "/misiones/editar/**", "/misiones/verificar/**", "/misiones/{id}/asignar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                .requestMatchers("/misiones", "/misiones/mis-misiones", "/misiones/tomar/**", "/misiones/completar/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("nombreUsuario")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/auth/login");
                })
                .accessDeniedPage("/error")
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll()
            )
            .userDetailsService(userDetailsService)  // CRUCIAL: Configurar el UserDetailsService
            .csrf(csrf -> csrf.disable()); // Temporal para desarrollo

        return http.build();
    }
}