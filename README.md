# LemonCut - Backend API 🍋⚙️

A production-ready REST API for the **LemonCut** URL shortener, built with Spring Boot 3.x. It handles high-performance URL redirection, dynamic QR code data generation, and real-time analytics.

**Live Frontend:** [https://lemoncut.space/](https://lemoncut.space/)  
**Associated Frontend Repository:** [url-shortener-frontend](https://github.com/Jinjerana/url-shortener-frontend)

---

## 🛠️ Tech Stack & Infrastructure

* **Framework:** Java 21 + Spring Boot 3.x (Spring Web, Spring Data JPA, Actuator)
* **Database & Caching:** PostgreSQL (Persistence) + Redis (High-speed caching for URL redirection)
* **Monitoring:** Prometheus & Grafana
* **Containerization:** Docker & Docker Compose (5-container architecture)
* **Cloud Hosting:** Deployed as a containerized service on **render.com** with **cron-job.org** to keep the free tier active.

---

## Project Structure & Architecture

The Spring Boot backend follows a clean, layered architecture:
* **Models / Entities:** Database schema definitions.
* **Repositories:** Data access layer using Spring Data JPA.
* **Services:** Core business logic for hashing and URL shortening.
* **Controllers:** REST API endpoints handling requests and responses.
* **DTOs (Data Transfer Objects):** Decoupled data structures for API communication.
* **Exceptions:** Custom centralized exception handling.

### Configuration Packages:
* `CacheConfig` & `CorsConfig`: Setup for Redis caching and secure Cross-Origin Resource Sharing.
* `JacksonConfig`: Custom JSON serialization/deserialization.
* `SwaggerConfig`: API documentation boilerplate (prepared for future extension).

---

## Configuration & Environment Files

The application uses profile-specific configuration files to separate environments securely:
* **`application.yml`:** Core application settings (HikariCP connection pooling, Hibernate, Redis Cache, Actuator, and custom Logging levels).
* **`application-local.yml`:** Contains local environment variables (secured via `.gitignore`).
* **`application-local.yml.example`:** A template file showing required environment keys for other developers cloning the repo.
* **`application.docker.yml`:** Configuration tailored for Docker Desktop environments.

---

## Docker Infrastructure (5-Container Setup)

The local environment is completely containerized. The `docker-compose.yml` orchestrates **5 separate containers**:
1. **Spring Boot Application** (The core API service defined via the `Dockerfile` & `.dockerignore`)
2. **PostgreSQL Database** (Persistent storage)
3. **Redis Cache** (Blazing fast redirection lookup)
4. **Prometheus** (Metrics collection service using `prometheus.yml`)
5. **Grafana** (Visualization dashboard for monitoring application health)

---

## API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/shorten` | Shorten a URL & generate QR metadata |
| **GET** | `/{shortCode}` | Redirect to original URL (Cached via Redis) |
| **GET** | `/api/stats/{shortCode}` | Get click statistics |
| **DELETE** | `/api/urls/{shortCode}` | Delete a short URL |

---

## Getting Started (Local Development)

To spin up the entire 5-container infrastructure locally on Docker Desktop, follow these steps:

### 1. Clone & Prepare Environment
```bash
git clone [https://github.com/Jinjerana/url-shortener-api.git](https://github.com/Jinjerana/url-shortener-api.git)
cd url-shortener-api