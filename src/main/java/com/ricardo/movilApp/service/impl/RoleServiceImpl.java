package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.RoleDTO;
import com.ricardo.movilApp.entity.Role;
import com.ricardo.movilApp.repository.RoleRepository;
import com.ricardo.movilApp.service.RoleService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ricardo.movilApp.util.Global.RPTA_WARNING;
import static com.ricardo.movilApp.util.Global.TIPO_DATA;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public GenericResponse<RoleDTO> save(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            return new GenericResponse<>(TIPO_DATA, RPTA_WARNING, "Ya existe ese rol", null);
        }
        Role role = new Role(roleDTO.getId(), roleDTO.getName(), roleDTO.getFecha());
        roleRepository.save(role);
        return GenericResponse.<RoleDTO>builder()
                .type("Exito")
                .rpta(1)
                .message("Rol guardado exitosamente")
                .body(new RoleDTO(role.getId(), role.getName(), role.getFecha()))
                .build();
    }

    @Override
    public GenericResponse<RoleDTO> update(Long id, RoleDTO roleDTO) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            role.setName(roleDTO.getName());
            role.setFecha(roleDTO.getFecha());
            roleRepository.save(role);
            return GenericResponse.<RoleDTO>builder()
                    .type("Exito")
                    .rpta(1)
                    .message("Rol actualizado exitosamente")
                    .body(new RoleDTO(role.getId(), role.getName(), role.getFecha()))
                    .build();
        } else {
            return GenericResponse.<RoleDTO>builder()
                    .type("Error")
                    .rpta(0)
                    .message("Role not found")
                    .body(null)
                    .build();
        }
    }

    @Override
    public RoleDTO findById(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        return optionalRole.map(role -> new RoleDTO(
                role.getId(),
                role.getName(),
                role.getFecha())).orElse(null);
    }

    @Override
    public List<RoleDTO> list() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RoleDTO(
                        role.getId(),
                        role.getName(),
                        role.getFecha()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}
