package com.ipas.ipas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("🔍 Processing request: " + path); // Debug log

        // Rutas que NO requieren autenticación JWT
        if (isPublicPath(path)) {
            System.out.println("✅ Public path, skipping JWT validation: " + path);
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("🔑 Authorization header: " + requestTokenHeader); // Debug log

        String username = null;
        String jwtToken = null;

        // Extraer token del header Authorization
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                System.out.println("👤 Username from token: " + username); // Debug log
            } catch (Exception e) {
                System.err.println("❌ Error extracting username from JWT: " + e.getMessage());
                logger.error("Unable to get JWT Token", e);
            }
        } else {
            System.out.println("⚠️  No Authorization header or doesn't start with Bearer");
        }

        // Validar token y establecer contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                System.out.println("✅ Token is valid, setting authentication context");
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("❌ Token validation failed");
            }
        }

        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/forgot-password") ||
               path.equals("/reset-password") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/api/auth/");
    }
}