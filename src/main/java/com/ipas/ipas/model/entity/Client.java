package com.ipas.ipas.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_document", columnList = "document_number"),
    @Index(name = "idx_client_email", columnList = "email"),
    @Index(name = "idx_client_user", columnList = "user_id")
})
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Document number is required")
    @Column(name = "document_number", unique = true, nullable = false)
    private String documentNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType = DocumentType.DNI;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "occupation")
    private String occupation;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status = ClientStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();
    
    // Constructors
    public Client() {}
    
    public Client(String documentNumber, String firstName, String lastName, String email, User user) {
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public ClientStatus getStatus() { return status; }
    public void setStatus(ClientStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<Policy> getPolicies() { return policies; }
    public void setPolicies(List<Policy> policies) { this.policies = policies; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum DocumentType {
        DNI, PASSPORT, CARNET_EXTRANJERIA
    }
    
    public enum ClientStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}