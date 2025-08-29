package com.payetonkawa.client.service;

import com.payetonkawa.client.config.RabbitMQConfig;
import com.payetonkawa.client.exception.ResourceNotFoundException;
import com.payetonkawa.client.model.Client;
import com.payetonkawa.client.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client();
        client.setId(1L);
        client.setNom("Hamras");
        client.setPrenom("Sihem");
        client.setEmail("sihem@test.com");
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));

        List<Client> clients = clientService.getAllClients();

        assertEquals(1, clients.size());
        assertEquals("Hamras", clients.get(0).getNom());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientById_Found() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client found = clientService.getClientById(1L);

        assertNotNull(found);
        assertEquals("Hamras", found.getNom());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById(99L));
    }

    @Test
    void testCreateClient() {
        when(clientRepository.save(client)).thenReturn(client);

        Client created = clientService.createClient(client);

        assertNotNull(created);
        assertEquals("Sihem", created.getPrenom());
        verify(clientRepository, times(1)).save(client);
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, client);
    }

    @Test
    void testUpdateClient() {
        Client updatedDetails = new Client();
        updatedDetails.setNom("Hamras");
        updatedDetails.setPrenom("Nour");
        updatedDetails.setEmail("nour@test.com");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client updated = clientService.updateClient(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Nour", updated.getPrenom());
        verify(clientRepository, times(1)).save(client);
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, client);
    }

    @Test
    void testDeleteClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.deleteClient(1L);

        verify(clientRepository, times(1)).delete(client);
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.CLIENT_CHANGE_QUEUE, "DELETE:" + 1L);
    }
}
