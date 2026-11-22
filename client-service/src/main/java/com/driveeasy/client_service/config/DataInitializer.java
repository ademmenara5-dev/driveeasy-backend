package com.driveeasy.client_service.config;

import com.driveeasy.client_service.model.CategorieClient;
import com.driveeasy.client_service.model.Client;
import com.driveeasy.client_service.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository repository;

    public DataInitializer(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            // Client 1 : Standard, éligible (28 ans, permis valide)
            repository.save(new Client(null, "09876543", "Ben Salem", "Karim",
                    LocalDate.of(1996, 5, 14), "karim.bensalem@email.com", "+216 98 123 456",
                    "15 Rue Habib Bourguiba, Tunis", "P-123456", LocalDate.of(2030, 8, 20),
                    CategorieClient.STANDARD, LocalDate.of(2025, 1, 10)));

            // Client 2 : Fidèle, éligible (34 ans, permis valide)
            repository.save(new Client(null, "08765432", "Trabelsi", "Sonia",
                    LocalDate.of(1990, 11, 22), "sonia.trabelsi@email.com", "+216 22 345 678",
                    "8 Avenue Habib Thameur, Sousse", "P-234567", LocalDate.of(2028, 4, 15),
                    CategorieClient.FIDELE, LocalDate.of(2024, 3, 5)));

            // Client 3 : VIP, éligible (45 ans, permis valide)
            repository.save(new Client(null, "07654321", "Mansouri", "Yassine",
                    LocalDate.of(1979, 2, 8), "yassine.mansouri@email.com", "+216 55 678 901",
                    "22 Rue de la Liberté, Sfax", "P-345678", LocalDate.of(2029, 12, 31),
                    CategorieClient.VIP, LocalDate.of(2023, 6, 12)));

            // Client 4 : Moins de 21 ans (non éligible)
            repository.save(new Client(null, "11223344", "Gharbi", "Amine",
                    LocalDate.of(2007, 7, 19), "amine.gharbi@email.com", "+216 29 456 789",
                    "4 Rue Ibn Khaldoun, Bizerte", "P-456789", LocalDate.of(2032, 1, 10),
                    CategorieClient.STANDARD, LocalDate.of(2026, 2, 1)));

            // Client 5 : Permis expiré (non éligible)
            repository.save(new Client(null, "12334455", "Ayari", "Nadia",
                    LocalDate.of(1992, 9, 3), "nadia.ayari@email.com", "+216 97 890 123",
                    "12 Rue Taher Sfar, Ariana", "P-567890", LocalDate.of(2024, 10, 1),
                    CategorieClient.STANDARD, LocalDate.of(2025, 4, 18)));
        }
    }
}
