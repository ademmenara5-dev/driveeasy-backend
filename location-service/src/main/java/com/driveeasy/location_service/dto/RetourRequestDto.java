package com.driveeasy.location_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RetourRequestDto {

    @NotNull(message = "Le kilométrage de retour est obligatoire")
    @Min(value = 0, message = "Le kilométrage ne peut pas être négatif")
    private Integer kmRetour;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateRetourEffectif;

    public RetourRequestDto() {
    }

    public Integer getKmRetour() {
        return kmRetour;
    }

    public void setKmRetour(Integer kmRetour) {
        this.kmRetour = kmRetour;
    }

    public LocalDate getDateRetourEffectif() {
        return dateRetourEffectif;
    }

    public void setDateRetourEffectif(LocalDate dateRetourEffectif) {
        this.dateRetourEffectif = dateRetourEffectif;
    }
}
