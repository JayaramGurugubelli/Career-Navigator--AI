package careerpilot_parent.security.config;
import careerpilot_parent.security.filter.JwtAuthenticationFilter;
import careerpilot_parent.security.handler.JwtAccessDeniedHandler;
import careerpilot_parent.security.handler.JwtAuthenticationEntryPoint;
import careerpilot_parent.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    private final CustomUserDetailsService
            customUserDetailsService;

    private final JwtAuthenticationEntryPoint
            authenticationEntryPoint;

    private final JwtAccessDeniedHandler
            accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                customUserDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf ->
                        csrf.disable()
                )

                .cors(
                        Customizer.withDefaults()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler
                                )
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .authorizeHttpRequests(auth ->
                        auth

                                /*
                                 * CORS preflight requests
                                 */
                                .requestMatchers(
                                        HttpMethod.OPTIONS,
                                        "/**"
                                )
                                .permitAll()

                                /*
                                 * Public authentication endpoints
                                 */
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/auth/register",
                                        "/api/auth/recruiter/register",
                                        "/api/auth/login",
                                        "/api/auth/logout",
                                        "/api/auth/verify-email",
                                        "/api/auth/resend-verification",
                                        "/api/auth/forgot-password",
                                        "/api/auth/reset-password",
                                        "/api/auth/refresh-token"
                                )
                                .permitAll()

                                /*
                                 * Email verification link
                                 */
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/auth/verify-email-link"
                                )
                                .permitAll()

                                /*
                                 * Swagger/OpenAPI endpoints
                                 */
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                )
                                .permitAll()

                                /*
                                 * WebSocket and SockJS handshake endpoints.
                                 *
                                 * Authentication for STOMP messages can later
                                 * be handled through a ChannelInterceptor.
                                 */
                                .requestMatchers(
                                        "/ws",
                                        "/ws/**"
                                )
                                .permitAll()

                                /*
                                 * Public job-search endpoints
                                 */
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/jobs/**"
                                )
                                .permitAll()

                                /*
                                 * Public interview experience endpoints.
                                 *
                                 * This allows:
                                 * GET /api/interview-experiences
                                 * GET /api/interview-experiences/{id}
                                 * GET /api/interview-experiences/{id}/comments
                                 * GET /api/interview-experiences/{id}/comments/{commentId}/replies
                                 *
                                 * Protected methods such as /my and like/state
                                 * remain protected by @PreAuthorize.
                                 */
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/interview-experiences/**"
                                )
                                .permitAll()

                                /*
                                 * Admin-only endpoints
                                 */
                                .requestMatchers(
                                        "/api/admin/**"
                                )
                                .hasRole("ADMIN")

                                /*
                                 * Recruiter-only endpoints
                                 */
                                .requestMatchers(
                                        "/api/recruiter/**"
                                )
                                .hasRole("RECRUITER")

                                /*
                                 * Student-only endpoints
                                 */
                                .requestMatchers(
                                        "/api/student/**"
                                )
                                .hasRole("STUDENT")

                                /*
                                 * All other endpoints require authentication
                                 */
                                .anyRequest()
                                .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Angular frontend and WebSocket CORS configuration.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With",
                        "Cache-Control",
                        "Pragma"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
