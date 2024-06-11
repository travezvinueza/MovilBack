package com.ricardo.movilApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String contrasena;
    private boolean vigencia;
    private String otp;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Cliente cliente;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    Set<Role> roles = new HashSet<>();

}

