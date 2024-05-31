package com.ricardo.movilApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    @Column(length = 10)
    private String telefono;
    @Column(length = 20)
    private String tipoDoc;
    @Column(length = 15)
    private String numDoc;
    @Column(length = 500)
    private String direccion;
    private String provincia;
    private String capital;
    private LocalDate fecha;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}

