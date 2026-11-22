package com.driveeasy.vehicule_service.dto;

import com.driveeasy.vehicule_service.model.BoiteVitesse;
import com.driveeasy.vehicule_service.model.Carburant;
import com.driveeasy.vehicule_service.model.StatutVehicule;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class VehiculeRequestDto {

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;

    @Min(value = 1900, message = "L'année doit être supérieure ou égale à 1900")
    private int annee;

    @NotBlank(message = "La couleur est obligatoire")
    private String couleur;

    @NotNull(message = "Le type de carburant est obligatoire")
    private Carburant carburant;

    @NotNull(message = "Le type de boîte de vitesse est obligatoire")
    private BoiteVitesse boite;

    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    private int nbPlaces;

    @Min(value = 0, message = "Le kilométrage ne peut pas être négatif")
    private int kilometrage;

    @NotNull(message = "Le prix journalier est obligatoire")
    @DecimalMin(value = "0.01", inclusive = true, message = "Le prix journalier doit être strictement positif")
    private BigDecimal prixJournalier;

    private StatutVehicule statut = StatutVehicule.DISPONIBLE;

    private String imageUrl;

    public VehiculeRequestDto() {
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public Carburant getCarburant() {
        return carburant;
    }

    public void setCarburant(Carburant carburant) {
        this.carburant = carburant;
    }

    public BoiteVitesse getBoite() {
        return boite;
    }

    public void setBoite(BoiteVitesse boite) {
        this.boite = boite;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }

    public BigDecimal getPrixJournalier() {
        return prixJournalier;
    }

    public void setPrixJournalier(BigDecimal prixJournalier) {
        this.prixJournalier = prixJournalier;
    }

    public StatutVehicule getStatut() {
        return statut;
    }

    public void setStatut(StatutVehicule statut) {
        this.statut = statut;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
