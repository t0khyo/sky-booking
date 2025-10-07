# sky-booking
Sky Booking is a modular monolith Spring Boot application designed for managing flight searches, hotel bookings, payment processing, and more.

## Run with Docker and Docker Compose
This project ships with a Dockerfile and a docker-compose.yml to run the full stack:
- Spring Boot application
- PostgreSQL
- Redis
- RabbitMQ (with management UI)
- Keycloak (dev mode)
- Nginx reverse proxy

### Prerequisites
- Docker and Docker Compose installed

### Start the stack
```
docker compose up --build
```
This will build the application image and start all services.

### Services and endpoints
- App via Nginx (reverse proxy): http://localhost/
- Spring Boot direct (inside network, not published): app:8080
- PostgreSQL: localhost:5432 (db: skybooking, user: skybooking, pass: skybooking)
- Redis: localhost:6379
- RabbitMQ AMQP: localhost:5672
- RabbitMQ UI: http://localhost:15672 (user: admin, pass: admin)
- Keycloak: http://localhost:8081 (admin/admin)

### Environment variables (compose)
The application reads settings from environment variables set in docker-compose.yml:
- SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/skybooking
- SPRING_DATASOURCE_USERNAME=skybooking
- SPRING_DATASOURCE_PASSWORD=skybooking
- SPRING_JPA_HIBERNATE_DDL_AUTO=update
- SPRING_REDIS_HOST=redis
- SPRING_RABBITMQ_HOST=rabbitmq

### Stop and clean
```
docker compose down -v
```
This stops containers and removes the postgres volume.

### Notes
- The Nginx container forwards all requests on port 80 to the app service on port 8080.
- Keycloak runs in dev mode without a preconfigured realm; you can configure it via the admin console if needed.
