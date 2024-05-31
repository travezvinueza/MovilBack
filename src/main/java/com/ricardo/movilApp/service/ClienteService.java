package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.ClienteDTO;
import com.ricardo.movilApp.util.GenericResponse;

import java.util.List;

public interface ClienteService {
    GenericResponse<ClienteDTO> save (ClienteDTO clienteDTO);
    ClienteDTO update (Long id, ClienteDTO clienteDTO);
    ClienteDTO buscarClientePorId(Long id);
    List<ClienteDTO> listar();
    void delete(Long id);
}