package com.ipas.ipas.view.dto;

import com.ipas.ipas.model.entity.Client;
import java.time.LocalDate;

public class ClientResponse {
    private Long id;
    private String documentNumber;
    private String documentType;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String address;
    private String occupation;
    private String status;

    public ClientResponse(Client client) {
    this.id = client.getId();
    this.documentNumber = client.getDocumentNumber();
    this.documentType = client.getDocumentType().name();
    this.firstName = client.getFirstName();
    this.lastName = client.getLastName();
    this.email = client.getEmail();
    this.phoneNumber = client.getPhoneNumber();
    this.birthDate = client.getBirthDate();
    // Forzar address a cadena vacía si es nulo
    this.address = (client.getAddress() != null && !client.getAddress().isEmpty()) ? client.getAddress() : "";
    this.occupation = client.getOccupation();
    this.status = client.getStatus().name();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
