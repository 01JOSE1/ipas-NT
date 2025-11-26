package com.ipas.ipas.view.dto;

public class RiesgoSiniestroResponseDTO {
    private Boolean success;
    private String riesgo;
    private Double probabilidad;
    private String mensaje;

    // Constructors
    public RiesgoSiniestroResponseDTO() {}

    public RiesgoSiniestroResponseDTO(Boolean success, String riesgo, Double probabilidad, String mensaje) {
        this.success = success;
        this.riesgo = riesgo;
        this.probabilidad = probabilidad;
        this.mensaje = mensaje;
    }

    // Getters and Setters
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getRiesgo() { return riesgo; }
    public void setRiesgo(String riesgo) { this.riesgo = riesgo; }

    public Double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
