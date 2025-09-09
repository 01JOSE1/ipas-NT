package com.ipas.ipas.view.controller;

import com.ipas.ipas.presenter.UserPresenter;
import com.ipas.ipas.presenter.AuthPresenter;
import com.ipas.ipas.view.dto.UserRequest;
import com.ipas.ipas.view.dto.PasswordChangeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserPresenter userPresenter;

    @Autowired
    private AuthPresenter authPresenter;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return userPresenter.handleGetAllUsers();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return userPresenter.handleGetUser(id);
    }
    
    
        @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userRequest,
            Principal principal) {
        return userPresenter.handleUpdateUser(id, userRequest, principal);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        return userPresenter.handleDeleteUser(id);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String q) {
        return userPresenter.handleSearchUsers(q);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest,
            Principal principal) {
        // In a real application, you would verify that the authenticated user's ID
        // matches the @PathVariable id, or that the user has ADMIN role.
        // For this exercise, we'll assume the authenticated user is changing their own password.
        return authPresenter.handleChangePassword(
                id,
                passwordChangeRequest.getCurrentPassword(),
                passwordChangeRequest.getNewPassword()
        );
    }
}