package com.cwgio97.webservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // Classe di configurazione
@EnableWebFluxSecurity // Standard per il reactive
//@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    // Creo i bean di configurazione

    // Per criptare le password
    @Bean
    public BCryptPasswordEncoder pswEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Qui specifico quali endpoint difendere e quali ruoli gli utenti devono avere per accedere
    // agli endpoint. Qui ci saranno due ruoli: admin e impiegato.
    // L'admin può modificare i clienti, l'impiegato può solamente visualizzarli.
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Creo due array che identificano quali endpoint richiedono quali permessi
        // ** = Qualsiasi elemento
        final String[] EMPLOYEE_MATCHER = {
                "/api/customers/find/**",
                "/api/gifts/find/**",
                "/api/gifts/auth"};
        final String[] ADMIN_MATCHER = {
                "/api/customers/insert/**",
                "/api/customers/delete/**",
                "/api/gifts/insert/**",
                "/api/gifts/delete/**",
                "/api/gifts/auth"};

        // Specifico le impostazioni
        return  http.authorizeExchange()
                // OPTIONS servirà quando ci si interfaccia con il frontend.
                // Se non si specifica permit all sul metodo option relativo a qualsiasi fonte,
                // non sarà possibile usare il frontend con i rispettivi dati
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Specifico i due matcher, che vengono attribuiti ai due ruoli
                .pathMatchers(EMPLOYEE_MATCHER).hasAnyRole("EMPLOYEE")
                .pathMatchers(ADMIN_MATCHER).hasAnyRole("ADMIN")
                .anyExchange().permitAll()
                .and().httpBasic()
                // Qui disabilito il Cross Site Request Forgery
                .and().csrf().disable()
                .build();
    }

    // Qui creo gli utenti, con le password criptate
    // Standard
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(
                // L'utente "user" ha i soli permessi da impiegato
                User.builder()
                        .username("user")
                        .password(new BCryptPasswordEncoder().encode("user"))
                        .roles("EMPLOYEE")
                        .build(),
                // L'admin invece li ha tutti e due
                User.builder()
                        .username("admin")
                        .password(new BCryptPasswordEncoder().encode("admin"))
                        .roles("ADMIN", "EMPLOYEE")
                        .build()
        );
    }

    // Fix StackOverFlow per CORS non funzionante in modo del tutto corretto... Ma non funziona :(
    /*
    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
    */
}