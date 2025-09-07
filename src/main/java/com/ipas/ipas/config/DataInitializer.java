package com.ipas.ipas.config;

import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userService.existsByEmail("admin@ipas.com")) {
            User admin = new User();
            admin.setEmail("admin@ipas.com");
            admin.setPassword("admin123");
            admin.setFirstName("Administrador");
            admin.setLastName("Sistema");
            admin.setRole(User.UserRole.ADMINISTRADOR);
            admin.setStatus(User.UserStatus.ACTIVE);
            
            userService.save(admin);
            System.out.println("Admin user created: admin@ipas.com / admin123");
        }
        
        // Create default asesor user if not exists
        if (!userService.existsByEmail("asesor@ipas.com")) {
            User asesor = new User();
            asesor.setEmail("asesor@ipas.com");
            asesor.setPassword("asesor123");
            asesor.setFirstName("Carlos");
            asesor.setLastName("Mendoza");
            asesor.setRole(User.UserRole.ASESOR);
            asesor.setStatus(User.UserStatus.ACTIVE);
            
            userService.save(asesor);
            System.out.println("Asesor user created: asesor@ipas.com / asesor123");
        }
    }
}