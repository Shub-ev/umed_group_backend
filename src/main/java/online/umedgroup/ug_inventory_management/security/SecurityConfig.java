package online.umedgroup.ug_inventory_management.security;

import online.umedgroup.ug_inventory_management.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // ---------------- PUBLIC ----------------
                        .requestMatchers(
                                "/error",
                                "/swagger-ui/**",
                                "/v3/**"
                        ).permitAll()

                        // ---------------- EMPLOYEE AUTH ----------------
                        .requestMatchers("/employee/login").permitAll()

                        // ---------------- EMPLOYEE CREATE (ADMIN ONLY) ----------------
                        .requestMatchers(HttpMethod.POST, "/employee", "/employee/").hasRole("ADMIN")

                        // ---------------- EMPLOYEE SELF OPERATIONS ----------------
                        .requestMatchers(HttpMethod.PATCH, "/employee/update/**")
                        .hasAnyRole("EMPLOYEE", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/employee/delete")
                        .hasAnyRole("EMPLOYEE", "ADMIN")   // ✅ FIXED (self delete allowed)

                        // ---------------- ADMIN DELETE EMPLOYEE ----------------
                        .requestMatchers(HttpMethod.DELETE, "/employee/delete-admin")
                        .hasRole("ADMIN")

                        // ---------------- EMPLOYEE GET APIs ----------------
                        .requestMatchers("/employee/**")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                        // ---------------- REPORTS ----------------
                        .requestMatchers("/reports/**")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                        // ---------------- TEMPLATES & INVENTORY ----------------
                        .requestMatchers("/templates/**").authenticated()
                        .requestMatchers("/inventory/**").authenticated()

                        // ---------------- ADMIN ----------------
                        .requestMatchers("/hkfu/login").permitAll()
                        .requestMatchers("/hkfu/**").hasRole("ADMIN")

                        // ---------------- DEFAULT ----------------
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://umedgroup.online"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}