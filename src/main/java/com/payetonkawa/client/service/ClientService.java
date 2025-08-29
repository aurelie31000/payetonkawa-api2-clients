package com.payetonkawa.client.service;

import com.payetonkawa.client.exception.ResourceNotFoundException;
import com.payetonkawa.client.model.Client;
import com.payetonkawa.client.repository.ClientRepository;
import com.payetonkawa.client.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;
    private final RabbitTemplate rabbitTemplate;

    
    public ClientService(ClientRepository clientRepository, RabbitTemplate rabbitTemplate) {
        this.clientRepository = clientRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public List<Client> getAllClients() {
       
        return clientRepository.findAll();
    }

    @Override
    public Client getClientById(Long id) {
      
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec id " + id));
    }

    @Override
    public Client createClient(Client client) {
        // Enregistrement du nouveau client et envoi d'un message à RabbitMQ
        Client createdClient = clientRepository.save(client);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, createdClient);
        return createdClient;
    }

    @Override
    public Client updateClient(Long id, Client clientDetails) {
       
        Client client = getClientById(id);
        client.setNom(clientDetails.getNom());
        client.setPrenom(clientDetails.getPrenom());
        client.setEmail(clientDetails.getEmail());
        
        // Enregistrement du client mis à jour et envoi d'un message à RabbitMQ
        Client updatedClient = clientRepository.save(client);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, updatedClient);
        return updatedClient;
    }

    @Override
    public void deleteClient(Long id) {
        // Enregistrer le client mis à jour et envoyer un message à RabbitMQ.
        clientRepository.delete(getClientById(id));
        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, "DELETE:" + id);
    }
}
