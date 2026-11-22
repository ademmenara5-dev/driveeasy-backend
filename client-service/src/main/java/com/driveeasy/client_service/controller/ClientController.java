package com.driveeasy.client_service.controller;

import com.driveeasy.client_service.dto.CategorieUpdateRequestDto;
import com.driveeasy.client_service.dto.ClientRequestDto;
import com.driveeasy.client_service.dto.ClientResponseDto;
import com.driveeasy.client_service.dto.EligibiliteResponseDto;
import com.driveeasy.client_service.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getClients(@RequestParam(required = false) String search) {
        List<ClientResponseDto> list = clientService.searchClients(search);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto dto) {
        ClientResponseDto created = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto dto) {
        ClientResponseDto updated = clientService.updateClient(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

    @GetMapping("/{id}/eligibilite")
    public ResponseEntity<EligibiliteResponseDto> checkEligibilite(@PathVariable Long id) {
        EligibiliteResponseDto res = clientService.checkEligibilite(id);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}/categorie")
    public ResponseEntity<ClientResponseDto> updateCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategorieUpdateRequestDto dto) {
        ClientResponseDto updated = clientService.updateCategorie(id, dto);
        return ResponseEntity.ok(updated);
    }
}
