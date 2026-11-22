# Plateforme de Location de Véhicules — « DriveEasy » (Backend)

Ce dépôt contient l'architecture backend microservices de la plateforme **DriveEasy**, réalisée dans le cadre du module *Développement d'applications web et multimédia (J2EE, .NET)* — Licence en Informatique et Multimédia (LIM3).

---

## 1. Description du Projet & Architecture

DriveEasy repose sur une architecture distribuée moderne construite avec **Java 21**, **Spring Boot 3.3.5**, et l'écosystème **Spring Cloud 2023.0.3**.
Conformément aux exigences du cahier des charges, **les données sont exclusivement gérées en mémoire vive (RAM)** via des structures de données thread-safe (`ConcurrentHashMap`), sans recours à une base de données relationnelle ou NoSQL.

### Cartographie des Microservices & Ports

| Composant | Technologie | Port | Rôle & Responsabilité |
| :--- | :--- | :---: | :--- |
| **Service Registry** | Spring Cloud Netflix Eureka Server | **8761** | Annuaire des services : enregistrement dynamique et découverte |
| **Config Server** | Spring Cloud Config Server | **8888** | Configuration externalisée et centralisée des microservices |
| **API Gateway** | Spring Cloud Gateway | **8080** | Point d'entrée unique, routage dynamique `lb://`, CORS, filtres personnalisés |
| **Vehicule Service** | Spring Boot (Web) + RAM | **8081** | Gestion du parc automobile, statuts, disponibilités, prix journalier |
| **Client Service** | Spring Boot (Web) + RAM | **8082** | Gestion des clients, contrôle d'éligibilité (âge >= 21, permis valide) |
| **Location Service** | Spring Boot (Web) + Feign + RAM | **8083** | Cycle de vie des locations, calcul des tarifs/remises/pénalités, statistiques |
| **Zipkin** | Docker (`openzipkin/zipkin`) | **9411** | Traçage distribué des requêtes inter-services (échantillonnage 100%) |

---

## 2. Prérequis Système

* **Java JDK 21 LTS** (Eclipse Temurin ou OpenJDK 21)
* **Apache Maven 3.9+**
* **Docker Desktop** (pour le container Zipkin)
* **Node.js 20+** & **npm** (pour le frontend Angular 22 optionnel)

Vérification des versions :
```bash
java -version   # Doit indiquer openjdk version "21.x"
mvn -version    # Doit indiquer Apache Maven 3.x
docker --version
```

---

## 3. Ordre de Démarrage Recommandé

Pour assurer la résolution des noms et le chargement des configurations, respectez l'ordre suivant :

### Étape 0 : Traçage Zipkin (Docker)
```bash
docker-compose up -d
# Interface Zipkin accessible sur : http://localhost:9411
```

### Étape 1 : Service Registry (Eureka Server - Port 8761)
```bash
cd service-registry
mvn spring-boot:run
# Dashboard Eureka accessible sur : http://localhost:8761
```

### Étape 2 : Config Server (Port 8888)
```bash
cd ../config-server
mvn spring-boot:run
# Vérification d'une configuration : http://localhost:8888/vehicule-service/default
```

### Étape 3 : Microservices Métier (Ouvrir 3 terminaux distincts)
```bash
# Terminal 1 - Vehicule Service (Port 8081)
cd vehicule-service
mvn spring-boot:run

# Terminal 2 - Client Service (Port 8082)
cd client-service
mvn spring-boot:run

# Terminal 3 - Location Service (Port 8083)
cd location-service
mvn spring-boot:run
```

### Étape 4 : API Gateway (Port 8080)
```bash
cd ../api-gateway
mvn spring-boot:run
# Toutes les requêtes passent désormais par : http://localhost:8080/api/...
```

---

## 4. Routage & Points d'Entrée API Gateway

Toutes les requêtes externes doivent être adressées à l'API Gateway sur le port **8080** :

| Route API Gateway | Service Cible | Méthodes Supportées | Description |
| :--- | :--- | :---: | :--- |
| `/api/vehicules/**` | `vehicule-service` (8081) | GET, POST, PUT, DELETE, PATCH | CRUD véhicules, disponibilités, filtres |
| `/api/clients/**` | `client-service` (8082) | GET, POST, PUT, DELETE, PATCH | CRUD clients, éligibilité, fidélité |
| `/api/locations/**` | `location-service` (8083) | GET, POST, PATCH | Réservations, départs, retours, statistiques |

---

## 5. Jeux de Données Pré-chargés en RAM

Des jeux de données de démonstration sont automatiquement injectés au démarrage de chaque microservice :

### Véhicules (`vehicule-service`)
1. **Peugeot 208** (2023, Essence, Manuelle, 85 €/j, Immatriculation : `123-TN-4567`, Statut : `DISPONIBLE`)
2. **Renault Clio V** (2024, Hybride, Automatique, 95 €/j, Immatriculation : `234-TN-5678`, Statut : `DISPONIBLE`)
3. **Volkswagen Golf 8** (2023, Diesel, Automatique, 120 €/j, Immatriculation : `345-TN-6789`, Statut : `LOUE`)
4. **Tesla Model 3** (2024, Électrique, Automatique, 160 €/j, Immatriculation : `456-TN-7890`, Statut : `DISPONIBLE`)
5. **BMW Série 3** (2022, Diesel, Automatique, 180 €/j, Immatriculation : `567-TN-8901`, Statut : `DISPONIBLE`)
6. **Mercedes-Benz Classe A** (2023, Essence, Automatique, 150 €/j, Immatriculation : `678-TN-9012`, Statut : `MAINTENANCE`)

### Clients (`client-service`)
1. **Karim Ben Salem** (CIN : `09876543`, 28 ans, Permis valide jusqu'en 2030 -> **Éligible**, Catégorie : `STANDARD`)
2. **Sonia Trabelsi** (CIN : `08765432`, 34 ans, Permis valide jusqu'en 2028 -> **Éligible**, Catégorie : `FIDELE` - Remise 5%)
3. **Yassine Mansouri** (CIN : `07654321`, 45 ans, Permis valide jusqu'en 2029 -> **Éligible**, Catégorie : `VIP` - Remise 10%)
4. **Amine Gharbi** (CIN : `11223344`, 19 ans -> **Non éligible : Moins de 21 ans**)
5. **Nadia Ayari** (CIN : `12334455`, 32 ans, Permis expiré en 2024 -> **Non éligible : Permis expiré**)

---

## 6. Exemples d'Appels API (cURL)

### Tester l'éligibilité d'un client
```bash
curl -X GET "http://localhost:8080/api/clients/1/eligibilite"
# Réponse : {"clientId":1,"eligible":true,"age":28,"permisValide":true,"motif":"Client éligible à la location."}
```

### Simuler le prix d'une location avec remise de fidélité
```bash
curl -X POST "http://localhost:8080/api/locations/simulation" \
  -H "Content-Type: application/json" \
  -d '{"clientId": 2, "vehiculeId": 1, "dateDebut": "2026-12-01", "dateFinPrevue": "2026-12-05"}'
```

### Créer une nouvelle réservation
```bash
curl -X POST "http://localhost:8080/api/locations" \
  -H "Content-Type: application/json" \
  -d '{"clientId": 1, "vehiculeId": 2, "dateDebut": "2026-12-10", "dateFinPrevue": "2026-12-15", "kmDepart": 12000}'
```

### Enregistrer le départ d'une location (Passe le véhicule à LOUE)
```bash
curl -X PATCH "http://localhost:8080/api/locations/3/depart"
```

### Enregistrer le retour (Calcul automatique des pénalités si retard)
```bash
curl -X PATCH "http://localhost:8080/api/locations/3/retour" \
  -H "Content-Type: application/json" \
  -d '{"kmRetour": 12600, "dateRetourEffectif": "2026-12-16"}'
```

### Consulter les indicateurs & statistiques (CA, occupation, top véhicules)
```bash
curl -X GET "http://localhost:8080/api/locations/statistiques"
```

---

## 7. Observabilité & Traçage Distribué (Zipkin)

Chaque service intègre `micrometer-tracing-bridge-brave` avec un taux d'échantillonnage de `1.0` (100%).
Lorsqu'une requête arrive sur l'API Gateway et traverse `location-service` -> `vehicule-service` / `client-service`, Zipkin capture l'arborescence complète des spans avec le temps d'exécution de chaque tronçon.
Ouvrez votre navigateur sur **`http://localhost:9411`** pour visualiser le graphe des dépendances et les temps de réponse.
