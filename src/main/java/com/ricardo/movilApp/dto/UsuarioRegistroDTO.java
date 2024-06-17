package com.ricardo.movilApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRegistroDTO {
    private String username;
    private String email;
    private String contrasena;
    private boolean vigencia;
    private LocalDate fecha;
}
