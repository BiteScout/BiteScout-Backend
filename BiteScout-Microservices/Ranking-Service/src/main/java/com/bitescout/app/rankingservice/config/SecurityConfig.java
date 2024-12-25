package com.bitescout.app.rankingservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // For preAuthorize annotation
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)  // Disable form login (new approach)
                .httpBasic(AbstractHttpConfigurer::disable);  // Disable HTTP Basic (new approach)
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        // Eğer JWT Authentication Filter'ı UsernamePasswordAuthenticationFilter'dan önce eklemeseydik, JWT Authentication Filter'ı çalışmayacaktı.
        // Auth-Servicedeki CustomAuthenticationProvider ile login işleminin nasıl gerçekleştiğini kontrol edebilirsiniz.
        // Burda sadece JWT Authentication Filter'ı ekleyerek, JWT ile giriş yapmayı sağlamış olduk.
        // Eğer Jwt hatalı ise
        return http.build();  // Final build step
    }
}

