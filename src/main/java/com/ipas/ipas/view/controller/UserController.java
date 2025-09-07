package com.ipas.ipas.view.controller;

import com.ipas.ipas.presenter.UserPresenter;
import com.ipas.ipas.view.dto.UserRequest;
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
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequest userRequest) {
        return userPresenter.handleCreateUser(userRequest);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        return userPresenter.handleUpdateUser(id, userRequest);
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
}