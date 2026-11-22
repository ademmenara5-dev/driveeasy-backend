package com.driveeasy.vehicule_service.dto;

import com.driveeasy.vehicule_service.model.StatutVehicule;
import jakarta.validation.constraints.NotNull;

public class StatutUpdateRequestDto {

    @NotNull(message = "Le statut est obligatoire")
    private StatutVehicule statut;

    public StatutUpdateRequestDto() {
    }

    public StatutUpdateRequestDto(StatutVehicule statut) {
        this.statut = statut;
    }

    public StatutVehicule getStatut() {
        return statut;
    }

    public void setStatut(StatutVehicule statut) {
        this.statut = statut;
    }
}
