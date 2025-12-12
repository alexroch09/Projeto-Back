package br.edu.lampi.infrareport.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.edu.lampi.infrareport.service.exceptions.UnauthorizedException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf)-> csrf.disable())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/teams/**").authenticated()
                        .requestMatchers( "/teams/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/bulletins/**").authenticated()
                        .requestMatchers( "/bulletins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/category/**").authenticated()
                        .requestMatchers( "/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/call_status/**").authenticated()
                        .requestMatchers( "/call_status/**").hasRole("ADMIN")
                        .requestMatchers( HttpMethod.POST, "/calls/search_by_filter").hasRole("ADMIN")
                        .requestMatchers( HttpMethod.POST, "/calls/csv_file").hasRole("ADMIN")
                        .requestMatchers( HttpMethod.PUT, "/calls/**").hasRole("ADMIN")
                        .requestMatchers( HttpMethod.POST, "/calls").hasRole("COMMON")
                        .requestMatchers(HttpMethod.GET,
                                                    "/v3/api-docs/**",
                                                    "/swagger-ui/**",
                                                    "/swagger-ui.html")
                                                    .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                                    "/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new UnauthorizedException()))
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}