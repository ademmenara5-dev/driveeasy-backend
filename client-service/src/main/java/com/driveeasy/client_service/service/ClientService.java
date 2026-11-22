package com.driveeasy.client_service.service;

import com.driveeasy.client_service.dto.CategorieUpdateRequestDto;
import com.driveeasy.client_service.dto.ClientRequestDto;
import com.driveeasy.client_service.dto.ClientResponseDto;
import com.driveeasy.client_service.dto.EligibiliteResponseDto;
import com.driveeasy.client_service.exception.DuplicateResourceException;
import com.driveeasy.client_service.exception.ResourceNotFoundException;
import com.driveeasy.client_service.model.CategorieClient;
import com.driveeasy.client_service.model.Client;
import com.driveeasy.client_service.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<ClientResponseDto> searchClients(String query) {
        if (query == null || query.isBlank()) {
            return repository.findAll().stream()
                    .map(ClientResponseDto::fromEntity)
                    .collect(Collectors.toList());
        }
        String q = query.trim().toLowerCase();
        return repository.findAll().stream()
                .filter(c -> (c.getNom() != null && c.getNom().toLowerCase().contains(q))
                        || (c.getPrenom() != null && c.getPrenom().toLowerCase().contains(q))
                        || (c.getCin() != null && c.getCin().toLowerCase().contains(q))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(q)))
                .map(ClientResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ClientResponseDto getClientById(Long id) {
        Client c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'ID " + id + " introuvable."));
        return ClientResponseDto.fromEntity(c);
    }

    public ClientResponseDto createClient(ClientRequestDto dto) {
        // Unicité CIN (409)
        repository.findByCin(dto.getCin()).ifPresent(c -> {
            throw new DuplicateResourceException("Un client avec le CIN '" + dto.getCin() + "' existe déjà.");
        });

        // Unicité Email (409)
        repository.findByEmail(dto.getEmail()).ifPresent(c -> {
            throw new DuplicateResourceException("Un client avec l'email '" + dto.getEmail() + "' existe déjà.");
        });

        // Unicité Numéro Permis (409)
        repository.findByNumeroPermis(dto.getNumeroPermis()).ifPresent(c -> {
            throw new DuplicateResourceException("Un client avec le numéro de permis '" + dto.getNumeroPermis() + "' existe déjà.");
        });

        Client client = new Client();
        mapDtoToEntity(dto, client);
        client.setDateInscription(LocalDate.now());
        if (client.getCategorieClient() == null) {
            client.setCategorieClient(CategorieClient.STANDARD);
        }

        Client saved = repository.save(client);
        return ClientResponseDto.fromEntity(saved);
    }

    public ClientResponseDto updateClient(Long id, ClientRequestDto dto) {
        Client existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'ID " + id + " introuvable."));

        // Vérification unicité CIN
        Optional<Client> byCin = repository.findByCin(dto.getCin());
        if (byCin.isPresent() && !byCin.get().getId().equals(id)) {
            throw new DuplicateResourceException("Un client avec le CIN '" + dto.getCin() + "' existe déjà.");
        }

        // Vérification unicité Email
        Optional<Client> byEmail = repository.findByEmail(dto.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            throw new DuplicateResourceException("Un client avec l'email '" + dto.getEmail() + "' existe déjà.");
        }

        // Vérification unicité Numéro Permis
        Optional<Client> byPermis = repository.findByNumeroPermis(dto.getNumeroPermis());
        if (byPermis.isPresent() && !byPermis.get().getId().equals(id)) {
            throw new DuplicateResourceException("Un client avec le numéro de permis '" + dto.getNumeroPermis() + "' existe déjà.");
        }

        mapDtoToEntity(dto, existing);
        Client updated = repository.save(existing);
        return ClientResponseDto.fromEntity(updated);
    }

    public void deleteClient(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Client avec l'ID " + id + " introuvable.");
        }
        repository.deleteById(id);
    }

    public EligibiliteResponseDto checkEligibilite(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'ID " + id + " introuvable."));

        int age = Period.between(client.getDateNaissance(), LocalDate.now()).getYears();
        boolean ageValide = age >= 21;
        boolean permisValide = client.getDateExpirationPermis() != null
                && client.getDateExpirationPermis().isAfter(LocalDate.now());

        boolean eligible = ageValide && permisValide;
        String motif;
        if (!ageValide && !permisValide) {
            motif = "Le client a moins de 21 ans (" + age + " ans) et son permis de conduire est expiré.";
        } else if (!ageValide) {
            motif = "Le client doit avoir au moins 21 ans (âge actuel : " + age + " ans).";
        } else if (!permisValide) {
            motif = "Le permis de conduire est expiré depuis le " + client.getDateExpirationPermis() + ".";
        } else {
            motif = "Client éligible à la location.";
        }

        return new EligibiliteResponseDto(client.getId(), eligible, age, permisValide, motif);
    }

    public ClientResponseDto updateCategorie(Long id, CategorieUpdateRequestDto dto) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'ID " + id + " introuvable."));

        client.setCategorieClient(dto.getCategorie());
        Client saved = repository.save(client);
        return ClientResponseDto.fromEntity(saved);
    }

    private void mapDtoToEntity(ClientRequestDto dto, Client client) {
        client.setCin(dto.getCin().trim().toUpperCase());
        client.setNom(dto.getNom().trim());
        client.setPrenom(dto.getPrenom().trim());
        client.setDateNaissance(dto.getDateNaissance());
        client.setEmail(dto.getEmail().trim().toLowerCase());
        client.setTelephone(dto.getTelephone().trim());
        client.setAdresse(dto.getAdresse());
        client.setNumeroPermis(dto.getNumeroPermis().trim().toUpperCase());
        client.setDateExpirationPermis(dto.getDateExpirationPermis());
        if (dto.getCategorieClient() != null) {
            client.setCategorieClient(dto.getCategorieClient());
        }
    }
}
