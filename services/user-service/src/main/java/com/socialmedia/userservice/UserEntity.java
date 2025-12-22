package com.socialmedia.userservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter @Setter @Entity @Table(name = "user_entity")
public class UserEntity {
    @Id
    @Size(max = 36)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "email_constraint")
    private String emailConstraint;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Size(max = 255)
    @Column(name = "federation_link")
    private String federationLink;

    @Size(max = 255)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 255)
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 255)
    @Column(name = "realm_id")
    private String realmId;

    @Size(max = 255)
    @Column(name = "username")
    private String username;

    @Column(name = "created_timestamp")
    private Long createdTimestamp;

    @Size(max = 255)
    @Column(name = "service_account_client_link")
    private String serviceAccountClientLink;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "not_before", nullable = false)
    private Integer notBefore;

}