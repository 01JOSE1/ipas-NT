package com.ipas.ipas.view.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PolicySimpleDTO {
    private Long id;
    private String policyNumber;
    private String policyType;
    private String coverage;
    private BigDecimal premiumAmount;
    private BigDecimal coverageAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long clientId;
    private BigDecimal deductible;
    private BigDecimal valorSiniestro;
    private String beneficiaries;
    private String termsConditions;
    private String clientName;

    public PolicySimpleDTO() {}

    public PolicySimpleDTO(Long id, String policyNumber, String policyType, String coverage, BigDecimal premiumAmount, BigDecimal coverageAmount, LocalDate startDate, LocalDate endDate, String status, Long clientId, BigDecimal deductible, BigDecimal valorSiniestro, String beneficiaries, String termsConditions, String clientName) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.coverage = coverage;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.clientId = clientId;
        this.deductible = deductible;
        this.valorSiniestro = valorSiniestro;
        this.beneficiaries = beneficiaries;
        this.termsConditions = termsConditions;
        this.clientName = clientName;
    }
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    public BigDecimal getValorSiniestro() { return valorSiniestro; }
    public void setValorSiniestro(BigDecimal valorSiniestro) { this.valorSiniestro = valorSiniestro; }
    public String getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(String beneficiaries) { this.beneficiaries = beneficiaries; }
    public String getTermsConditions() { return termsConditions; }
    public void setTermsConditions(String termsConditions) { this.termsConditions = termsConditions; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
