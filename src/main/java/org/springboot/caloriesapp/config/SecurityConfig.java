package org.springboot.caloriesapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/login", "/api/register").permitAll()
                                .anyRequest().permitAll()
                );
        //TODO: Implement session authentication

//                .formLogin(form -> form
//                        .loginPage("/api/login")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/api/logout")
//                        .logoutSuccessUrl("/api/login?logout")
//                        .permitAll()
//                );
        return http.build();
    }

}
