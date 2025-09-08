
package com.ipas.ipas.view.controller;
import com.ipas.ipas.view.dto.UserRequest;
import com.ipas.ipas.presenter.AuthPresenter;
import com.ipas.ipas.view.dto.LoginRequest;
import com.ipas.ipas.view.dto.PasswordResetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthPresenter authPresenter;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authPresenter.handleLogin(loginRequest);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        return authPresenter.handlePasswordReset(request);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        return authPresenter.handleNewPassword(token, newPassword);
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRequest userRequest) {
        return authPresenter.handleRegister(userRequest);
    }
}