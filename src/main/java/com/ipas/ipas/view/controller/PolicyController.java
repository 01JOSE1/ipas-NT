package com.ipas.ipas.view.controller;

import com.ipas.ipas.presenter.PolicyPresenter;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<Map<String, Object>> createPolicy(
            @Valid @RequestBody PolicyRequest policyRequest,
            Principal principal) {
        return policyPresenter.handleCreatePolicy(policyRequest, principal);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASESOR')")
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
}