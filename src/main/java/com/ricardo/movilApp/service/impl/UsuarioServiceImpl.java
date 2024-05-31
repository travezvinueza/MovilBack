package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.entity.Role;
import com.ricardo.movilApp.entity.Usuario;
import com.ricardo.movilApp.repository.RoleRepository;
import com.ricardo.movilApp.repository.UsuarioRepository;
import com.ricardo.movilApp.service.UsuarioService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ricardo.movilApp.util.Global.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;


    @Override
    public GenericResponse<UsuarioDTO> login(String email, String password) {
        Usuario user = usuarioRepository.findByEmailAndContrasena(email, password);

        if (user != null) {
            String role = user.getRoles().stream().findFirst().map(Role::getRole).orElse("DEFAULT_ROLE");

            UsuarioDTO userDto = UsuarioDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .contrasena(user.getContrasena())
                    .vigencia(user.isVigencia())
                    .role(role)
                    .build();

            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Haz iniciado sesión correctamente", userDto);
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_WARNING, "Lo sentimos, ese usuario no existe", null);
        }
    }

    @Override
    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setVigencia(usuarioDTO.isVigencia());

        Role role = roleRepository.findByRole(usuarioDTO.getRole());
        usuario.getRoles().clear();
        usuario.getRoles().add(role);

        usuario = usuarioRepository.save(usuario);

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .contrasena(usuario.getContrasena())
                .vigencia(usuario.isVigencia())
                .role(role.getRole())
                .build();
    }

    @Override
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));
        usuarioRepository.delete(usuario);
    }

    @Override
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        String role = usuario.getRoles().stream().findFirst().map(Role::getRole).orElse("DEFAULT_ROLE");

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .contrasena(usuario.getContrasena())
                .vigencia(usuario.isVigencia())
                .role(role)
                .build();
    }

    @Override
    public List<UsuarioDTO> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> {
                    String role = usuario.getRoles().stream().findFirst().map(Role::getRole).orElse("DEFAULT_ROLE");
                    return UsuarioDTO.builder()
                            .id(usuario.getId())
                            .email(usuario.getEmail())
                            .contrasena(usuario.getContrasena())
                            .vigencia(usuario.isVigencia())
                            .role(role)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
