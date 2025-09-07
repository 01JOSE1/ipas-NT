package com.ipas.ipas.model.service;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.User;
import com.ipas.ipas.model.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public List<Client> findAll() {
        return clientRepository.findAll();
    }
    
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }
    
    public Optional<Client> findByDocumentNumber(String documentNumber) {
        return clientRepository.findByDocumentNumber(documentNumber);
    }
    
    public List<Client> findByUser(User user) {
        return clientRepository.findByUser(user);
    }
    
    public List<Client> findActiveClientsByUser(User user) {
        return clientRepository.findActiveClientsByUser(user);
    }
    
    public Client save(Client client) {
        return clientRepository.save(client);
    }
    
    public Client update(Client client) {
        client.setUpdatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }
    
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
    
    public List<Client> searchClients(String searchTerm) {
    return clientRepository.findBySearchTerm(searchTerm);
    }
    
    public boolean existsByDocumentNumber(String documentNumber) {
        return clientRepository.existsByDocumentNumber(documentNumber);
    }
    
    public Long countClientsByUser(User user) {
        return clientRepository.countByUser(user);
    }
    
    public List<Client> findByStatus(Client.ClientStatus status) {
        return clientRepository.findByStatus(status);
    }
    
    public void changeClientStatus(Long clientId, Client.ClientStatus status) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setStatus(status);
            client.setUpdatedAt(LocalDateTime.now());
            clientRepository.save(client);
        }
    }
}