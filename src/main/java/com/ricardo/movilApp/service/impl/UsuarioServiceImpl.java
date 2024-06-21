package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.ClienteDTO;
import com.ricardo.movilApp.dto.UsuarioClienteDTO;
import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.entity.Cliente;
import com.ricardo.movilApp.entity.Role;
import com.ricardo.movilApp.entity.Usuario;
import com.ricardo.movilApp.repository.ClienteRepository;
import com.ricardo.movilApp.repository.RoleRepository;
import com.ricardo.movilApp.repository.UsuarioRepository;
import com.ricardo.movilApp.service.UsuarioService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ricardo.movilApp.util.Global.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Override
    public GenericResponse<UsuarioDTO> create(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()) != null) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "El correo electrónico ya existe. Intenta con otro", null);
        }

//        if (clienteRepository.existsByNumDoc(usuarioDTO.getUsuarioClienteDto().getNumDoc())) {
//            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Ya existe un cliente con ese mismo numero de identificacion", null);
//        }

        Role role = roleRepository.findByName(usuarioDTO.getRole());
        if (role == null) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Role no encontrado", null);
        }

        Cliente cliente = Cliente.builder()
                .id(usuarioDTO.getUsuarioClienteDto().getId())
                .nombres(usuarioDTO.getUsuarioClienteDto().getNombres())
                .apellidos(usuarioDTO.getUsuarioClienteDto().getApellidos())
                .telefono(usuarioDTO.getUsuarioClienteDto().getTelefono())
                .tipoDoc(usuarioDTO.getUsuarioClienteDto().getTipoDoc())
                .numDoc(usuarioDTO.getUsuarioClienteDto().getNumDoc())
                .direccion(usuarioDTO.getUsuarioClienteDto().getDireccion())
                .provincia(usuarioDTO.getUsuarioClienteDto().getProvincia())
                .capital(usuarioDTO.getUsuarioClienteDto().getCapital())
                .fecha(usuarioDTO.getUsuarioClienteDto().getFecha())
                .build();

        cliente = clienteRepository.save(cliente);

        Usuario usuario = Usuario.builder()
                .id(usuarioDTO.getId())
                .username(usuarioDTO.getUsername())
                .email(usuarioDTO.getEmail())
                .contrasena(usuarioDTO.getContrasena())
                .vigencia(true)
                .fecha(usuarioDTO.getFecha())
                .roles(Set.of(role))
                .cliente(cliente)
                .build();

        usuario = usuarioRepository.save(usuario);

        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setClienteId(cliente.getId());
        usuarioDTO.getUsuarioClienteDto().setId(cliente.getId());
        return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Usuario creado correctamente", usuarioDTO);
    }


    @Override
    public GenericResponse<UsuarioDTO> login(String username, String password) {
        Usuario user = usuarioRepository.findByUsernameAndContrasena(username, password);

        if (user != null) {
            if (user.isVigencia()) {
                String role = user.getRoles().stream().findFirst().map(Role::getName).orElse("USER");

                Cliente cliente = user.getCliente();
                UsuarioClienteDTO usuarioClienteDto = UsuarioClienteDTO.builder()
                        .id(cliente.getId())
                        .nombres(cliente.getNombres())
                        .apellidos(cliente.getApellidos())
                        .telefono(cliente.getTelefono())
                        .tipoDoc(cliente.getTipoDoc())
                        .numDoc(cliente.getNumDoc())
                        .direccion(cliente.getDireccion())
                        .provincia(cliente.getProvincia())
                        .capital(cliente.getCapital())
                        .fecha(cliente.getFecha())
                        .build();

                UsuarioDTO userDto = UsuarioDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .contrasena(user.getContrasena())
                        .vigencia(user.isVigencia())
                        .fecha(user.getFecha())
                        .role(role)
                        .clienteId(cliente.getUsuario().getId())
                        .usuarioClienteDto(usuarioClienteDto)
                        .build();

                return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Haz iniciado sesión correctamente", userDto);
            } else {
                return new GenericResponse<>(TIPO_AUTH, RPTA_WARNING, "Lo sentimos, tu cuenta está inactiva", null);
            }
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "El Usuario o la Contraseña son incorrectos", null);
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
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .contrasena(user.getContrasena())
                    .vigencia(user.isVigencia())
                    .fecha(user.getFecha())
                    .clienteId(user.getId())
                    .role(user.getRoles().stream().findFirst().map(Role::getName).orElse("DEFAULT_ROLE"))
                    .build();

            String message = vigencia ? ACTIVADO : DESACTIVADO;
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

            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "EL OTP fue enviado a tu correo electrónico", null);
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

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    @Override
    public GenericResponse<UsuarioDTO> update(Long id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Usuario no encontrado", null);
        }

        Role role = roleRepository.findByName(usuarioDTO.getRole());
        if (role == null) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Role no encontrado", null);
        }

        Usuario usuario = optionalUsuario.get();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setVigencia(usuarioDTO.isVigencia());
        usuario.setFecha(usuarioDTO.getFecha());
        usuario.getRoles().clear();
        usuario.getRoles().add(role);

        Cliente cliente = usuario.getCliente();
        UsuarioClienteDTO clienteDTO = usuarioDTO.getUsuarioClienteDto();

        cliente.setNombres(clienteDTO.getNombres());
        cliente.setApellidos(clienteDTO.getApellidos());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setTipoDoc(clienteDTO.getTipoDoc());
        cliente.setNumDoc(clienteDTO.getNumDoc());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setProvincia(clienteDTO.getProvincia());
        cliente.setCapital(clienteDTO.getCapital());
        cliente.setFecha(clienteDTO.getFecha());

        usuario = usuarioRepository.save(usuario);
        cliente = clienteRepository.save(cliente);

        usuarioDTO.setId(usuario.getId());
        return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Usuario actualizado correctamente", usuarioDTO);
    }

    @Override
    public GenericResponse<UsuarioDTO> delete(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuarioRepository.deleteById(id);
            return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Usuario eliminado correctamente", null);
        } else {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Usuario no encontrado o ya eliminado", null);
        }
    }

    @Override
    public GenericResponse<UsuarioDTO> buscarUsuarioPorId(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Usuario no encontrado", null);
        }

        Usuario usuario = optionalUsuario.get();
        UsuarioDTO usuarioDTO = mapToUsuarioDTO(usuario);
        return new GenericResponse<>(TIPO_DATA, RPTA_OK, OPERACION_CORRECTA, usuarioDTO);
    }

    @Override
    public UsuarioDTO getUsuarioPorId(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (!optionalUsuario.isPresent()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        return mapToUsuarioDTO(optionalUsuario.get());
    }

    @Override
    public GenericResponse<List<UsuarioDTO>> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> usuarioDTOs = usuarios.stream()
                .map(this::mapToUsuarioDTO)
                .collect(Collectors.toList());

        return new GenericResponse<>(TIPO_AUTH, RPTA_OK, OPERACION_CORRECTA, usuarioDTOs);
    }

    private UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        Cliente cliente = usuario.getCliente();
        UsuarioClienteDTO usuarioClienteDto = UsuarioClienteDTO.builder()
                .id(cliente.getId())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .telefono(cliente.getTelefono())
                .tipoDoc(cliente.getTipoDoc())
                .numDoc(cliente.getNumDoc())
                .direccion(cliente.getDireccion())
                .provincia(cliente.getProvincia())
                .capital(cliente.getCapital())
                .fecha(cliente.getFecha())
                .build();

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .contrasena(usuario.getContrasena())
                .vigencia(usuario.isVigencia())
                .fecha(usuario.getFecha())
                .clienteId(cliente.getUsuario().getId())
                .role(usuario.getRoles().stream().findFirst().map(Role::getName).orElse("USER"))
                .usuarioClienteDto(usuarioClienteDto)
                .build();
    }
}
