package com.ipas.ipas.model.repository;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByDocumentNumber(String documentNumber);
    
    Optional<Client> findByEmail(String email);
    
    boolean existsByDocumentNumber(String documentNumber);
    
    List<Client> findByUser(User user);
    
    List<Client> findByStatus(Client.ClientStatus status);
    
    @Query("SELECT c FROM Client c WHERE c.user = ?1 AND c.status = 'ACTIVE'")
    List<Client> findActiveClientsByUser(User user);
    
    @Query("SELECT c FROM Client c WHERE " +
        "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "LOWER(c.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "LOWER(c.lastName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
        "c.documentNumber LIKE %?1% OR " +
        "CAST(c.id AS string) = ?1")
    List<Client> findBySearchTerm(String searchTerm);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.user = ?1")
    Long countByUser(User user);
}