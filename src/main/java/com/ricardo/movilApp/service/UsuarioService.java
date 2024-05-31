package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.util.GenericResponse;

import java.util.List;

public interface UsuarioService {

    UsuarioDTO update (Long id, UsuarioDTO usuarioDTO);
    UsuarioDTO buscarUsuarioPorId(Long id);
    GenericResponse<UsuarioDTO> login(String email, String password);
    List<UsuarioDTO> listar();
    void delete(Long id);

}
