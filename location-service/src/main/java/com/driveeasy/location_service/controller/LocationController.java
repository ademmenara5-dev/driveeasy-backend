package com.driveeasy.location_service.controller;

import com.driveeasy.location_service.dto.*;
import com.driveeasy.location_service.model.StatutLocation;
import com.driveeasy.location_service.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponseDto>> getAllLocations(
            @RequestParam(required = false) StatutLocation statut,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long vehiculeId) {
        List<LocationResponseDto> list = locationService.getAllLocations(statut, clientId, vehiculeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationEnrichieResponseDto> getLocationEnrichie(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationEnrichie(id));
    }

    @PostMapping
    public ResponseEntity<LocationResponseDto> createLocation(@Valid @RequestBody LocationRequestDto dto) {
        LocationResponseDto created = locationService.createLocation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/simulation")
    public ResponseEntity<SimulationResponseDto> simulatePrice(@Valid @RequestBody SimulationRequestDto dto) {
        SimulationResponseDto sim = locationService.simulatePrice(dto);
        return ResponseEntity.ok(sim);
    }

    @PatchMapping("/{id}/depart")
    public ResponseEntity<LocationResponseDto> demarrerLocation(@PathVariable Long id) {
        LocationResponseDto demarree = locationService.demarrerLocation(id);
        return ResponseEntity.ok(demarree);
    }

    @PatchMapping("/{id}/retour")
    public ResponseEntity<LocationResponseDto> cloturerRetour(
            @PathVariable Long id,
            @Valid @RequestBody RetourRequestDto dto) {
        LocationResponseDto terminee = locationService.cloturerRetour(id, dto);
        return ResponseEntity.ok(terminee);
    }

    @PatchMapping("/{id}/annulation")
    public ResponseEntity<LocationResponseDto> annulerLocation(@PathVariable Long id) {
        LocationResponseDto annulee = locationService.annulerLocation(id);
        return ResponseEntity.ok(annulee);
    }

    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesResponseDto> getStatistiques() {
        return ResponseEntity.ok(locationService.getStatistiques());
    }
}
