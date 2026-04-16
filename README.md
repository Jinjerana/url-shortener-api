# URL Shortener & Analytics API

A production-ready URL shortener built with Spring Boot 3.x

## Tech Stack
- Java 21 + Spring Boot 3.x
- PostgreSQL 15 + Redis 7
- Docker + Docker Compose
- Prometheus + Grafana

## Getting Started
'''bash
docker compose up --build -d

## API Endpoints
### POST | /api/shorten | Shorten a URL
### GET | /{shortCode} | Redirect to original URL
### DELETE | /api/urls/{shortCode} | Delete a short URL 
### GET | /api/stats/{shortCode} | Get click statistics