package com.driveeasy.vehicule_service.service;

import com.driveeasy.vehicule_service.dto.StatutUpdateRequestDto;
import com.driveeasy.vehicule_service.dto.VehiculeRequestDto;
import com.driveeasy.vehicule_service.dto.VehiculeResponseDto;
import com.driveeasy.vehicule_service.exception.DuplicateResourceException;
import com.driveeasy.vehicule_service.exception.InvalidOperationException;
import com.driveeasy.vehicule_service.exception.ResourceNotFoundException;
import com.driveeasy.vehicule_service.model.Carburant;
import com.driveeasy.vehicule_service.model.StatutVehicule;
import com.driveeasy.vehicule_service.model.Vehicule;
import com.driveeasy.vehicule_service.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehiculeService {

    private final VehiculeRepository repository;

    public VehiculeService(VehiculeRepository repository) {
        this.repository = repository;
    }

    public List<VehiculeResponseDto> getAllVehicules(String categorie, Carburant carburant,
                                                     StatutVehicule statut, BigDecimal prixMax) {
        return repository.findAll().stream()
                .filter(v -> categorie == null || categorie.isBlank()
                        || (v.getMarque() != null && v.getMarque().toLowerCase().contains(categorie.toLowerCase()))
                        || (v.getModele() != null && v.getModele().toLowerCase().contains(categorie.toLowerCase())))
                .filter(v -> carburant == null || v.getCarburant() == carburant)
                .filter(v -> statut == null || v.getStatut() == statut)
                .filter(v -> prixMax == null || (v.getPrixJournalier() != null && v.getPrixJournalier().compareTo(prixMax) <= 0))
                .map(VehiculeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public VehiculeResponseDto getVehiculeById(Long id) {
        Vehicule v = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec l'ID " + id + " introuvable."));
        return VehiculeResponseDto.fromEntity(v);
    }

    public VehiculeResponseDto createVehicule(VehiculeRequestDto dto) {
        // Validation unicité immatriculation -> 409
        repository.findByImmatriculation(dto.getImmatriculation()).ifPresent(v -> {
            throw new DuplicateResourceException("Un véhicule avec l'immatriculation '" + dto.getImmatriculation() + "' existe déjà.");
        });

        // Validation cohérence année (<= année courante)
        int currentYear = Year.now().getValue();
        if (dto.getAnnee() > currentYear) {
            throw new InvalidOperationException("L'année du véhicule (" + dto.getAnnee() + ") ne peut pas dépasser l'année courante (" + currentYear + ").");
        }

        // Validation prix journalier strictement positif
        if (dto.getPrixJournalier() == null || dto.getPrixJournalier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Le prix journalier doit être strictement positif.");
        }

        Vehicule vehicule = new Vehicule();
        mapDtoToEntity(dto, vehicule);
        if (vehicule.getStatut() == null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }

        Vehicule saved = repository.save(vehicule);
        return VehiculeResponseDto.fromEntity(saved);
    }

    public VehiculeResponseDto updateVehicule(Long id, VehiculeRequestDto dto) {
        Vehicule existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec l'ID " + id + " introuvable."));

        // Vérification unicité immatriculation si modifiée
        Optional<Vehicule> byImmat = repository.findByImmatriculation(dto.getImmatriculation());
        if (byImmat.isPresent() && !byImmat.get().getId().equals(id)) {
            throw new DuplicateResourceException("Un véhicule avec l'immatriculation '" + dto.getImmatriculation() + "' existe déjà.");
        }

        int currentYear = Year.now().getValue();
        if (dto.getAnnee() > currentYear) {
            throw new InvalidOperationException("L'année du véhicule (" + dto.getAnnee() + ") ne peut pas dépasser l'année courante (" + currentYear + ").");
        }

        if (dto.getPrixJournalier() == null || dto.getPrixJournalier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Le prix journalier doit être strictement positif.");
        }

        mapDtoToEntity(dto, existing);
        Vehicule updated = repository.save(existing);
        return VehiculeResponseDto.fromEntity(updated);
    }

    public void deleteVehicule(Long id) {
        Vehicule v = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec l'ID " + id + " introuvable."));

        if (v.getStatut() == StatutVehicule.LOUE) {
            throw new DuplicateResourceException("Suppression refusée : le véhicule est actuellement loué.");
        }

        repository.deleteById(id);
    }

    public VehiculeResponseDto updateStatut(Long id, StatutUpdateRequestDto dto) {
        Vehicule v = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec l'ID " + id + " introuvable."));

        v.setStatut(dto.getStatut());
        Vehicule saved = repository.save(v);
        return VehiculeResponseDto.fromEntity(saved);
    }

    public List<VehiculeResponseDto> getVehiculesDisponibles(LocalDate dateDebut, LocalDate dateFin) {
        // Règle : Un véhicule en MAINTENANCE ou LOUE ne peut pas être réservé
        return repository.findAll().stream()
                .filter(v -> v.getStatut() == StatutVehicule.DISPONIBLE)
                .map(VehiculeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void mapDtoToEntity(VehiculeRequestDto dto, Vehicule v) {
        v.setImmatriculation(dto.getImmatriculation().trim().toUpperCase());
        v.setMarque(dto.getMarque().trim());
        v.setModele(dto.getModele().trim());
        v.setAnnee(dto.getAnnee());
        v.setCouleur(dto.getCouleur().trim());
        v.setCarburant(dto.getCarburant());
        v.setBoite(dto.getBoite());
        v.setNbPlaces(dto.getNbPlaces());
        v.setKilometrage(dto.getKilometrage());
        v.setPrixJournalier(dto.getPrixJournalier());
        if (dto.getStatut() != null) {
            v.setStatut(dto.getStatut());
        }
        v.setImageUrl(dto.getImageUrl());
    }
}
