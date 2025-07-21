package com.virtualartgallery.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("🚀 Security Configuration Loaded");
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Add this line to enable CORS
                                                                                   // configuration
                .csrf(csrf -> csrf.disable()) // Disable CSRF for JWT authentication
                .authorizeHttpRequests(auth -> auth

                        // User Authentication & Profile Access
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                        .requestMatchers("/api/users/profile/**").authenticated()

                        // Artwork Access
                        .requestMatchers(HttpMethod.GET, "/api/artworks/**").permitAll() // Publicly visible
                        .requestMatchers(HttpMethod.GET, "/api/artworks/artist/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/artworks/upload").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.PUT, "/api/artworks/**").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/artworks/**").hasAnyAuthority("ARTIST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/artworks/title/**").hasAnyAuthority("ARTIST", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/artist/**").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.GET, "/api/artworks/download/**").hasAnyAuthority("BUYER")

                        // ✅ Order-Related Access
                        .requestMatchers(HttpMethod.POST, "/api/orders/place").hasAuthority("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/buyer").hasAuthority("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/earnings").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.GET, "/api/orders/buyer/purchased-artworks").hasAuthority("BUYER")



                        
                        // ✅ Add security for checkout single item
                        .requestMatchers(HttpMethod.POST, "/api/orders/checkout/single/{cartItemId}")
                        .hasAuthority("BUYER")

                        // Cart Access
                        .requestMatchers(HttpMethod.POST, "/api/cart/add").hasAuthority("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/cart/**").hasAuthority("BUYER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/remove/**").hasAuthority("BUYER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/clear").hasAuthority("BUYER")

                        // Buyer Profile Protection
                        .requestMatchers("/api/buyers/create").authenticated() // Only authenticated users can create
                                                                               // buyer profiles
                        .requestMatchers(HttpMethod.GET, "/api/buyers/{username}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/buyers/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/buyers/update").hasAuthority("BUYER")

                        // Artist Profile Protection
                        .requestMatchers("/api/artists/create").authenticated() // Only authenticated users can create
                                                                                // artist profiles
                        .requestMatchers(HttpMethod.GET, "/api/artists/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/artists/me").permitAll()


                        .requestMatchers(HttpMethod.PUT, "/api/artists/update/**").hasAuthority("ARTIST")

                        // Payments Related
                        .requestMatchers(HttpMethod.POST, "/api/payments/system-update").hasAuthority("PAYMENT_SYSTEM") // Payment
                                                                                                                        // system
                                                                                                                        // auto-updates
                        .requestMatchers(HttpMethod.POST, "/api/payments/create-payment-intent").authenticated() // Authenticated
                                                                                                                 // users
                                                                                                                 // can
                                                                                                                 // initiate
                                                                                                                 // payment
                        .requestMatchers(HttpMethod.GET, "/api/payments/{orderId}").authenticated() // Authenticated
                                                                                                    // users can view
                                                                                                    // payment details
                        .requestMatchers(HttpMethod.POST, "/api/payments/confirm").authenticated() // Only authenticated
                                                                                                   // users (with JWT)
                                                                                                   // can confirm
                                                                                                   // payment

                        // Webhook Endpoint (Allow all without authentication)
                        .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()

                        // 🛠️ ✅ Admin Dashboard Access
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard/stats").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/dashboard/users/{userId}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard/orders").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/dashboard/orders/{orderId}/status/{status}")
                        .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard/artworks").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/dashboard/artworks/{artworkId}")
                        .hasAuthority("ADMIN")

                        // ✅ Block any other admin endpoint by default
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Default protection
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No
                                                                                                              // sessions
                .authenticationProvider(authenticationProvider()) // Use custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Apply JWT
                                                                                                       // filter

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 🔥 Required!

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
