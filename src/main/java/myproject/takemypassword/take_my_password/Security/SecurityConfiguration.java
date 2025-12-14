package myproject.takemypassword.take_my_password.Security;

import myproject.takemypassword.take_my_password.component.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req

                        // Endpoints pubblici
                        .requestMatchers(
                                "/api/auth/*",
                                "/api/auth/**",
                                "/user/register",
                                "/css/**",
                                "/js/**",
                                "/",
                                "/password",
                            "/user/confirm-email")
                        .permitAll()

                        // Endpoints protetti
                        .requestMatchers("/api/archive/*").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/archive/**").hasAnyAuthority("ADMIN", "USER")

                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. Permetti le credenziali (necessario per l'header Authorization con JWT)
        config.setAllowCredentials(true);

        // 2. Specifica i domini del tuo frontend
        // Quando sei in sviluppo
        config.addAllowedOrigin("http://10.0.2.2:8080"); // Per emulatore Android
        config.addAllowedOrigin("http://localhost:3000"); // Per local react
        // Quando sei su Render.com
        // Ricorda di cambiare "URL_FRONEND_RENDER" con l'URL effettivo del tuo sito
        // React su Render
        config.addAllowedOrigin("https://take-my-password-react-app.onrender.com"); 

        // 3. Permetti tutti i metodi HTTP
        config.addAllowedMethod("*");

        // 4. Permetti tutti gli header (cruciale per l'header Authorization)
        config.addAllowedHeader("*");

        // Applica questa configurazione a tutti i percorsi
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }
}