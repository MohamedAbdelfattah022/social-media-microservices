# Social Media Microservices Platform

![Java](https://img.shields.io/badge/Java-25-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen) ![.NET](https://img.shields.io/badge/.NET-10.0-512BD4) ![Angular](https://img.shields.io/badge/Angular-21-red) ![License](https://img.shields.io/badge/license-MIT-blue)

A scalable social media platform built with microservices architecture, featuring Java Spring Boot and .NET Core services with an Angular frontend. Demonstrates modern cloud-native patterns including event-driven architecture, real-time notifications, and polyglot persistence.

## Key Features

- **User Management**: Profile creation, OAuth2/OIDC authentication via Keycloak, and complex social graph relationships using Neo4j
- **Content Management**: Create, edit, and delete posts with multimedia support and Redis caching for high performance reads
- **Social Interactions**: Robust system for likes, comments, and shares with transactional consistency
- **Real-time Notifications**: Server-Sent Events (SSE) for instant notification delivery with cursor pagination
- **File Management**: Scalable file storage and retrieval using MinIO (S3-compatible)
- **Inter-Service Communication**: gRPC for low latency data fetching between services
- **Modern Frontend**: Responsive Angular 21 SPA with TailwindCSS and signal state management
- **Event-Driven Architecture**: Asynchronous processing using RabbitMQ for service decoupling

## Architecture

![Architecture Diagram](images/architecture-diagram.svg)

## Project Structure

```
social-media-microservices/
├── client/                          # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/               # Guards, interceptors, services
│   │   │   ├── features/           # Feature modules
│   │   │   └── shared/             # Shared components, models
│   │   └── environments/
│   └── package.json
├── services/
│   ├── api-gateway/                # Spring Cloud Gateway
│   ├── config-server/              # Spring Cloud Config
│   ├── discovery-service/          # Netflix Eureka
│   ├── user-service/               # User management (Java)
│   ├── post-service/               # Post management (Java)
│   ├── interaction-service/        # Interactions (Java)
│   ├── feed-service/               # Feed aggregation (Java)
│   ├── minio-service/              # File storage (Java)
│   ├── notification-service/       # Notifications (.NET)
│   └── grpc-proto/                 # Shared gRPC protocol definitions
├── docker-compose.yml              # Infrastructure services
├── images/                         # Architecture diagrams
└── README.md
```

### Domain Services

| Service                  | Port | Tech Stack       | Database          | Description                                                        |
| ------------------------ | ---- | ---------------- | ----------------- | ------------------------------------------------------------------ |
| **User Service**         | 8081 | Java/Spring Boot | PostgreSQL, Neo4j | Manages user profiles, Keycloak auth integration, and social graph |
| **Post Service**         | 8082 | Java/Spring Boot | PostgreSQL, Redis | Handles post CRUD operations with Redis caching layer              |
| **Interaction Service**  | 8083 | Java/Spring Boot | PostgreSQL        | Manages likes, comments, shares with event publishing              |
| **Feed Service**         | 8084 | Java/Spring Boot | -                 | Aggregates personalized feeds using gRPC to fetch data             |
| **Notification Service** | 8086 | .NET 10          | PostgreSQL        | Real-time notifications via SSE, RabbitMQ event consumption        |
| **MinIO Service**        | 8085 | Java/Spring Boot | MinIO             | File upload/download with S3-compatible object storage             |

### Infrastructure Services

| Service               | Port        | Description                                                |
| --------------------- | ----------- | ---------------------------------------------------------- |
| **API Gateway**       | 8080        | Spring Cloud Gateway - entry point for all client requests |
| **Discovery Service** | 8761        | Netflix Eureka - service registry and discovery            |
| **Config Server**     | 8888        | Spring Cloud Config - centralized configuration management |
| **Keycloak**          | 8180        | Identity provider for OAuth2/OIDC authentication           |
| **RabbitMQ**          | 5672, 15672 | Message broker for event-driven patterns                   |
| **Neo4j**             | 7474, 7687  | Graph database for social network relationships            |
| **Redis**             | 6379        | In-memory cache for post data                              |
| **MinIO**             | 9000, 9001  | S3-compatible object storage for files                     |

### Frontend

| Application    | Port | Description                               |
| -------------- | ---- | ----------------------------------------- |
| **Client App** | 4200 | Angular 21 SPA with standalone components |

## Technology Stack

### Backend
- **Java Services**:
  - Java 25, Spring Boot 4.0.0
  - Spring Cloud Gateway, Config, Eureka
  - Spring Security with OAuth2/OIDC
  - gRPC for inter-service communication
  - Flyway for database migrations
  - Maven for dependency management

- **.NET Service**:
  - .NET 10.0, ASP.NET Core
  - Wolverine for message handling
  - Entity Framework Core with Npgsql
  - JWT Bearer authentication

### Frontend
- Angular 21 with standalone components
- TailwindCSS 4.x for styling
- Signal based state management
- RxJS for reactive programming
- TypeScript with strict mode

### Databases & Storage
- **PostgreSQL**: Primary transactional store (4 separate databases for service isolation)
- **Neo4j**: Graph database for social connections (followers/following)
- **Redis**: High speed caching layer for posts
- **MinIO**: S3 compatible object storage for files

### Messaging & Events
- **RabbitMQ**: Event driven communication between services
- **Server-Sent Events (SSE)**: Real time push notifications to clients

### Security
- **Keycloak**: OAuth2/OpenID Connect provider
- **JWT**: Token based authentication
- **Spring Security**: OAuth2 resource server configuration
- **CORS**: Configured for cross origin requests

### DevOps
- Docker & Docker Compose for containerization
- Maven for Java builds
- dotnet CLI for .NET builds
- npm for frontend builds

## Getting Started

### Prerequisites

Ensure you have the following installed:

- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Java 25 JDK](https://jdk.java.net/25/)
- [.NET 10 SDK](https://dotnet.microsoft.com/download/dotnet/10.0)
- [Maven](https://maven.apache.org/install.html)
- [Node.js](https://nodejs.org/)

### Quick Start with Docker Compose

1. **Start Infrastructure Services**

   ```bash
   docker-compose up -d
   ```

   This starts PostgreSQL, Neo4j, Redis, RabbitMQ, Keycloak, and MinIO.

2. **Verify Services**

   - **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
   - **RabbitMQ Dashboard**: [http://localhost:15672](http://localhost:15672) (guest/guest)
   - **Keycloak Console**: [http://localhost:8180](http://localhost:8180) (admin/admin)
   - **Neo4j Browser**: [http://localhost:7474](http://localhost:7474) (neo4j/password)
   - **MinIO Console**: [http://localhost:9001](http://localhost:9001) (minioadmin/minioadmin123)

3. **Build and Run Backend Services**

   Build all Java services:
   ```bash
   mvn clean install
   ```

   Start services individually:
   ```bash
   # Config Server (start first)
   cd services/config-server
   mvn spring-boot:run

   # Discovery Service (start second)
   cd services/discovery-service
   mvn spring-boot:run

   # Domain Services (start in any order)
   cd services/user-service
   mvn spring-boot:run

   cd services/post-service
   mvn spring-boot:run

   cd services/interaction-service
   mvn spring-boot:run

   cd services/feed-service
   mvn spring-boot:run

   cd services/notification-service
   dotnet run

   cd services/minio-service
   mvn spring-boot:run

   # API Gateway (start last)
   cd services/api-gateway
   mvn spring-boot:run

   ```

4. **Start Frontend**

   ```bash
   cd client
   npm install
   npm start
   ```

   The application will be available at [http://localhost:4200](http://localhost:4200).

## Key Architectural Patterns

### 1. API Gateway Pattern
- Single entry point for all client requests
- JWT validation at gateway level
- Request routing to appropriate services
- CORS configuration

### 2. Service Discovery
- Services register with Eureka on startup
- Dynamic service location via load balancer URLs (`lb://SERVICE-NAME`)
- Health checks for automatic failure detection

### 3. Event Driven Architecture
- Services publish events to RabbitMQ when state changes
- Notification service consumes events asynchronously
- Loose coupling between services
- Eventual consistency

### 4. Database per Service
- Each service owns its database
- No direct database access between services
- Data consistency via events and sagas

### 5. Polyglot Persistence
- PostgreSQL for transactional data
- Neo4j for graph relationships
- Redis for caching
- MinIO for object storage

### 6. gRPC for Inter Service Communication
- Type safe contracts via Protocol Buffers
- Low latency compared to REST
- Used for service to service calls (not client facing)

### 7. Real Time Updates
- Server Sent Events for push notifications
- Long lived HTTP connections
- Auto reconnection with exponential backoff

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

Made with ❤️ by [Mohamed Abdelfattah](https://github.com/MohamedAbdelfattah022)
