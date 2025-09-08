package com.ipas.ipas.model.service;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.Policy;
import com.ipas.ipas.model.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolicyService {
    public List<Policy> findByClients(List<Client> clients) {
        return policyRepository.findByClients(clients);
    }
    
    @Autowired
    private PolicyRepository policyRepository;
    
    public List<Policy> findAll() {
        return policyRepository.findAll();
    }
    
    public Optional<Policy> findById(Long id) {
        return policyRepository.findById(id);
    }
    
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber);
    }
    
    public List<Policy> findByClient(Client client) {
        return policyRepository.findByClient(client);
    }
    
    public Policy save(Policy policy) {
        if (policy.getPolicyNumber() == null || policy.getPolicyNumber().isEmpty()) {
            policy.setPolicyNumber(generatePolicyNumber());
        }
        return policyRepository.save(policy);
    }
    
    public Policy update(Policy policy) {
        policy.setUpdatedAt(LocalDateTime.now());
        return policyRepository.save(policy);
    }
    
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }
    
    public List<Policy> searchPolicies(String searchTerm) {
        return policyRepository.findBySearchTerm(searchTerm);
    }
    
    public boolean existsByPolicyNumber(String policyNumber) {
        return policyRepository.existsByPolicyNumber(policyNumber);
    }
    
    public Long countPoliciesByClient(Client client) {
        return policyRepository.countByClient(client);
    }
    
    public List<Policy> findByStatus(Policy.PolicyStatus status) {
        return policyRepository.findByStatus(status);
    }
    
    public List<Policy> findByPolicyType(String policyType) {
        return policyRepository.findByPolicyType(policyType);
    }
    
    public List<Policy> findExpiredPolicies() {
        return policyRepository.findExpiredPolicies(LocalDate.now());
    }
    
    public List<Policy> findPoliciesExpiringInDays(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return policyRepository.findPoliciesExpiringBetween(startDate, endDate);
    }
    
    public void changePolicyStatus(Long policyId, Policy.PolicyStatus status) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isPresent()) {
            Policy policy = policyOpt.get();
            policy.setStatus(status);
            policy.setUpdatedAt(LocalDateTime.now());
            policyRepository.save(policy);
        }
    }
    
    private String generatePolicyNumber() {
        String prefix = "POL";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp + "-" + random;
    }
}