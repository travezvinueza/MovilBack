package com.ricardo.movilApp.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String contrasena;
    private boolean vigencia;
    private String role;
    private LocalDate fecha;
    private Long clienteId;
    private UsuarioClienteDTO UsuarioClienteDTO;
}
