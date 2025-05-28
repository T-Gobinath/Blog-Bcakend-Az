package com.chatee.blog.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user1")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String name;

    @Column
    private Long mobile;

    @Column
    private String role;
}