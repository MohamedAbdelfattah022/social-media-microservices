package com.socialmedia.userservice.service;

import com.socialmedia.userservice.dto.SignupRequest;
import com.socialmedia.userservice.entity.postgres.OutboxEvent;
import com.socialmedia.userservice.enums.OutboxEventAggregateType;
import com.socialmedia.userservice.enums.OutboxEventStatus;
import com.socialmedia.userservice.enums.OutboxEventType;
import com.socialmedia.userservice.exception.UserAlreadyExistsException;
import com.socialmedia.userservice.exception.UserRegistrationException;
import com.socialmedia.userservice.repository.postgres.OutboxEventRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloakAdminClient;
    private final String keycloakRealm;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    private static UserRepresentation createUserRepresentation(SignupRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        user.setCredentials(List.of(credential));
        return user;
    }

    @Transactional
    public String registerUser(SignupRequest request) {
        RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
        UsersResource usersResource = realmResource.users();

        if (!usersResource.searchByUsername(request.getUsername(), true).isEmpty())
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());


        if (!usersResource.searchByEmail(request.getEmail(), true).isEmpty())
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());


        UserRepresentation user = createUserRepresentation(request);

        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            String errorMessage = response.readEntity(String.class);
            log.error("Failed to create user: {} - {}", response.getStatus(), errorMessage);
            throw new UserRegistrationException("Failed to create user: " + errorMessage);
        }

        String userId = extractUserId(response);

        userService.createProfile(userId, request);
        createOutboxEvent(userId, request.getUsername());

        log.info("User created successfully: {} ({})", request.getUsername(), userId);

        return userId;
    }

    private String extractUserId(Response response) {
        String location = response.getHeaderString("Location");
        if (location != null)
            return location.substring(location.lastIndexOf('/') + 1);

        throw new UserRegistrationException("Could not extract user ID from Keycloak response");
    }

    private void createOutboxEvent(String userId, String username) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("userId", userId);
            payload.put("username", username);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(OutboxEventAggregateType.USER)
                    .aggregateId(userId)
                    .eventType(OutboxEventType.USER_CREATED)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEventStatus.PENDING)
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.debug("Outbox event created for user: {}", userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }
    }
}
