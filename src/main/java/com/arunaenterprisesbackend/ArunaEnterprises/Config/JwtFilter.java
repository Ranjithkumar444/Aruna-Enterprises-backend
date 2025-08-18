package com.arunaenterprisesbackend.ArunaEnterprises.Config;

import com.arunaenterprisesbackend.ArunaEnterprises.Service.CustomUserDetailService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {


    @Autowired
    private JWTService jwtService;
    @Autowired
    ApplicationContext context; // Used to get CustomUserDetailService bean

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String role = null; // To store the role extracted from the token

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtService.extractEmail(token);
                // Extract the role directly from the token's claims
                role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
            }

            // If email is extracted and no authentication is currently set in SecurityContext
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load UserDetails (primarily to check if user exists and account status)
                // Note: The role is now primarily taken from the token, but this provides a sanity check.
                UserDetails userDetails = context.getBean(CustomUserDetailService.class).loadUserByUsername(email);

                // Validate the token and ensure the extracted role is not null
                if (jwtService.validateToken(token, userDetails) && role != null) {
                    // Create authentication token using UserDetails (principal) and the role from the token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // credentials not needed after authentication
                            Collections.singletonList(new SimpleGrantedAuthority(role)) // Set authority from token's role
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
        }
    }
}