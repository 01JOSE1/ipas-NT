package com.ipas.ipas.view.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RiesgoSiniestroRequestDTO {
    @JsonProperty("edad")
    private Integer edad;
    @JsonProperty("document_type")
    private String documentType;
    @JsonProperty("occupation")
    private String occupation;
    @JsonProperty("siniestro")
    private String siniestro;
    @JsonProperty("cliente_status")
    private String clienteStatus;
    @JsonProperty("policy_type")
    private String policyType;
    @JsonProperty("premium_amount")
    private BigDecimal premiumAmount;
    @JsonProperty("coverage_amount")
    private BigDecimal coverageAmount;
    @JsonProperty("deductible")
    private BigDecimal deductible;
    @JsonProperty("policy_status")
    private String policyStatus;
    @JsonProperty("duracion_dias")
    private Integer duracionDias;
    @JsonProperty("valor_siniestro")
    private BigDecimal valorSiniestro;

    // Constructors
    public RiesgoSiniestroRequestDTO() {}

    public RiesgoSiniestroRequestDTO(Integer edad, String documentType, String occupation, 
                                     String siniestro, String clienteStatus, String policyType,
                                     BigDecimal premiumAmount, BigDecimal coverageAmount,
                                     BigDecimal deductible, String policyStatus, Integer duracionDias,
                                     BigDecimal valorSiniestro) {
        this.edad = edad;
        this.documentType = documentType;
        this.occupation = occupation;
        this.siniestro = siniestro;
        this.clienteStatus = clienteStatus;
        this.policyType = policyType;
        this.premiumAmount = premiumAmount;
        this.coverageAmount = coverageAmount;
        this.deductible = deductible;
        this.policyStatus = policyStatus;
        this.duracionDias = duracionDias;
        this.valorSiniestro = valorSiniestro;
    }

    // Getters and Setters
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getSiniestro() { return siniestro; }
    public void setSiniestro(String siniestro) { this.siniestro = siniestro; }

    public String getClienteStatus() { return clienteStatus; }
    public void setClienteStatus(String clienteStatus) { this.clienteStatus = clienteStatus; }

    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }

    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }

    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }

    public String getPolicyStatus() { return policyStatus; }
    public void setPolicyStatus(String policyStatus) { this.policyStatus = policyStatus; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public BigDecimal getValorSiniestro() { return valorSiniestro; }
    public void setValorSiniestro(BigDecimal valorSiniestro) { this.valorSiniestro = valorSiniestro; }
}
