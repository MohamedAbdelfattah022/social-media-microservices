# Social Media Microservices Platform

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen) ![License](https://img.shields.io/badge/license-MIT-blue)

A scalable social media platform built with Java Spring Boot microservices.

## Key Features

- **User Management**: Profile creation, authentication, and social graph (followers/following).
- **Content Management**: Create, edit, and delete posts with multimedia support.
- **Social Interactions**: Like, comment, and share functionality.
- **Realtime Chat**: WebSocket based private messaging.
- **News Feed**: Personalized feed aggregation and ranking.
- **Media Processing**: Asynchronous image/video upload and processing.
- **Notifications**: Realtime notifications.

## Architecture
![Architecture Diagram](images/architecture-diagram.svg)

### Technology Stack

- **Core Framework**: Java 17, Spring Boot 4.x
- **Cloud Native**: Spring Cloud Gateway, Netflix Eureka, Spring Cloud Config
- **Databases**:
  - PostgreSQL (User/Interaction)
  - MongoDB (Post, Chat)
  - Neo4j (User)
- **Caching**: Redis
- **Messaging**: Kafka / RabbitMQ
- **Storage**: MinIO (Object Storage)
- **Security**: Spring Security, OAuth2/OIDC

## Getting Started

### Prerequisites

Ensure you have the following installed:

- [Docker](https://www.docker.com/get-started)
- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/install.html)

### Running Services

```bash
docker-compose up -d
```

### Build from Source

To build all services locally:

```bash
mvn clean install
```

## 📄 License

This project is under MIT license. For more details, see the [LICENSE](LICENSE) file.

Made with ❤️ by [Mohamed Abdelfattah](https://github.com/MohamedAbdelfattah022)
