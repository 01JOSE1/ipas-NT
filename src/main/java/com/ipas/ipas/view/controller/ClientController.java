package com.ipas.ipas.view.controller;

import com.ipas.ipas.presenter.ClientPresenter;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ipas.ipas.view.dto.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {
    
    @Autowired
    private ClientPresenter clientPresenter;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClients(Principal principal) {
        return clientPresenter.handleGetAllClients(principal);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClient(@PathVariable Long id, Principal principal) {
        return clientPresenter.handleGetClient(id, principal);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<Map<String, Object>> createClient(
            @Valid @RequestBody ClientRequest clientRequest,
            Principal principal) {
        return clientPresenter.handleCreateClient(clientRequest, principal);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<Map<String, Object>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest clientRequest,
            Principal principal) {
        return clientPresenter.handleUpdateClient(id, clientRequest, principal);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchClients(
            @RequestParam String q,
            Principal principal) {
        return clientPresenter.handleSearchClients(q, principal);
    }
}