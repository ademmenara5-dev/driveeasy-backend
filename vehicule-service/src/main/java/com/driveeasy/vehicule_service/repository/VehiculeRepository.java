package com.driveeasy.vehicule_service.repository;

import com.driveeasy.vehicule_service.model.Vehicule;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VehiculeRepository {

    private final Map<Long, Vehicule> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Vehicule> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Vehicule> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        if (immatriculation == null) return Optional.empty();
        return storage.values().stream()
                .filter(v -> v.getImmatriculation() != null && v.getImmatriculation().equalsIgnoreCase(immatriculation.trim()))
                .findFirst();
    }

    public Vehicule save(Vehicule vehicule) {
        if (vehicule.getId() == null) {
            vehicule.setId(idGenerator.getAndIncrement());
        }
        storage.put(vehicule.getId(), vehicule);
        return vehicule;
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
