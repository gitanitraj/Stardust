package com.zipcode.stardust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/subforum", "/loginform", "/viewpost", "/action_login",
                        "/action_createaccount", "/static/**", "/style.css").permitAll()
                .requestMatchers("/addpost", "/action_post", "/action_comment", "/action_reaction")
                        .authenticated()
                .requestMatchers("/action_deletepost", "/action_deletecomment", "/action_lockthread")
                        .hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/loginform")
                .loginProcessingUrl("/action_login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/loginform?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/action_logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}
