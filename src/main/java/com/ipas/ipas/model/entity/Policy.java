package com.ipas.ipas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "policies", indexes = {
    @Index(name = "idx_policy_number", columnList = "policy_number"),
    @Index(name = "idx_policy_status", columnList = "status"),
    @Index(name = "idx_policy_client", columnList = "client_id"),
    @Index(name = "idx_policy_dates", columnList = "start_date, end_date")
})
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Policy number is required")
    @Column(name = "policy_number", unique = true, nullable = false)
    private String policyNumber;
    
    @NotBlank(message = "Policy type is required")
    @Column(name = "policy_type", nullable = false)
    private String policyType;
    
    @NotBlank(message = "Coverage is required")
    @Column(name = "coverage", nullable = false, length = 1000)
    private String coverage;
    
    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    @Column(name = "premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;
    
    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status = PolicyStatus.ACTIVE;
    
    @Column(name = "deductible", precision = 12, scale = 2)
    private BigDecimal deductible;

    @Column(name = "valor_siniestro", precision = 15, scale = 2)
    private BigDecimal valorSiniestro;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;
    
    @Column(name = "beneficiaries", length = 1000)
    private String beneficiaries;
    
    @Column(name = "terms_conditions", length = 2000)
    private String termsConditions;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // Constructors
    public Policy() {}
    
    public Policy(String policyNumber, String policyType, String coverage, 
                  BigDecimal premiumAmount, BigDecimal coverageAmount, 
                  LocalDate startDate, LocalDate endDate, Client client) {
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.coverage = coverage;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }
    
    public String getCoverage() { return coverage; }
    public void setCoverage(String coverage) { this.coverage = coverage; }
    
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }

    public BigDecimal getValorSiniestro() { return valorSiniestro; }
    public void setValorSiniestro(BigDecimal valorSiniestro) { this.valorSiniestro = valorSiniestro; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(String beneficiaries) { this.beneficiaries = beneficiaries; }
    
    public String getTermsConditions() { return termsConditions; }
    public void setTermsConditions(String termsConditions) { this.termsConditions = termsConditions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum PolicyStatus {
        ACTIVE, INACTIVE, EXPIRED, CANCELLED, SUSPENDED
    }
}