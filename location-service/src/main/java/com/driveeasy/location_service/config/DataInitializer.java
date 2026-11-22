package com.driveeasy.location_service.config;

import com.driveeasy.location_service.model.Location;
import com.driveeasy.location_service.model.StatutLocation;
import com.driveeasy.location_service.repository.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LocationRepository repository;

    public DataInitializer(LocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            // Location 1 : Terminée (Client 2 - Sonia Trabelsi avec Peugeot 208)
            repository.save(new Location(null, 2L, 1L,
                    LocalDate.now().minusDays(10), LocalDate.now().minusDays(5), LocalDate.now().minusDays(5),
                    24000, 24500, new BigDecimal("85.00"), new BigDecimal("403.75"),
                    BigDecimal.ZERO, StatutLocation.TERMINEE, LocalDateTime.now().minusDays(11)));

            // Location 2 : En cours (Client 3 - Yassine Mansouri avec Volkswagen Golf)
            repository.save(new Location(null, 3L, 3L,
                    LocalDate.now().minusDays(2), LocalDate.now().plusDays(3), null,
                    38000, null, new BigDecimal("120.00"), new BigDecimal("540.00"),
                    BigDecimal.ZERO, StatutLocation.EN_COURS, LocalDateTime.now().minusDays(3)));

            // Location 3 : Réservée (Client 1 - Karim Ben Salem avec Tesla Model 3)
            repository.save(new Location(null, 1L, 4L,
                    LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), null,
                    8500, null, new BigDecimal("160.00"), new BigDecimal("640.00"),
                    BigDecimal.ZERO, StatutLocation.RESERVEE, LocalDateTime.now().minusHours(4)));
        }
    }
}
