package com.noxas.reparaciones.config;

import com.noxas.reparaciones.domain.Usuario;
import com.noxas.reparaciones.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsername(adminUsername).isPresent()) {
            return; // ya existe, no lo recreamos en cada arranque
        }

        if (adminPassword.isBlank()) {
            return; // sin password configurada, no creamos nada
        }

        Usuario admin = Usuario.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .rol("ADMIN")
                .build();

        usuarioRepository.save(admin);
    }
}