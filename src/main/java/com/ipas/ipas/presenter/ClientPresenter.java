package com.ipas.ipas.presenter;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.ClientService;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.view.dto.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.ipas.ipas.view.dto.ClientSimpleDTO;

@Component
public class ClientPresenter {
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private UserService userService;
    
    public ResponseEntity<Map<String, Object>> handleGetAllClients(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Client> clients;
            User user = currentUser.get();
            
            if (user.getRole() == User.UserRole.ADMINISTRADOR) {
                clients = clientService.findAll();
            } else {
                clients = clientService.findByUser(user);
            }
            
            // Mapear a DTO simple para evitar problemas de serialización
            var clientDTOs = clients.stream().map(client -> new ClientSimpleDTO(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getDocumentNumber(),
                client.getDocumentType() != null ? client.getDocumentType().name() : null,
                client.getPhoneNumber()
            )).collect(Collectors.toList());
            response.put("success", true);
            response.put("data", clientDTOs);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching clients: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleGetClient(Long id, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> clientOpt = clientService.findById(id);
            
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                Optional<User> currentUser = userService.findByEmail(principal.getName());
                
                if (currentUser.isPresent()) {
                    User user = currentUser.get();
                    if (user.getRole() == User.UserRole.ADMINISTRADOR || 
                        client.getUser().getId().equals(user.getId())) {
                        response.put("success", true);
                        response.put("data", client);
                    } else {
                        response.put("success", false);
                        response.put("message", "Access denied");
                        return ResponseEntity.status(403).body(response);
                    }
                }
            } else {
                response.put("success", false);
                response.put("message", "Client not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching client: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleCreateClient(ClientRequest clientRequest, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (clientService.existsByDocumentNumber(clientRequest.getDocumentNumber())) {
                response.put("success", false);
                response.put("message", "Document number already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Client client = new Client();
            client.setDocumentNumber(clientRequest.getDocumentNumber());
            client.setDocumentType(Client.DocumentType.valueOf(clientRequest.getDocumentType()));
            client.setFirstName(clientRequest.getFirstName());
            client.setLastName(clientRequest.getLastName());
            client.setEmail(clientRequest.getEmail());
            client.setPhoneNumber(clientRequest.getPhoneNumber());
            client.setBirthDate(clientRequest.getBirthDate());
            client.setAddress(clientRequest.getAddress());
            client.setOccupation(clientRequest.getOccupation());
            client.setUser(currentUser.get());
            
            Client savedClient = clientService.save(client);
            response.put("success", true);
            response.put("data", savedClient);
            response.put("message", "Client created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating client: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleUpdateClient(Long id, ClientRequest clientRequest, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> clientOpt = clientService.findById(id);
            
            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                Optional<User> currentUser = userService.findByEmail(principal.getName());
                
                if (currentUser.isPresent()) {
                    User user = currentUser.get();
                    if (user.getRole() == User.UserRole.ADMINISTRADOR || 
                        client.getUser().getId().equals(user.getId())) {
                        
                        client.setFirstName(clientRequest.getFirstName());
                        client.setLastName(clientRequest.getLastName());
                        client.setEmail(clientRequest.getEmail());
                        client.setPhoneNumber(clientRequest.getPhoneNumber());
                        client.setBirthDate(clientRequest.getBirthDate());
                        client.setAddress(clientRequest.getAddress());
                        client.setOccupation(clientRequest.getOccupation());
                        
                        Client updatedClient = clientService.update(client);
                        response.put("success", true);
                        response.put("data", updatedClient);
                        response.put("message", "Client updated successfully");
                    } else {
                        response.put("success", false);
                        response.put("message", "Access denied");
                        return ResponseEntity.status(403).body(response);
                    }
                }
            } else {
                response.put("success", false);
                response.put("message", "Client not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating client: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleSearchClients(String searchTerm, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Client> clients = clientService.searchClients(searchTerm);
            
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isPresent() && currentUser.get().getRole() == User.UserRole.ASESOR) {
                clients = clients.stream()
                    .filter(client -> client.getUser().getId().equals(currentUser.get().getId()))
                    .toList();
            }
            
            response.put("success", true);
            response.put("data", clients);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching clients: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}