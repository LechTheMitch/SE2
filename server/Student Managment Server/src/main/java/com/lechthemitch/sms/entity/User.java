package com.lechthemitch.sms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "First Name must be provided")
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @NotBlank(message = "Email must be provided")
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    @Column(name = "password", length = 68, nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean forcePasswordChange = false;

    @Column(nullable = false)
    private boolean forceEmailChange = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId")
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_direct_permissions",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "permissionId")
    )
    private Set<Permission> permissions;

}
