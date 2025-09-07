package com.ipas.ipas.model.repository;

import com.ipas.ipas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByResetPasswordToken(String token);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByStatus(User.UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.role = ?1")
    List<User> findActiveUsersByRole(User.UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    Long countByRole(User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
    List<User> findBySearchTerm(String searchTerm);
}