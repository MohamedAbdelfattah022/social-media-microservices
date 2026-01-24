# Social Media Microservices Platform

![Java](https://img.shields.io/badge/Java-25-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen) ![Angular](https://img.shields.io/badge/Angular-21-red) ![License](https://img.shields.io/badge/license-MIT-blue)

A scalable social media platform built with Java Spring Boot microservices and an Angular frontend, designed to demonstrate modern microservice architecture patterns.

## Key Features

- **User Management**: Profile creation, authentication via Keycloak, and complex social graph relationships (followers/following) using Neo4j.
- **Content Management**: Create, edit, and delete posts with support for multimedia. High-performance access via Redis caching.
- **Social Interactions**: Robust system for likes, comments, and shares, ensuring data consistency with Transactional Outbox patterns.
- **Feed Generation**: Personalized feed aggregation.
- **Communication**: Inter-service communication using gRPC for low-latency data fetching and validations.
- **Frontend Experience**: Modern, responsive UI built with Angular 21 and TailwindCSS.
- **Event-Driven Architecture**: Asynchronous processing using RabbitMQ for decoupling services.

## 🏗️ Architecture

The project follows a microservices architecture with the following core components:

![Architecture Diagram](images/architecture-diagram.svg)

### Domain Services

| Service                 | Port | Database          | Description                                                             |
| ----------------------- | ---- | ----------------- | ----------------------------------------------------------------------- |
| **User Service**        | 8081 | PostgreSQL, Neo4j | Manages user profiles, auth (Keycloak), and social graph relationships. |
| **Post Service**        | 8082 | PostgreSQL, Redis | Handles post creation, retrieval, and caching.                          |
| **Interaction Service** | 8083 | PostgreSQL        | Manages likes, comments, and other interactions.                        |
| **Feed Service**        | 8084 | -                 | Aggregates and serves personalized user feeds.                          |

### Infrastructure Services

| Service               | Port | Description                           |
| --------------------- | ---- | ------------------------------------- |
| **API Gateway**       | 8080 | Entry point for all client requests.  |
| **Discovery Service** | 8761 | Eureka service registry.              |
| **Config Server**     | 8888 | Centralized configuration management. |
| **Identity Provider** | 8180 | Keycloak for OIDC authentication.     |

### Frontend

| Application    | Port | Description                       |
| -------------- | ---- | --------------------------------- |
| **Client App** | 4200 | Angular SPA for user interaction. |

## 🛠️ Technology Stack

- **Core Framework**: Java 25, Spring Boot 4.0.0
- **Frontend**: Angular 21, TailwindCSS
- **Communication**: REST, gRPC (Spring gRPC 1.0.0)
- **Service Discovery**: Spring Cloud Netflix Eureka
- **Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Databases**:
  - **PostgreSQL**: Primary transactional store for User, Post, Interaction.
  - **Neo4j**: Graph database for social connections.
  - **Redis**: Caching layer for high-speed reads.
- **Messaging**: RabbitMQ
- **Security**: Spring Security, OAuth2 / OpenID Connect (Keycloak)
- **Tools**: Docker, Docker Compose, Maven, Lombok, Flyway

## 🏁 Getting Started

### Prerequisites

Ensure you have the following installed:

- [Docker](https://www.docker.com/products/docker-desktop)
- [Java 25](https://jdk.java.net/25/)
- [Maven](https://maven.apache.org/install.html)
- [Node.js & npm](https://nodejs.org/) (for Frontend)

### Running with Docker Compose (Recommended)

To start the backend services and infrastructure:

```bash
docker-compose up -d
```

Validating the services:
- **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
- **RabbitMQ Dashboard**: [http://localhost:15672](http://localhost:15672) (guest/guest)
- **Keycloak Console**: [http://localhost:8180](http://localhost:8180) (admin/admin)
- **Neo4j Browser**: [http://localhost:7474](http://localhost:7474) (neo4j/password)

### Running the Frontend

Navigate to the client directory and start the Angular application:

```bash
cd client
npm install
npm start
```

The application will be available at [http://localhost:4200](http://localhost:4200).

### Building from Source

To build all backend services locally:

```bash
mvn clean install
```

## 📄 License

This project is under MIT license. For more details, see the [LICENSE](LICENSE) file.

Made with ❤️ by [Mohamed Abdelfattah](https://github.com/MohamedAbdelfattah022)
