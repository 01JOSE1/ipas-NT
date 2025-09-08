
package com.ipas.ipas.presenter;

import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.security.JwtUtil;
import com.ipas.ipas.view.dto.LoginRequest;
import com.ipas.ipas.view.dto.LoginResponse;
import com.ipas.ipas.view.dto.PasswordResetRequest;
import com.ipas.ipas.view.dto.UserSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthPresenter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<Map<String, Object>> handleLogin(LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("Intentando login con email: " + loginRequest.getEmail());

        try {
            UserService.AuthStatus authStatus = userService.authenticate(
                    loginRequest.getEmail(),
                    loginRequest.getPassword());

            if (authStatus == UserService.AuthStatus.SUCCESS) {
                Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (user.getTwoFactorEnabled() &&
                            (loginRequest.getTwoFactorCode() == null || loginRequest.getTwoFactorCode().isEmpty())) {
                        response.put("success", false);
                        response.put("message", "Two factor authentication required");
                        response.put("requiresTwoFactor", true);
                        return ResponseEntity.ok(response);
                    }
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
                    System.out.println("Token generado: " + token);
                    userService.updateLastLogin(user.getId());
                    UserSimpleDTO userDto = new UserSimpleDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole()
                    );
                    Map<String, Object> data = new HashMap<>();
                    data.put("token", token);
                    data.put("user", userDto);
                    response.put("success", true);
                    response.put("data", data);
                    response.put("message", "Login successful");
                    System.out.println("Login response: " + response);
                    return ResponseEntity.ok(response);
                }
            } else {
                response.put("success", false);
                if (authStatus == UserService.AuthStatus.INACTIVE_USER) {
                    response.put("message", "Usuario inactivo");
                } else {
                    response.put("message", "Credenciales incorrectas");
                }
                return ResponseEntity.badRequest().body(response);
            }
            // This part should be unreachable, but as a fallback:
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login error: " + e.getMessage());
            System.err.println("Login error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> handlePasswordReset(PasswordResetRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean emailSent = userService.generateResetPasswordToken(request.getEmail());

            if (emailSent) {
                response.put("success", true);
                response.put("message", "Password reset email sent");
            } else {
                response.put("success", false);
                response.put("message", "Email not found");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> handleNewPassword(String token, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean passwordReset = userService.resetPassword(token, newPassword);

            if (passwordReset) {
                response.put("success", true);
                response.put("message", "Password updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating password: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

        public ResponseEntity<Map<String, Object>> handleRegister(com.ipas.ipas.view.dto.UserRequest userRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userService.existsByEmail(userRequest.getEmail())) {
                response.put("success", false);
                response.put("message", "El correo ya está registrado");
                return ResponseEntity.badRequest().body(response);
            }
            User user = new User();
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setRole(User.UserRole.REGISTRADO);
            user.setStatus(User.UserStatus.ACTIVE);
            userService.save(user);
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}