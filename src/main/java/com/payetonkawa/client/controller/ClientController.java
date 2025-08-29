package com.payetonkawa.client.controller;
import com.payetonkawa.client.model.Client;
import com.payetonkawa.client.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;
    public ClientController(ClientService clientService) { this.clientService = clientService; }
    @GetMapping
    public List<Client> getAllClients() { return clientService.getAllClients(); }
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) { return ResponseEntity.ok(clientService.getClientById(id)); }
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) { return new ResponseEntity<>(clientService.createClient(client), HttpStatus.CREATED); }
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientDetails) { return ResponseEntity.ok(clientService.updateClient(id, clientDetails)); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) { clientService.deleteClient(id); return ResponseEntity.noContent().build(); }
}
