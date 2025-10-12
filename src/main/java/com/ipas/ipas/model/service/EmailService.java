package com.ipas.ipas.model.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.stereotype.Service;

// @Service
// public class EmailService {
    
//     @Autowired
//     private JavaMailSender mailSender;
    
//     public void sendPasswordResetEmail(String to, String token) {
//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(to);
//         message.setSubject("IPAS - Recuperación de Contraseña");
//         message.setText("Para restablecer su contraseña, haga clic en el siguiente enlace:\n" +
//                        "http://localhost:8080/reset-password?token=" + token + "\n\n" +
//                        "Este enlace expirará en 1 hora.\n\n" +
//                        "Si no solicitó este cambio, ignore este correo.");
        
//         mailSender.send(message);
//     }
    
//     public void sendWelcomeEmail(String to, String firstName) {
//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(to);
//         message.setSubject("Bienvenido a IPAS");
//         message.setText("Hola " + firstName + ",\n\n" +
//                        "Bienvenido al Sistema de Gestión de Pólizas de Seguros IPAS.\n" +
//                        "Su cuenta ha sido creada exitosamente.\n\n" +
//                        "Saludos,\n" +
//                        "Equipo IPAS");
        
//         mailSender.send(message);
//     }
// }