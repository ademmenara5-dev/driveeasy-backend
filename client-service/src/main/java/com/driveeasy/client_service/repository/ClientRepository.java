package com.driveeasy.client_service.repository;

import com.driveeasy.client_service.model.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ClientRepository {

    private final Map<Long, Client> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Client> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Client> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Client> findByCin(String cin) {
        if (cin == null) return Optional.empty();
        return storage.values().stream()
                .filter(c -> c.getCin() != null && c.getCin().equalsIgnoreCase(cin.trim()))
                .findFirst();
    }

    public Optional<Client> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.values().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public Optional<Client> findByNumeroPermis(String numeroPermis) {
        if (numeroPermis == null) return Optional.empty();
        return storage.values().stream()
                .filter(c -> c.getNumeroPermis() != null && c.getNumeroPermis().equalsIgnoreCase(numeroPermis.trim()))
                .findFirst();
    }

    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(idGenerator.getAndIncrement());
        }
        storage.put(client.getId(), client);
        return client;
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
