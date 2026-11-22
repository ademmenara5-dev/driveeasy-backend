package com.driveeasy.vehicule_service.config;

import com.driveeasy.vehicule_service.model.BoiteVitesse;
import com.driveeasy.vehicule_service.model.Carburant;
import com.driveeasy.vehicule_service.model.StatutVehicule;
import com.driveeasy.vehicule_service.model.Vehicule;
import com.driveeasy.vehicule_service.repository.VehiculeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final VehiculeRepository repository;

    public DataInitializer(VehiculeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(new Vehicule(null, "123-TN-4567", "Peugeot", "208", 2023, "Blanc Nacré",
                    Carburant.ESSENCE, BoiteVitesse.MANUELLE, 5, 24500, new BigDecimal("85.00"),
                    StatutVehicule.DISPONIBLE, "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=500"));

            repository.save(new Vehicule(null, "234-TN-5678", "Renault", "Clio V", 2024, "Gris Titane",
                    Carburant.HYBRIDE, BoiteVitesse.AUTOMATIQUE, 5, 12000, new BigDecimal("95.00"),
                    StatutVehicule.DISPONIBLE, "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=500"));

            repository.save(new Vehicule(null, "345-TN-6789", "Volkswagen", "Golf 8", 2023, "Noir Intense",
                    Carburant.DIESEL, BoiteVitesse.AUTOMATIQUE, 5, 38000, new BigDecimal("120.00"),
                    StatutVehicule.LOUE, "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=500"));

            repository.save(new Vehicule(null, "456-TN-7890", "Tesla", "Model 3", 2024, "Bleu Outremer",
                    Carburant.ELECTRIQUE, BoiteVitesse.AUTOMATIQUE, 5, 8500, new BigDecimal("160.00"),
                    StatutVehicule.DISPONIBLE, "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=500"));

            repository.save(new Vehicule(null, "567-TN-8901", "BMW", "Série 3", 2022, "Gris Minéral",
                    Carburant.DIESEL, BoiteVitesse.AUTOMATIQUE, 5, 52000, new BigDecimal("180.00"),
                    StatutVehicule.DISPONIBLE, "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=500"));

            repository.save(new Vehicule(null, "678-TN-9012", "Mercedes-Benz", "Classe A", 2023, "Rouge Jupiter",
                    Carburant.ESSENCE, BoiteVitesse.AUTOMATIQUE, 5, 29000, new BigDecimal("150.00"),
                    StatutVehicule.MAINTENANCE, "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=500"));
        }
    }
}
