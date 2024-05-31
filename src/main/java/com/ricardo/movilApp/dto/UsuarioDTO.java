package com.ricardo.movilApp.dto;

import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String email;
    private String contrasena;
    private boolean vigencia;
    private String role;
}
