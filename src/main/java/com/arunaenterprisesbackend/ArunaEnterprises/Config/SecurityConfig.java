package com.arunaenterprisesbackend.ArunaEnterprises.Config;

import com.arunaenterprisesbackend.ArunaEnterprises.Service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/public/**", "/admin/login", "/favicon.ico", "/error", "/actuator/**").permitAll()
                        .requestMatchers("/admin/create").hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/get-admins").hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/register-employee").hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/dashboard").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/admin/salary/**").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/attendance-list").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/contact/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/admin/box/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/admin/api/order-summaries").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/order/client/create").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigin.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
