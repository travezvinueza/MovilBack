package com.ricardo.movilApp.service;

import com.ricardo.movilApp.dto.RoleDTO;
import com.ricardo.movilApp.util.GenericResponse;

import java.util.List;

public interface RoleService {
    GenericResponse<RoleDTO> save(RoleDTO roleDTO);
    GenericResponse<RoleDTO> update(Long id, RoleDTO roleDTO);
    RoleDTO findById(Long id);
    List<RoleDTO> list();
    void delete(Long id);
}
