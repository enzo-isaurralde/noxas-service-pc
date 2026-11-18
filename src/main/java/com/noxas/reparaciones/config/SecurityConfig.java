package com.noxas.reparaciones.config;

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

    // BCrypt: hashea la contraseña con salt automático, nunca se guarda en texto plano
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // La API pública que consume la landing: cotización y creación de consultas
                        .requestMatchers("/api/**").permitAll()
                        // Recursos estáticos de la landing y del panel
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Página de login del panel, accesible sin loguearse
                        .requestMatchers("/admin/login").permitAll()
                        // Todo lo demás bajo /admin/** requiere estar logueado
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/consultas", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .permitAll()
                )
                // La API es stateless y no usa formularios de Spring, así que
                // se desactiva CSRF solo ahí (el panel Thymeleaf sí lo necesita)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }
}