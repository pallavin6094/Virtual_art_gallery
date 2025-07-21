package com.virtualartgallery.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    String authorizationHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;
    String role = null;

    System.out.println("🔍 Incoming Request: " + request.getRequestURI());

    try {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(token);
            role = jwtUtil.extractUserRole(token);  // 🔹 Extract role from JWT

            System.out.println("✅ JWT Token Found: " + token);
            System.out.println("✅ Extracted Username: " + username);
            System.out.println("✅ Extracted Role: " + role);
        } else {
            System.out.println("❌ No JWT Token in Request Headers");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                System.out.println("✅ JWT Token is valid, setting authentication");

                // 🔹 Create authority from extracted role
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("❌ Invalid JWT Token");
            }
        }
    } catch (ExpiredJwtException e) {
        System.out.println("❌ JWT Token has expired: " + e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT Token has expired");
        return;
    } catch (SecurityException e) {
        System.out.println("❌ JWT Signature validation failed: " + e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid JWT signature");
        return;
    } catch (Exception e) {
        System.out.println("❌ JWT Authentication Error: " + e.getMessage());
    }

    chain.doFilter(request, response);
}

}