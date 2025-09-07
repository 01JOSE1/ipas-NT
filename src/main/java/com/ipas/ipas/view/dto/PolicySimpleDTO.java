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

    public PolicySimpleDTO() {}

    public PolicySimpleDTO(Long id, String policyNumber, String policyType, String coverage, BigDecimal premiumAmount, BigDecimal coverageAmount, LocalDate startDate, LocalDate endDate, String status) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.coverage = coverage;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

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
