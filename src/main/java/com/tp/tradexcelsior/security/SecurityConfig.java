package com.tp.tradexcelsior.security;

import com.tp.tradexcelsior.filters.JwtAuthenticationFilter;
import com.tp.tradexcelsior.service.impl.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

  private UserDetailsService userDetailsService;

  private JwtAuthenticationEntryPoint authenticationEntryPoint;

  private JwtAuthenticationFilter authenticationFilter;

  @Bean
  public static PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((authorize) -> {
          // Allowing USER role to access specific APIs
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/reference").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/reference/{id}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/core-watchlist/{id}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/core-watchlist").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/core-watchlist/search").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/checklist").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/checklist/{id}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/book").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/book/{id}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/book/search").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/users/search").hasAnyRole("ADMIN");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/users/status").hasAnyRole("ADMIN");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.PUT, "/api/v1/users/set-password/{userId}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.PUT, "/api/v1/users/reset-password/{userId}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/success-stories/{userName}").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/api/v1/success-stories").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.POST, "/api/v1/support").hasAnyRole("ADMIN", "USER");
          authorize.requestMatchers(HttpMethod.GET, "/images/id/{imageId}").hasAnyRole("ADMIN", "USER");


          // Allowing ADMIN role to access ALL APIs
          authorize.requestMatchers(HttpMethod.GET, "/api/**").hasRole("ADMIN");    // GET All APIs
          authorize.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN");   // POST All APIs
          authorize.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN");    // PUT All APIs
          authorize.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN"); // DELETE All APIs
          authorize.requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN");  // PATCH All APIs

          authorize.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
          authorize.requestMatchers("/swagger-ui/**").permitAll();
          authorize.requestMatchers("/v3/api-docs/**").permitAll();

          authorize.anyRequest().authenticated();
        }).httpBasic(Customizer.withDefaults());

    http.exceptionHandling( exception -> exception
        .authenticationEntryPoint(authenticationEntryPoint));

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

}