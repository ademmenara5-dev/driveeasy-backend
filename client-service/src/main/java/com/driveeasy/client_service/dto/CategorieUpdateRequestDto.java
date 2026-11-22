package com.driveeasy.client_service.dto;

import com.driveeasy.client_service.model.CategorieClient;
import jakarta.validation.constraints.NotNull;

public class CategorieUpdateRequestDto {

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieClient categorie;

    public CategorieUpdateRequestDto() {
    }

    public CategorieUpdateRequestDto(CategorieClient categorie) {
        this.categorie = categorie;
    }

    public CategorieClient getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieClient categorie) {
        this.categorie = categorie;
    }
}
