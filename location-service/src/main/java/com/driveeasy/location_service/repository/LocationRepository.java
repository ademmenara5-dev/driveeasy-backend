package com.driveeasy.location_service.repository;

import com.driveeasy.location_service.model.Location;
import com.driveeasy.location_service.model.StatutLocation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class LocationRepository {

    private final Map<Long, Location> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Location> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Location> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public List<Location> findByClientId(Long clientId) {
        if (clientId == null) return new ArrayList<>();
        return storage.values().stream()
                .filter(l -> clientId.equals(l.getClientId()))
                .collect(Collectors.toList());
    }

    public List<Location> findByVehiculeId(Long vehiculeId) {
        if (vehiculeId == null) return new ArrayList<>();
        return storage.values().stream()
                .filter(l -> vehiculeId.equals(l.getVehiculeId()))
                .collect(Collectors.toList());
    }

    public long countTermineesByClientId(Long clientId) {
        if (clientId == null) return 0;
        return storage.values().stream()
                .filter(l -> clientId.equals(l.getClientId()) && l.getStatut() == StatutLocation.TERMINEE)
                .count();
    }

    public Location save(Location location) {
        if (location.getId() == null) {
            location.setId(idGenerator.getAndIncrement());
        }
        storage.put(location.getId(), location);
        return location;
    }

    public boolean deleteById(Long id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    public boolean existsById(Long id) {
        if (id == null) return false;
        return storage.containsKey(id);
    }

    public long count() {
        return storage.size();
    }
}
