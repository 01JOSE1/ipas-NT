
package com.ipas.ipas.view.controller;

import com.ipas.ipas.presenter.PolicyPresenter;
import com.ipas.ipas.view.dto.PolicyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "*")
public class PolicyController {
    // Endpoint para obtener pólizas asociadas a un cliente específico
    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Map<String, Object>> getPoliciesByClient(@PathVariable Long clientId) {
        return policyPresenter.handleGetPoliciesByClient(clientId);
    }
    
    @Autowired
    private PolicyPresenter policyPresenter;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPolicies(Principal principal) {
        return policyPresenter.handleGetAllPolicies(principal);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPolicy(@PathVariable Long id, Principal principal) {
        return policyPresenter.handleGetPolicy(id, principal);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPolicy(
            @Valid @RequestBody PolicyRequest policyRequest,
            Principal principal) {
        return policyPresenter.handleCreatePolicy(policyRequest, principal);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody PolicyRequest policyRequest,
            Principal principal) {
        return policyPresenter.handleUpdatePolicy(id, policyRequest, principal);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPolicies(
            @RequestParam String q,
            Principal principal) {
        return policyPresenter.handleSearchPolicies(q, principal);
    }
    // Endpoint para obtener pólizas asociadas a los clientes de un usuario específico
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Map<String, Object>> getPoliciesByUser(@PathVariable Long userId) {
        return policyPresenter.handleGetPoliciesByUser(userId);
    }
}