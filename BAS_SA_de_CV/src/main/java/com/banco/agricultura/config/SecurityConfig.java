package com.banco.agricultura.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    // ─── 1. Password Encoder ──────────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ─── 2. Authentication Manager ────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ─── 3. Security Filter Chain ─────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ── Deshabilitar CSRF para JSF (JSF maneja su propio ViewState) ──
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/api/**"),   // REST endpoints
                                new AntPathRequestMatcher("/**/*.xhtml"), // JSF pages
                                new AntPathRequestMatcher("/views/**"),  // JSF pages por rol
                                new AntPathRequestMatcher("/logout")    // Ignorar CSRF en logout
                        )
                )

                // ── Autorización de rutas por rol ─────────────────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Recursos públicos
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/login.xhtml"),
                                new AntPathRequestMatcher("/views/registro.xhtml"),
                                new AntPathRequestMatcher("/resources/**"),
                                new AntPathRequestMatcher("/static/**"),
                                new AntPathRequestMatcher("/jakarta.faces.resource/**"),
                                new AntPathRequestMatcher("/debug-user/**")
                        ).permitAll()

                        // Módulo Cliente
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/cliente/**")
                        ).hasRole("Cliente")

                        // Módulo Dependiente
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/dependiente/**"),
                                new AntPathRequestMatcher("/api/dependiente/**")
                        ).hasRole("Dependiente")

                        // Módulo Cajero
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/cajero/**")
                        ).hasRole("Cajero")

                        // Módulo Gerente de Sucursal
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/gerente-sucursal/**")
                        ).hasRole("Gerente de Sucursal")

                        // Módulo Gerente General
                        .requestMatchers(
                                new AntPathRequestMatcher("/views/gerente-general/**")
                        ).hasRole("Gerente General")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // ── Configuración del Login ───────────────────────────────────────
                .formLogin(login -> login
                        .loginPage("/views/login.xhtml")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/views/login.xhtml?error=true")
                        .permitAll()
                )

                // ── Logout ────────────────────────────────────────────────────────
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/views/login.xhtml?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ── Acceso denegado ───────────────────────────────────────────────
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/views/error/403.xhtml")
                );

        return http.build();
    }

    // ─── 4. Success Handler — redirige según rol ──────────────────────────────
    @Bean
    public org.springframework.security.web.authentication.AuthenticationSuccessHandler
    authenticationSuccessHandler() {

        return (request, response, authentication) -> {
            String redirectUrl = "/views/login.xhtml";

            boolean hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Cliente"));
            if (hasRole) redirectUrl = "/views/cliente/dashboard.xhtml";

            hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Dependiente"));
            if (hasRole) redirectUrl = "/views/dependiente/dashboard.xhtml";

            hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Cajero"));
            if (hasRole) redirectUrl = "/views/cajero/dashboard.xhtml";

            hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Gerente de Sucursal"));
            if (hasRole) redirectUrl = "/views/gerente-sucursal/dashboard.xhtml";

            hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_Gerente General"));
            if (hasRole) redirectUrl = "/views/gerente-general/dashboard.xhtml";

            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }
}