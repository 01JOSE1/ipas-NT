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
import java.util.stream.Collectors;
import com.ipas.ipas.view.dto.PolicySimpleDTO;
import java.util.Optional;

@Component
public class PolicyPresenter {
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private UserService userService;
    
    public ResponseEntity<Map<String, Object>> handleGetAllPolicies(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> currentUser = userService.findByEmail(principal.getName());
            if (currentUser.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Policy> policies;
            User user = currentUser.get();
            
            if (user.getRole() == User.UserRole.ADMINISTRADOR) {
                policies = policyService.findAll();
            } else {
                List<Client> userClients = clientService.findByUser(user);
                policies = userClients.stream()
                    .flatMap(client -> policyService.findByClient(client).stream())
                    .toList();
            }
            
            // Mapear a DTO simple para evitar problemas de serialización
            var policyDTOs = policies.stream().map(policy -> new PolicySimpleDTO(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverage(),
                policy.getPremiumAmount(),
                policy.getCoverageAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus() != null ? policy.getStatus().name() : null
            )).collect(Collectors.toList());
            response.put("success", true);
            response.put("data", policyDTOs);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching policies: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
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
                        response.put("success", true);
                        response.put("data", policy);
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
            
            Policy savedPolicy = policyService.save(policy);
            response.put("success", true);
            response.put("data", savedPolicy);
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
                        response.put("success", true);
                        response.put("data", updatedPolicy);
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
            
            response.put("success", true);
            response.put("data", policies);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching policies: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}