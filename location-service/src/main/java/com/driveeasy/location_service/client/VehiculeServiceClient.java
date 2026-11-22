package com.driveeasy.location_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "vehicule-service")
public interface VehiculeServiceClient {

    @GetMapping("/vehicules/{id}")
    VehiculeDto getVehiculeById(@PathVariable("id") Long id);

    @PatchMapping("/vehicules/{id}/statut")
    VehiculeDto updateStatut(@PathVariable("id") Long id, @RequestBody Map<String, String> body);
}
