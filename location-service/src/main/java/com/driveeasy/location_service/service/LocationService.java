package com.driveeasy.location_service.service;

import com.driveeasy.location_service.client.ClientDto;
import com.driveeasy.location_service.client.ClientServiceClient;
import com.driveeasy.location_service.client.EligibiliteDto;
import com.driveeasy.location_service.client.VehiculeDto;
import com.driveeasy.location_service.client.VehiculeServiceClient;
import com.driveeasy.location_service.dto.*;
import com.driveeasy.location_service.exception.InvalidOperationException;
import com.driveeasy.location_service.exception.ResourceNotFoundException;
import com.driveeasy.location_service.model.Location;
import com.driveeasy.location_service.model.StatutLocation;
import com.driveeasy.location_service.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository repository;
    private final ClientServiceClient clientServiceClient;
    private final VehiculeServiceClient vehiculeServiceClient;

    public LocationService(LocationRepository repository,
                           ClientServiceClient clientServiceClient,
                           VehiculeServiceClient vehiculeServiceClient) {
        this.repository = repository;
        this.clientServiceClient = clientServiceClient;
        this.vehiculeServiceClient = vehiculeServiceClient;
    }

    public List<LocationResponseDto> getAllLocations(StatutLocation statut, Long clientId, Long vehiculeId) {
        return repository.findAll().stream()
                .filter(l -> statut == null || l.getStatut() == statut)
                .filter(l -> clientId == null || clientId.equals(l.getClientId()))
                .filter(l -> vehiculeId == null || vehiculeId.equals(l.getVehiculeId()))
                .sorted(Comparator.comparing(Location::getDateCreation).reversed())
                .map(LocationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LocationResponseDto getLocationById(Long id) {
        Location loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location avec l'ID " + id + " introuvable."));
        return LocationResponseDto.fromEntity(loc);
    }

    public LocationEnrichieResponseDto getLocationEnrichie(Long id) {
        Location loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location avec l'ID " + id + " introuvable."));

        ClientDto client = null;
        try {
            client = clientServiceClient.getClientById(loc.getClientId());
        } catch (Exception e) {
            log.warn("Impossible de récupérer les détails du client ID {}: {}", loc.getClientId(), e.getMessage());
        }

        VehiculeDto vehicule = null;
        try {
            vehicule = vehiculeServiceClient.getVehiculeById(loc.getVehiculeId());
        } catch (Exception e) {
            log.warn("Impossible de récupérer les détails du véhicule ID {}: {}", loc.getVehiculeId(), e.getMessage());
        }

        return new LocationEnrichieResponseDto(LocationResponseDto.fromEntity(loc), client, vehicule);
    }

    public LocationResponseDto createLocation(LocationRequestDto dto) {
        if (dto.getDateFinPrevue().isBefore(dto.getDateDebut())) {
            throw new InvalidOperationException("La date de fin prévue ne peut pas être antérieure à la date de début.");
        }

        // 1. Contrôle de l'éligibilité du client (GET /clients/{id}/eligibilite)
        EligibiliteDto eligibilite;
        ClientDto client;
        try {
            eligibilite = clientServiceClient.checkEligibilite(dto.getClientId());
            client = clientServiceClient.getClientById(dto.getClientId());
        } catch (Exception e) {
            throw new InvalidOperationException("Échec de la vérification du client (client-service indisponible ou ID invalide) : " + e.getMessage());
        }

        if (!eligibilite.isEligible()) {
            throw new InvalidOperationException("Client inéligible à la location : " + eligibilite.getMotif());
        }

        // 2. Contrôle de la disponibilité du véhicule (GET /vehicules/{id})
        VehiculeDto vehicule;
        try {
            vehicule = vehiculeServiceClient.getVehiculeById(dto.getVehiculeId());
        } catch (Exception e) {
            throw new InvalidOperationException("Échec de la récupération du véhicule (vehicule-service indisponible ou ID invalide) : " + e.getMessage());
        }

        if (!"DISPONIBLE".equalsIgnoreCase(vehicule.getStatut())) {
            throw new InvalidOperationException("Le véhicule " + vehicule.getImmatriculation() + " n'est pas disponible (statut actuel : " + vehicule.getStatut() + ").");
        }

        // 3. Calcul du prix
        BigDecimal prixJournalier = vehicule.getPrixJournalier();
        long nbJours = ChronoUnit.DAYS.between(dto.getDateDebut(), dto.getDateFinPrevue());
        if (nbJours <= 0) nbJours = 1;

        BigDecimal montantBrut = prixJournalier.multiply(BigDecimal.valueOf(nbJours));
        BigDecimal tauxRemise = getTauxRemise(client.getCategorieClient());
        BigDecimal montantRemise = montantBrut.multiply(tauxRemise).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montantTotal = montantBrut.subtract(montantRemise);

        Location loc = new Location();
        loc.setClientId(dto.getClientId());
        loc.setVehiculeId(dto.getVehiculeId());
        loc.setDateDebut(dto.getDateDebut());
        loc.setDateFinPrevue(dto.getDateFinPrevue());
        loc.setKmDepart(dto.getKmDepart() > 0 ? dto.getKmDepart() : vehicule.getKilometrage());
        loc.setPrixJournalierApplique(prixJournalier);
        loc.setMontantTotal(montantTotal);
        loc.setPenalite(BigDecimal.ZERO);
        loc.setStatut(StatutLocation.RESERVEE);
        loc.setDateCreation(LocalDateTime.now());

        Location saved = repository.save(loc);
        return LocationResponseDto.fromEntity(saved);
    }

    public SimulationResponseDto simulatePrice(SimulationRequestDto dto) {
        if (dto.getDateFinPrevue().isBefore(dto.getDateDebut())) {
            throw new InvalidOperationException("La date de fin ne peut pas être antérieure à la date de début.");
        }

        ClientDto client = clientServiceClient.getClientById(dto.getClientId());
        VehiculeDto vehicule = vehiculeServiceClient.getVehiculeById(dto.getVehiculeId());

        long nbJours = ChronoUnit.DAYS.between(dto.getDateDebut(), dto.getDateFinPrevue());
        if (nbJours <= 0) nbJours = 1;

        BigDecimal prixJournalier = vehicule.getPrixJournalier();
        BigDecimal montantBrut = prixJournalier.multiply(BigDecimal.valueOf(nbJours));
        BigDecimal tauxRemise = getTauxRemise(client.getCategorieClient());
        BigDecimal montantRemise = montantBrut.multiply(tauxRemise).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montantTotal = montantBrut.subtract(montantRemise);

        SimulationResponseDto sim = new SimulationResponseDto();
        sim.setNombreJours(nbJours);
        sim.setPrixJournalier(prixJournalier);
        sim.setMontantBrut(montantBrut);
        sim.setCategorieClient(client.getCategorieClient());
        sim.setTauxRemise(tauxRemise);
        sim.setMontantRemise(montantRemise);
        sim.setMontantTotal(montantTotal);
        return sim;
    }

    public LocationResponseDto demarrerLocation(Long id) {
        Location loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location avec l'ID " + id + " introuvable."));

        if (loc.getStatut() != StatutLocation.RESERVEE) {
            throw new InvalidOperationException("Impossible de démarrer la location : le statut actuel est " + loc.getStatut());
        }

        // Appel inter-service : passage du véhicule à LOUE
        try {
            vehiculeServiceClient.updateStatut(loc.getVehiculeId(), Map.of("statut", "LOUE"));
        } catch (Exception e) {
            throw new InvalidOperationException("Échec de la mise à jour du statut du véhicule : " + e.getMessage());
        }

        loc.setStatut(StatutLocation.EN_COURS);
        Location saved = repository.save(loc);
        return LocationResponseDto.fromEntity(saved);
    }

    public LocationResponseDto cloturerRetour(Long id, RetourRequestDto dto) {
        Location loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location avec l'ID " + id + " introuvable."));

        if (loc.getStatut() != StatutLocation.EN_COURS) {
            throw new InvalidOperationException("Seule une location EN_COURS peut être clôturée.");
        }

        LocalDate dateRetour = dto.getDateRetourEffectif() != null ? dto.getDateRetourEffectif() : LocalDate.now();
        loc.setDateRetourEffectif(dateRetour);
        loc.setKmRetour(dto.getKmRetour());

        // Calcul des pénalités de retard : 1.5 * prixJournalier par jour de retard
        BigDecimal penalite = BigDecimal.ZERO;
        if (dateRetour.isAfter(loc.getDateFinPrevue())) {
            long joursRetard = ChronoUnit.DAYS.between(loc.getDateFinPrevue(), dateRetour);
            penalite = loc.getPrixJournalierApplique()
                    .multiply(BigDecimal.valueOf(1.5))
                    .multiply(BigDecimal.valueOf(joursRetard))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        loc.setPenalite(penalite);
        loc.setMontantTotal(loc.getMontantTotal().add(penalite));
        loc.setStatut(StatutLocation.TERMINEE);
        Location saved = repository.save(loc);

        // Appel inter-service : passage du véhicule à DISPONIBLE
        try {
            vehiculeServiceClient.updateStatut(loc.getVehiculeId(), Map.of("statut", "DISPONIBLE"));
        } catch (Exception e) {
            log.error("Erreur lors de la libération du véhicule ID {}: {}", loc.getVehiculeId(), e.getMessage());
        }

        // Mise à jour de fidélité du client (FIDELE après 5 locations, VIP après 15)
        long totalTerminees = repository.countTermineesByClientId(loc.getClientId());
        try {
            if (totalTerminees >= 15) {
                clientServiceClient.updateCategorie(loc.getClientId(), Map.of("categorie", "VIP"));
            } else if (totalTerminees >= 5) {
                clientServiceClient.updateCategorie(loc.getClientId(), Map.of("categorie", "FIDELE"));
            }
        } catch (Exception e) {
            log.warn("Impossible de mettre à jour la catégorie du client ID {}: {}", loc.getClientId(), e.getMessage());
        }

        return LocationResponseDto.fromEntity(saved);
    }

    public LocationResponseDto annulerLocation(Long id) {
        Location loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location avec l'ID " + id + " introuvable."));

        if (loc.getStatut() == StatutLocation.TERMINEE) {
            throw new InvalidOperationException("Impossible d'annuler une location déjà terminée.");
        }

        // Si la location était EN_COURS, libérer le véhicule
        if (loc.getStatut() == StatutLocation.EN_COURS) {
            try {
                vehiculeServiceClient.updateStatut(loc.getVehiculeId(), Map.of("statut", "DISPONIBLE"));
            } catch (Exception e) {
                log.warn("Impossible de libérer le véhicule lors de l'annulation : {}", e.getMessage());
            }
        }

        loc.setStatut(StatutLocation.ANNULEE);
        Location saved = repository.save(loc);
        return LocationResponseDto.fromEntity(saved);
    }

    public StatistiquesResponseDto getStatistiques() {
        List<Location> all = repository.findAll();

        BigDecimal caTotal = all.stream()
                .filter(l -> l.getStatut() == StatutLocation.TERMINEE || l.getStatut() == StatutLocation.EN_COURS)
                .map(Location::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate now = LocalDate.now();
        BigDecimal caMois = all.stream()
                .filter(l -> (l.getStatut() == StatutLocation.TERMINEE || l.getStatut() == StatutLocation.EN_COURS)
                        && l.getDateCreation().getMonth() == now.getMonth()
                        && l.getDateCreation().getYear() == now.getYear())
                .map(Location::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long total = all.size();
        long enCours = all.stream().filter(l -> l.getStatut() == StatutLocation.EN_COURS).count();
        long reservees = all.stream().filter(l -> l.getStatut() == StatutLocation.RESERVEE).count();
        long terminees = all.stream().filter(l -> l.getStatut() == StatutLocation.TERMINEE).count();
        long annulees = all.stream().filter(l -> l.getStatut() == StatutLocation.ANNULEE).count();

        double tauxOccupation = total > 0 ? ((double) (enCours + reservees) / (double) total) * 100.0 : 0.0;

        Map<Long, Long> topVehicules = all.stream()
                .collect(Collectors.groupingBy(Location::getVehiculeId, Collectors.counting()));

        StatistiquesResponseDto stats = new StatistiquesResponseDto();
        stats.setChiffreAffairesTotal(caTotal);
        stats.setChiffreAffairesMois(caMois);
        stats.setTotalLocations(total);
        stats.setLocationsEnCours(enCours);
        stats.setLocationsReservees(reservees);
        stats.setLocationsTerminees(terminees);
        stats.setLocationsAnnulees(annulees);
        stats.setTauxOccupation(Math.round(tauxOccupation * 10.0) / 10.0);
        stats.setTopVehiculesLocationCount(topVehicules);
        return stats;
    }

    private BigDecimal getTauxRemise(String categorieClient) {
        if ("VIP".equalsIgnoreCase(categorieClient)) {
            return new BigDecimal("0.10"); // 10%
        } else if ("FIDELE".equalsIgnoreCase(categorieClient)) {
            return new BigDecimal("0.05"); // 5%
        }
        return BigDecimal.ZERO;
    }
}
