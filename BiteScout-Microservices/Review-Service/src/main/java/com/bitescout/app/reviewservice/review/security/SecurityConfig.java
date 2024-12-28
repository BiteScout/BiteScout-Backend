package com.bitescout.app.reviewservice.review.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // For preAuthorize annotation
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/reviews").authenticated()
                        .anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)  // Disable form login (new approach)
                .httpBasic(AbstractHttpConfigurer::disable)  // Disable HTTP Basic (new approach)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        // Eğer JWT Authentication Filter'ı UsernamePasswordAuthenticationFilter'dan önce eklemeseydik, JWT Authentication Filter'ı çalışmayacaktı.
        // Auth-Servicedeki CustomAuthenticationProvider ile login işleminin nasıl gerçekleştiğini kontrol edebilirsiniz.
        // Burda sadece JWT Authentication Filter'ı ekleyerek, JWT ile giriş yapmayı sağlamış olduk.
        // Eğer Jwt hatalı ise
        return http.build();  // Final build step
    }
}

