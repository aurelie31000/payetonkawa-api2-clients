package com.payetonkawa.client.service;

import com.payetonkawa.client.model.Client;
import java.util.List;

public interface IClientService {
    List<Client> getAllClients();
    Client getClientById(Long id);
    Client createClient(Client client);
    Client updateClient(Long id, Client clientDetails);
    void deleteClient(Long id);
}
