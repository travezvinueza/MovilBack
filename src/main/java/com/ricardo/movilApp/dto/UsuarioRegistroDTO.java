package com.ricardo.movilApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRegistroDTO {
    private String email;
    private String contrasena;
    private boolean vigencia;
}
