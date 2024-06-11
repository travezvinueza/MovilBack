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
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.ricardo.movilApp.util.Global.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;


    @Override
    public GenericResponse<UsuarioDTO> login(String email, String password) {
        Usuario user = usuarioRepository.findByEmailAndContrasena(email, password);

        if (user != null) {
            if (user.isVigencia()) {
                String role = user.getRoles().stream().findFirst().map(Role::getName).orElse("DEFAULT_ROLE");

                UsuarioDTO userDto = UsuarioDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .contrasena(user.getContrasena())
                        .vigencia(user.isVigencia())
                        .role(role)
                        .build();

                return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Haz iniciado sesión correctamente", userDto);
            } else {
                return new GenericResponse<>(TIPO_AUTH, RPTA_WARNING, "Lo sentimos, tu cuenta está inactiva", null);
            }
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR,"El Email o la Contraseña son incorrectos", null);
        }
    }

    public GenericResponse<UsuarioDTO> toggleUserVigencia(Long userId, boolean vigencia) {
        Optional<Usuario> optionalUser = usuarioRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Usuario user = optionalUser.get();
            user.setVigencia(vigencia);
            usuarioRepository.save(user);

            UsuarioDTO userDto = UsuarioDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .contrasena(user.getContrasena())
                    .vigencia(user.isVigencia())
                    .role(user.getRoles().stream().findFirst().map(Role::getName).orElse("DEFAULT_ROLE"))
                    .build();

            String message = vigencia ? ACTIVADO  : DESACTIVADO;
            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, message, userDto);
        } else {
            return new GenericResponse<>(TIPO_RESULT, RPTA_ERROR, "Lo sentimos, ese usuario no existe", null);
        }
    }

    @Override
    public GenericResponse<String> requestPasswordReset(String email) {
        Usuario user = usuarioRepository.findByEmail(email);

        if (user != null) {
            String otp = generateOTP();
            user.setOtp(otp);
            usuarioRepository.save(user);

            String subject = "Restablecimiento de contraseña";
            String body = "Tu OTP para restablecer la contraseña es: " + otp;
            emailService.sendEmail(email, subject, body);

            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "OTP enviado a tu correo electrónico", null);
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Lo sentimos, ese usuario no existe", null);
        }
    }

    @Override
    public GenericResponse<String> verifyAndResetPassword(String otp, String newPassword) {
        Usuario user = usuarioRepository.findByOtp(otp);

        if (user != null) {
            user.setContrasena(newPassword);
            user.setOtp(null); // Eliminar OTP después de uso
            usuarioRepository.save(user);

            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Contraseña restablecida correctamente", null);
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "El OTP es incorrecto o ha expirado", null);
        }
    }

    private String generateOTP(){
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    @Override
    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setVigencia(usuarioDTO.isVigencia());

        Role role = roleRepository.findByName(usuarioDTO.getRole());
        usuario.getRoles().clear();
        usuario.getRoles().add(role);

        usuario = usuarioRepository.save(usuario);

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .contrasena(usuario.getContrasena())
                .vigencia(usuario.isVigencia())
                .role(role.getName())
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

        String role = usuario.getRoles().stream().findFirst().map(Role::getName).orElse("DEFAULT_ROLE");

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
                    String role = usuario.getRoles().stream().findFirst().map(Role::getName).orElse("DEFAULT_ROLE");
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
