package com.ipas.ipas.model.repository;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    Optional<Policy> findByPolicyNumber(String policyNumber);
    
    boolean existsByPolicyNumber(String policyNumber);
    
    List<Policy> findByClient(Client client);
    
    List<Policy> findByStatus(Policy.PolicyStatus status);
    
    List<Policy> findByPolicyType(String policyType);
    
    @Query("SELECT p FROM Policy p WHERE p.endDate < ?1 AND p.status = 'ACTIVE'")
    List<Policy> findExpiredPolicies(LocalDate currentDate);
    
    @Query("SELECT p FROM Policy p WHERE p.endDate BETWEEN ?1 AND ?2 AND p.status = 'ACTIVE'")
    List<Policy> findPoliciesExpiringBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Policy p WHERE p.client IN ?1")
    List<Policy> findByClients(List<Client> clients);
    
    @Query("SELECT COUNT(p) FROM Policy p WHERE p.client = ?1")
    Long countByClient(Client client);
    
    @Query("SELECT p FROM Policy p WHERE " +
        "LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "LOWER(p.policyType) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "LOWER(p.client.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "LOWER(p.client.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Policy> findBySearchTerm(String searchTerm);
}