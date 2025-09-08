
package com.ipas.ipas.presenter;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.Policy;
import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.service.ClientService;
import com.ipas.ipas.model.service.PolicyService;
import com.ipas.ipas.model.service.UserService;
import com.ipas.ipas.view.dto.PolicyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.ipas.ipas.view.dto.PolicySimpleDTO;

@Component
public class PolicyPresenter {
    // Obtener pólizas asociadas a un cliente específico
    public ResponseEntity<Map<String, Object>> handleGetPoliciesByClient(Long clientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Client> clientOpt = clientService.findById(clientId);
            if (clientOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Cliente no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            Client client = clientOpt.get();
            List<Policy> policies = policyService.findByClient(client);
            var policyDTOs = policies.stream().map(policy -> new PolicySimpleDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverage(),
                policy.getPremiumAmount(),
                policy.getCoverageAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus() != null ? policy.getStatus().name() : null,
                policy.getClient() != null ? policy.getClient().getId() : null,
                policy.getDeductible(),
                policy.getBeneficiaries(),
                policy.getTermsConditions(),
                policy.getClient() != null ? (policy.getClient().getFirstName() + " " + policy.getClient().getLastName()) : null
            )).toList();
            response.put("success", true);
            response.put("data", policyDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener pólizas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    // Método para obtener todas las pólizas
    public ResponseEntity<Map<String, Object>> handleGetAllPolicies(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Policy> policies = policyService.findAll();
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isPresent() && currentUser.get().getRole() == User.UserRole.ASESOR) {
                User user = currentUser.get();
                List<Client> userClients = clientService.findByUser(user);
                policies = policies.stream()
                    .filter(policy -> userClients.contains(policy.getClient()))
                    .toList();
            }
            var policyDTOs = policies.stream().map(policy -> new PolicySimpleDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverage(),
                policy.getPremiumAmount(),
                policy.getCoverageAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus() != null ? policy.getStatus().name() : null,
                policy.getClient() != null ? policy.getClient().getId() : null,
                policy.getDeductible(),
                policy.getBeneficiaries(),
                policy.getTermsConditions(),
                policy.getClient() != null ? (policy.getClient().getFirstName() + " " + policy.getClient().getLastName()) : null
            )).toList();
            response.put("success", true);
            response.put("data", policyDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener pólizas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    // Obtener pólizas asociadas a los clientes de un usuario específico
    public ResponseEntity<Map<String, Object>> handleGetPoliciesByUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            User user = userOpt.get();
            List<Client> clients = clientService.findByUser(user);
            List<Policy> policies = policyService.findByClients(clients);
            var policyDTOs = policies.stream().map(policy -> new PolicySimpleDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverage(),
                policy.getPremiumAmount(),
                policy.getCoverageAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus() != null ? policy.getStatus().name() : null,
                policy.getClient() != null ? policy.getClient().getId() : null,
                policy.getDeductible(),
                policy.getBeneficiaries(),
                policy.getTermsConditions(),
                policy.getClient() != null ? (policy.getClient().getFirstName() + " " + policy.getClient().getLastName()) : null
            )).toList();
            response.put("success", true);
            response.put("data", policyDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener pólizas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ...otros métodos existentes correctamente estructurados...
    
    public ResponseEntity<Map<String, Object>> handleGetPolicy(Long id, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Policy> policyOpt = policyService.findById(id);
            
            if (policyOpt.isPresent()) {
                Policy policy = policyOpt.get();
                Optional<User> currentUser = userService.findByEmail(principal.getName());
                
                if (currentUser.isPresent()) {
                    User user = currentUser.get();
                    if (user.getRole() == User.UserRole.ADMINISTRADOR || 
                        policy.getClient().getUser().getId().equals(user.getId())) {
                        PolicySimpleDTO policyDTO = new PolicySimpleDTO(
                            policy.getId(),
                            policy.getPolicyNumber(),
                            policy.getPolicyType(),
                            policy.getCoverage(),
                            policy.getPremiumAmount(),
                            policy.getCoverageAmount(),
                            policy.getStartDate(),
                            policy.getEndDate(),
                            policy.getStatus() != null ? policy.getStatus().name() : null,
                            policy.getClient() != null ? policy.getClient().getId() : null,
                            policy.getDeductible(),
                            policy.getBeneficiaries(),
                            policy.getTermsConditions(),
                            policy.getClient() != null ? (policy.getClient().getFirstName() + " " + policy.getClient().getLastName()) : null
                        );
                        response.put("success", true);
                        response.put("data", policyDTO);
                    } else {
                        response.put("success", false);
                        response.put("message", "Access denied");
                        return ResponseEntity.status(403).body(response);
                    }
                }
            } else {
                response.put("success", false);
                response.put("message", "Policy not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching policy: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleCreatePolicy(PolicyRequest policyRequest, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> clientOpt = clientService.findById(policyRequest.getClientId());
            if (clientOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Client not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Client client = clientOpt.get();
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                if (user.getRole() == User.UserRole.ASESOR && 
                    !client.getUser().getId().equals(user.getId())) {
                    response.put("success", false);
                    response.put("message", "Access denied");
                    return ResponseEntity.status(403).body(response);
                }
            }
            
            Policy policy = new Policy();
            policy.setPolicyType(policyRequest.getPolicyType());
            policy.setCoverage(policyRequest.getCoverage());
            policy.setPremiumAmount(policyRequest.getPremiumAmount());
            policy.setCoverageAmount(policyRequest.getCoverageAmount());
            policy.setStartDate(policyRequest.getStartDate());
            policy.setEndDate(policyRequest.getEndDate());
            policy.setDeductible(policyRequest.getDeductible());
            policy.setBeneficiaries(policyRequest.getBeneficiaries());
            policy.setTermsConditions(policyRequest.getTermsConditions());
            policy.setClient(client);
            if (policyRequest.getPolicyNumber() != null && !policyRequest.getPolicyNumber().isEmpty()) {
                policy.setPolicyNumber(policyRequest.getPolicyNumber());
            }
            if (policyRequest.getPolicyNumber() != null && !policyRequest.getPolicyNumber().isEmpty()) {
                policy.setPolicyNumber(policyRequest.getPolicyNumber());
            }
            
            Policy savedPolicy = policyService.save(policy);
            PolicySimpleDTO policyDTO = new PolicySimpleDTO(
                savedPolicy.getId(),
                savedPolicy.getPolicyNumber(),
                savedPolicy.getPolicyType(),
                savedPolicy.getCoverage(),
                savedPolicy.getPremiumAmount(),
                savedPolicy.getCoverageAmount(),
                savedPolicy.getStartDate(),
                savedPolicy.getEndDate(),
                savedPolicy.getStatus() != null ? savedPolicy.getStatus().name() : null,
                savedPolicy.getClient() != null ? savedPolicy.getClient().getId() : null,
                savedPolicy.getDeductible(),
                savedPolicy.getBeneficiaries(),
                savedPolicy.getTermsConditions(),
                savedPolicy.getClient() != null ? (savedPolicy.getClient().getFirstName() + " " + savedPolicy.getClient().getLastName()) : null
            );
            response.put("success", true);
            response.put("data", policyDTO);
            response.put("message", "Policy created successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating policy: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleUpdatePolicy(Long id, PolicyRequest policyRequest, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Policy> policyOpt = policyService.findById(id);
            
            if (policyOpt.isPresent()) {
                Policy policy = policyOpt.get();
                Optional<User> currentUser = userService.findByEmail(principal.getName());
                
                if (currentUser.isPresent()) {
                    User user = currentUser.get();
                    if (user.getRole() == User.UserRole.ADMINISTRADOR || 
                        policy.getClient().getUser().getId().equals(user.getId())) {
                        
                        policy.setPolicyType(policyRequest.getPolicyType());
                        policy.setCoverage(policyRequest.getCoverage());
                        policy.setPremiumAmount(policyRequest.getPremiumAmount());
                        policy.setCoverageAmount(policyRequest.getCoverageAmount());
                        policy.setStartDate(policyRequest.getStartDate());
                        policy.setEndDate(policyRequest.getEndDate());
                        policy.setDeductible(policyRequest.getDeductible());
                        policy.setBeneficiaries(policyRequest.getBeneficiaries());
                        policy.setTermsConditions(policyRequest.getTermsConditions());
                        
                        Policy updatedPolicy = policyService.update(policy);
                        PolicySimpleDTO policyDTO = new PolicySimpleDTO(
                            updatedPolicy.getId(),
                            updatedPolicy.getPolicyNumber(),
                            updatedPolicy.getPolicyType(),
                            updatedPolicy.getCoverage(),
                            updatedPolicy.getPremiumAmount(),
                            updatedPolicy.getCoverageAmount(),
                            updatedPolicy.getStartDate(),
                            updatedPolicy.getEndDate(),
                            updatedPolicy.getStatus() != null ? updatedPolicy.getStatus().name() : null,
                            updatedPolicy.getClient() != null ? updatedPolicy.getClient().getId() : null,
                            updatedPolicy.getDeductible(),
                            updatedPolicy.getBeneficiaries(),
                            updatedPolicy.getTermsConditions(),
                            updatedPolicy.getClient() != null ? (updatedPolicy.getClient().getFirstName() + " " + updatedPolicy.getClient().getLastName()) : null
                        );
                        response.put("success", true);
                        response.put("data", policyDTO);
                        response.put("message", "Policy updated successfully");
                    } else {
                        response.put("success", false);
                        response.put("message", "Access denied");
                        return ResponseEntity.status(403).body(response);
                    }
                }
            } else {
                response.put("success", false);
                response.put("message", "Policy not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating policy: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    public ResponseEntity<Map<String, Object>> handleSearchPolicies(String searchTerm, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Policy> policies = policyService.searchPolicies(searchTerm);

            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isPresent() && currentUser.get().getRole() == User.UserRole.ASESOR) {
                User user = currentUser.get();
                List<Client> userClients = clientService.findByUser(user);
                policies = policies.stream()
                    .filter(policy -> userClients.contains(policy.getClient()))
                    .toList();
            }

            // Mapear a DTO simple para evitar problemas de serialización y mantener consistencia con el frontend
            var policyDTOs = policies.stream().map(policy -> new PolicySimpleDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverage(),
                policy.getPremiumAmount(),
                policy.getCoverageAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus() != null ? policy.getStatus().name() : null,
                policy.getClient() != null ? policy.getClient().getId() : null,
                policy.getDeductible(),
                policy.getBeneficiaries(),
                policy.getTermsConditions(),
                policy.getClient() != null ? (policy.getClient().getFirstName() + " " + policy.getClient().getLastName()) : null
            )).collect(java.util.stream.Collectors.toList());
            response.put("success", true);
            response.put("data", policyDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching policies: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
