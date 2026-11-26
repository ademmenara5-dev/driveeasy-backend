package com.driveeasy.vehicule_service.controller;

import com.driveeasy.vehicule_service.dto.StatutUpdateRequestDto;
import com.driveeasy.vehicule_service.dto.VehiculeRequestDto;
import com.driveeasy.vehicule_service.dto.VehiculeResponseDto;
import com.driveeasy.vehicule_service.model.Carburant;
import com.driveeasy.vehicule_service.model.StatutVehicule;
import com.driveeasy.vehicule_service.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public ResponseEntity<List<VehiculeResponseDto>> getAllVehicules(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Carburant carburant,
            @RequestParam(required = false) StatutVehicule statut,
            @RequestParam(required = false) BigDecimal prixMax) {
        List<VehiculeResponseDto> list = vehiculeService.getAllVehicules(categorie, carburant, statut, prixMax);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponseDto> getVehiculeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.getVehiculeById(id));
    }

    @PostMapping
    public ResponseEntity<VehiculeResponseDto> createVehicule(@Valid @RequestBody VehiculeRequestDto dto) {
        VehiculeResponseDto created = vehiculeService.createVehicule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponseDto> updateVehicule(
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDto dto) {
        VehiculeResponseDto updated = vehiculeService.updateVehicule(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicule(@PathVariable Long id) {
        vehiculeService.deleteVehicule(id);
    }

    @RequestMapping(value = "/{id}/statut", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<VehiculeResponseDto> updateStatut(
            @PathVariable Long id,
            @Valid @RequestBody StatutUpdateRequestDto dto) {
        VehiculeResponseDto updated = vehiculeService.updateStatut(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculeResponseDto>> getVehiculesDisponibles(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<VehiculeResponseDto> disponibles = vehiculeService.getVehiculesDisponibles(dateDebut, dateFin);
        return ResponseEntity.ok(disponibles);
    }
}
