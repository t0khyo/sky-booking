# sky-booking
Sky Booking is a modular monolith Spring Boot application designed for managing flight searches, hotel bookings, payment processing, and more.


## Tech Stack

- **Spring Boot** - Main framework for building the backend application.
- **PostgreSQL** - Relational database for storing user and booking data.
- **Redis** - In-memory data store for caching and session management.
- **Keycloak** - Identity and access management for authentication and authorization.
- **RabbitMQ** - Message broker for handling communication between different services.
- **Nginx** - Reverse proxy and load balancer for better traffic management.
- **Docker** - Containerization for creating isolated environments for each service.

## Requirements

Before running the application, ensure you have the following installed on your machine:

- Docker and Docker Compose
- Java 11 or higher

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/t0khyo/sky-booking.git
cd sky-booking
```

### Running the Application

To start the application and all required services, simply run the following command:

```bash
docker-compose up --build
```
