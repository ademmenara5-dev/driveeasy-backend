package com.driveeasy.location_service.dto;

import java.math.BigDecimal;
import java.util.Map;

public class StatistiquesResponseDto {

    private BigDecimal chiffreAffairesTotal;
    private BigDecimal chiffreAffairesMois;
    private long totalLocations;
    private long locationsEnCours;
    private long locationsReservees;
    private long locationsTerminees;
    private long locationsAnnulees;
    private double tauxOccupation;
    private Map<Long, Long> topVehiculesLocationCount;

    public StatistiquesResponseDto() {
    }

    public BigDecimal getChiffreAffairesTotal() {
        return chiffreAffairesTotal;
    }

    public void setChiffreAffairesTotal(BigDecimal chiffreAffairesTotal) {
        this.chiffreAffairesTotal = chiffreAffairesTotal;
    }

    public BigDecimal getChiffreAffairesMois() {
        return chiffreAffairesMois;
    }

    public void setChiffreAffairesMois(BigDecimal chiffreAffairesMois) {
        this.chiffreAffairesMois = chiffreAffairesMois;
    }

    public long getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(long totalLocations) {
        this.totalLocations = totalLocations;
    }

    public long getLocationsEnCours() {
        return locationsEnCours;
    }

    public void setLocationsEnCours(long locationsEnCours) {
        this.locationsEnCours = locationsEnCours;
    }

    public long getLocationsReservees() {
        return locationsReservees;
    }

    public void setLocationsReservees(long locationsReservees) {
        this.locationsReservees = locationsReservees;
    }

    public long getLocationsTerminees() {
        return locationsTerminees;
    }

    public void setLocationsTerminees(long locationsTerminees) {
        this.locationsTerminees = locationsTerminees;
    }

    public long getLocationsAnnulees() {
        return locationsAnnulees;
    }

    public void setLocationsAnnulees(long locationsAnnulees) {
        this.locationsAnnulees = locationsAnnulees;
    }

    public double getTauxOccupation() {
        return tauxOccupation;
    }

    public void setTauxOccupation(double tauxOccupation) {
        this.tauxOccupation = tauxOccupation;
    }

    public Map<Long, Long> getTopVehiculesLocationCount() {
        return topVehiculesLocationCount;
    }

    public void setTopVehiculesLocationCount(Map<Long, Long> topVehiculesLocationCount) {
        this.topVehiculesLocationCount = topVehiculesLocationCount;
    }
}
