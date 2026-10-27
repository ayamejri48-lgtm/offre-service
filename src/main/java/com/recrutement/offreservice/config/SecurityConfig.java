
package com.recrutement.offreservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()


                        // Consulter toutes les offres ou une offre précise
                        .requestMatchers(HttpMethod.GET, "/offres", "/offres/**")
                        .authenticated()

                        // Créer une offre
                        .requestMatchers(HttpMethod.POST, "/offres")
                        .hasRole("RECRUITER")

                        // Modifier une offre
                        .requestMatchers(HttpMethod.PUT, "/offres/**")
                        .hasAnyRole("ADMIN", "RECRUITER")

                        // Publier ou clôturer une offre
                        .requestMatchers(HttpMethod.PATCH, "/offres/**")
                        .hasAnyRole("ADMIN", "RECRUITER")

                        // Supprimer une offre
                        .requestMatchers(HttpMethod.DELETE, "/offres/**")
                        .hasAnyRole("ADMIN", "RECRUITER")

                        // Créer une competence
                        .requestMatchers(HttpMethod.POST, "/offres/*/skills")
                        .hasAnyRole("ADMIN", "RECRUITER")

                                // Candidat : postuler à une offre
                                .requestMatchers(HttpMethod.POST, "/candidatures")
                                .hasRole("CANDIDATE")

                        // Candidat : consulter ses candidatures
                                .requestMatchers(HttpMethod.GET, "/candidatures/me")
                                .hasRole("CANDIDATE")

                        // Recruteur ou admin : consulter les candidatures d'une offre
                                .requestMatchers(HttpMethod.GET, "/candidatures/offre/*")
                                .hasAnyRole("ADMIN", "RECRUITER")

                        // Candidat : retirer sa candidature
                                .requestMatchers(HttpMethod.PATCH, "/candidatures/*/withdraw")
                                .hasRole("CANDIDATE")

                         // Recruteur ou admin : traiter une candidature
                                .requestMatchers(HttpMethod.PATCH, "/candidatures/*/status")
                                .hasAnyRole("ADMIN", "RECRUITER")

                        // Refuser les autres requêtes
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new Converter<Jwt, Collection<GrantedAuthority>>() {
                    @Override
                    public Collection<GrantedAuthority> convert(Jwt jwt) {
                        String role = jwt.getClaimAsString("role");

                        if (role == null || role.isBlank()) {
                            return List.of();
                        }

                        return List.of(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        );
                    }
                }
        );

        return converter;
    }
}
