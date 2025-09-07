package com.ipas.ipas.presenter;
import com.ipas.ipas.view.dto.UserResponse;

import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.view.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserPresenter {
    
    @Autowired
    private UserService userService;
    
    public ResponseEntity<Map<String, Object>> handleGetAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.findAll();
            List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .toList();
            response.put("success", true);
            response.put("data", userResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleGetUser(Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isPresent()) {
                response.put("success", true);
                response.put("data", new UserResponse(userOpt.get()));
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleCreateUser(UserRequest userRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userService.existsByEmail(userRequest.getEmail())) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = new User();
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(User.UserRole.valueOf(userRequest.getRole()));
            
            User savedUser = userService.save(user);
            response.put("success", true);
            response.put("data", new UserResponse(savedUser));
            response.put("message", "User created successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleUpdateUser(Long id, UserRequest userRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("[UserPresenter] Update request for user id: " + id);
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("[UserPresenter] Editando usuario: " + user.getId() + " rol actual: " + user.getRole());
                user.setFirstName(userRequest.getFirstName());
                user.setLastName(userRequest.getLastName());
                user.setPhoneNumber(userRequest.getPhoneNumber());
                if (userRequest.getRole() != null) {
                    try {
                        user.setRole(User.UserRole.valueOf(userRequest.getRole()));
                    } catch (Exception ex) {
                        System.err.println("[UserPresenter] Error al asignar rol: " + userRequest.getRole());
                        response.put("success", false);
                        response.put("message", "Rol inválido: " + userRequest.getRole());
                        return ResponseEntity.badRequest().body(response);
                    }
                }
                User updatedUser = userService.update(user);
                System.out.println("[UserPresenter] Usuario actualizado: " + updatedUser.getId() + " nuevo rol: " + updatedUser.getRole());
                response.put("success", true);
                response.put("data", new UserResponse(updatedUser));
                response.put("message", "User updated successfully");
            } else {
                System.out.println("[UserPresenter] Usuario no encontrado para id: " + id);
                response.put("success", false);
                response.put("message", "User not found");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("[UserPresenter] Error updating user: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleDeleteUser(Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userService.findById(id).isPresent()) {
                userService.deleteUser(id);
                response.put("success", true);
                response.put("message", "User deleted successfully");
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleSearchUsers(String searchTerm) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> users = userService.searchUsers(searchTerm);
            List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .toList();
            response.put("success", true);
            response.put("data", userResponses);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}