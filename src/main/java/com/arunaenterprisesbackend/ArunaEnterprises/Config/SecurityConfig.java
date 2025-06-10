package com.arunaenterprisesbackend.ArunaEnterprises.Config;

import com.arunaenterprisesbackend.ArunaEnterprises.Service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Autowired
    private CustomUserDetailService userDetailsService; // Autowire your CustomUserDetailService
    @Autowired
    private JwtFilter jwtFilter;

    @Value("${cors.allowed-origin}") // Make sure you have this property in application.properties/yaml
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests -> requests
                        // Publicly accessible endpoints (no authentication/authorization needed)
                        .requestMatchers("/", "/public/**", "/admin/login", "/favicon.ico", "/error", "/actuator/**").permitAll()

                        // Role-based access for /admin endpoints
                        // Only SUPER_ADMIN can create new admins (regular or super)
                        .requestMatchers("/admin/create").hasRole("SUPER_ADMIN")
                        // Only SUPER_ADMIN can view all admins
                        .requestMatchers("/admin/get-admins").hasRole("SUPER_ADMIN")
                        // Example: Only SUPER_ADMIN can register new employees
                        .requestMatchers("/admin/register-employee").hasRole("SUPER_ADMIN")

                        // Endpoints accessible by both SUPER_ADMIN and ADMIN
                        .requestMatchers("/admin/dashboard").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/admin/salary/**").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/attendance-list").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/contact/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/admin/box/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // Add more specific rules here for other /admin endpoints as needed

                        // Fallback: Any other /admin endpoint not explicitly matched above requires authentication
                        // This should ideally be covered by more specific rules.
                        .requestMatchers("/admin/**").authenticated()

                        // All other requests (outside /admin and public) require authentication
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic as JWT is used
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before username/password filter
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin)); // Use the value from properties
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // Allow all headers
        config.setAllowCredentials(true); // Allow sending credentials (e.g., cookies, auth headers)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS to all paths
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // Set the password encoder
        provider.setUserDetailsService(userDetailsService); // Set your custom UserDetailsService
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Get AuthenticationManager from configuration
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password hashing
    }
}
