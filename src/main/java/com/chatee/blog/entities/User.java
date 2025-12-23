package com.chatee.blog.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Will be hashed

    @Column(nullable = false)
    private String name;

    private String mobile;

    // --- NEW FIELDS FOR VERIFICATION ---

    @Column(nullable = false, unique = true)
    private String email; // Separate email field is better than using username

    private String role;

    @Column(name = "verification_code")
    private String verificationCode;

    private boolean enabled = false; // User cannot login until this is true
}