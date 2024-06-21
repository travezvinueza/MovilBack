package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.ClienteDTO;
import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.util.GenericResponse;

import java.util.List;

public interface UsuarioService {
    GenericResponse<UsuarioDTO> create(UsuarioDTO usuarioDTO);
    GenericResponse<UsuarioDTO> update (Long id, UsuarioDTO usuarioDTO);
    GenericResponse<UsuarioDTO> buscarUsuarioPorId(Long id);
    GenericResponse<UsuarioDTO> login(String username, String password);
    GenericResponse<UsuarioDTO> toggleUserVigencia(Long userId, boolean vigencia);
    GenericResponse<String> requestPasswordReset(String email);
    GenericResponse<String> verifyAndResetPassword(String otp, String newPassword);
    GenericResponse<List<UsuarioDTO>> listar();
    GenericResponse<UsuarioDTO> delete(Long id);

    UsuarioDTO getUsuarioPorId(Long id);
}
