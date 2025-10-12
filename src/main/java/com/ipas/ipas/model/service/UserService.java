package com.ipas.ipas.model.service;

import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    // @Autowired
    // private EmailService emailService;
    
    public enum AuthStatus {
        SUCCESS,
        INVALID_CREDENTIALS,
        INACTIVE_USER,
        USER_NOT_FOUND
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    public User update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public AuthStatus authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return AuthStatus.INVALID_CREDENTIALS;
            }
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                return AuthStatus.INACTIVE_USER;
            }
            return AuthStatus.SUCCESS;
        }
        return AuthStatus.USER_NOT_FOUND;
    }

    
    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    // public boolean generateResetPasswordToken(String email) {
    //     Optional<User> userOpt = userRepository.findByEmail(email);
    //     if (userOpt.isPresent()) {
    //         User user = userOpt.get();
    //         String token = UUID.randomUUID().toString();
    //         user.setResetPasswordToken(token);
    //         user.setResetPasswordExpires(LocalDateTime.now().plusHours(1));
    //         userRepository.save(user);
            
    //         // Send email with reset link
    //         emailService.sendPasswordResetEmail(user.getEmail(), token);
    //         return true;
    //     }
    //     return false;
    // }
    
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getResetPasswordExpires().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetPasswordToken(null);
                user.setResetPasswordExpires(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> searchUsers(String searchTerm) {
        List<User> usersByNameEmail = userRepository.findBySearchTerm(searchTerm);
        
        List<User> usersByRole = new ArrayList<>();
        try {
            User.UserRole role = User.UserRole.valueOf(searchTerm.toUpperCase());
            usersByRole = userRepository.findByRole(role);
        } catch (IllegalArgumentException e) {
            // Not a valid role, ignore
        }

        // Combine the lists and remove duplicates
        Set<User> combinedUsers = new LinkedHashSet<>(usersByNameEmail);
        combinedUsers.addAll(usersByRole);
        
        return new ArrayList<>(combinedUsers);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public void enableTwoFactor(Long userId, String secret) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTwoFactorEnabled(true);
            user.setTwoFactorSecret(secret);
            userRepository.save(user);
        }
    }
    
    public boolean disableTwoFactor(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTwoFactorEnabled(false);
            user.setTwoFactorSecret(null);
            userRepository.save(user);
        }
        return false;
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // @Autowired
    // private BCryptPasswordEncoder encoder;

    // @Bean
    // public CommandLineRunner testPassword() {
    //     return args -> {
    //         String raw = "admin123";
    //         String hash = encoder.encode(raw);
    //         System.out.println("Hash generado para admin123: " + hash);
    //     };
    // }


}
