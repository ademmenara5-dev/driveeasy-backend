package com.driveeasy.location_service.dto;

import com.driveeasy.location_service.client.ClientDto;
import com.driveeasy.location_service.client.VehiculeDto;

public class LocationEnrichieResponseDto {

    private LocationResponseDto location;
    private ClientDto client;
    private VehiculeDto vehicule;

    public LocationEnrichieResponseDto() {
    }

    public LocationEnrichieResponseDto(LocationResponseDto location, ClientDto client, VehiculeDto vehicule) {
        this.location = location;
        this.client = client;
        this.vehicule = vehicule;
    }

    public LocationResponseDto getLocation() {
        return location;
    }

    public void setLocation(LocationResponseDto location) {
        this.location = location;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }

    public VehiculeDto getVehicule() {
        return vehicule;
    }

    public void setVehicule(VehiculeDto vehicule) {
        this.vehicule = vehicule;
    }
}
