package com.ipas.ipas.view.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PolicyRequest {
    private String policyNumber;
    
    @NotBlank(message = "Policy type is required")
    private String policyType;
    
    @NotBlank(message = "Coverage is required")
    private String coverage;
    
    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;
    
    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private BigDecimal deductible;
    private String beneficiaries;
    private String termsConditions;
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    // Constructors
    public PolicyRequest() {}
    
    // Getters and Setters
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
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    
    public String getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(String beneficiaries) { this.beneficiaries = beneficiaries; }
    
    public String getTermsConditions() { return termsConditions; }
    public void setTermsConditions(String termsConditions) { this.termsConditions = termsConditions; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
}