package com.ipas.ipas.presenter;
import com.ipas.ipas.view.dto.UserResponse;

import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.view.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.security.Principal;
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
            // Filtrar el usuario actual si es admin
            // Obtener el usuario actual del contexto de seguridad
            final String[] currentEmailHolder = new String[1];
            currentEmailHolder[0] = null;
            try {
                currentEmailHolder[0] = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            } catch (Exception ex) {}
            final String currentEmail = currentEmailHolder[0];
            List<UserResponse> userResponses = users.stream()
                .filter(u -> currentEmail == null || !u.getEmail().equalsIgnoreCase(currentEmail))
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
    
    public ResponseEntity<Map<String, Object>> handleUpdateUser(Long id, UserRequest userRequest, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> authenticatedUserOpt = userService.findByEmail(principal.getName());
            if (authenticatedUserOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Authenticated user not found");
                return ResponseEntity.status(403).body(response);
            }

            User authenticatedUser = authenticatedUserOpt.get();
            boolean isAdmin = authenticatedUser.getRole() == User.UserRole.ADMINISTRADOR;

            if (!isAdmin && !authenticatedUser.getId().equals(id)) {
                response.put("success", false);
                response.put("message", "You are not authorized to update this user");
                return ResponseEntity.status(403).body(response);
            }

            Optional<User> userToUpdateOpt = userService.findById(id);
            if (userToUpdateOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User to update not found");
                return ResponseEntity.status(404).body(response);
            }

            User userToUpdate = userToUpdateOpt.get();

            // Update personal info
            if (userRequest.getFirstName() != null) {
                userToUpdate.setFirstName(userRequest.getFirstName());
            }
            if (userRequest.getLastName() != null) {
                userToUpdate.setLastName(userRequest.getLastName());
            }
            if (userRequest.getPhoneNumber() != null) {
                userToUpdate.setPhoneNumber(userRequest.getPhoneNumber());
            }

            // Admin-only updates
            if (isAdmin) {
                if (userRequest.getRole() != null) {
                    userToUpdate.setRole(User.UserRole.valueOf(userRequest.getRole()));
                }
                if (userRequest.getStatus() != null) {
                    userToUpdate.setStatus(User.UserStatus.valueOf(userRequest.getStatus()));
                }
            }

            User updatedUser = userService.update(userToUpdate);
            response.put("success", true);
            response.put("data", new UserResponse(updatedUser));
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
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