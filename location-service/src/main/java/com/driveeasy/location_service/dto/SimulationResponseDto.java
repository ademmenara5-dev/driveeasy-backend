package com.driveeasy.location_service.dto;

import java.math.BigDecimal;

public class SimulationResponseDto {

    private long nombreJours;
    private BigDecimal prixJournalier;
    private BigDecimal montantBrut;
    private String categorieClient;
    private BigDecimal tauxRemise;
    private BigDecimal montantRemise;
    private BigDecimal montantTotal;

    public SimulationResponseDto() {
    }

    public long getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(long nombreJours) {
        this.nombreJours = nombreJours;
    }

    public BigDecimal getPrixJournalier() {
        return prixJournalier;
    }

    public void setPrixJournalier(BigDecimal prixJournalier) {
        this.prixJournalier = prixJournalier;
    }

    public BigDecimal getMontantBrut() {
        return montantBrut;
    }

    public void setMontantBrut(BigDecimal montantBrut) {
        this.montantBrut = montantBrut;
    }

    public String getCategorieClient() {
        return categorieClient;
    }

    public void setCategorieClient(String categorieClient) {
        this.categorieClient = categorieClient;
    }

    public BigDecimal getTauxRemise() {
        return tauxRemise;
    }

    public void setTauxRemise(BigDecimal tauxRemise) {
        this.tauxRemise = tauxRemise;
    }

    public BigDecimal getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(BigDecimal montantRemise) {
        this.montantRemise = montantRemise;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
}
