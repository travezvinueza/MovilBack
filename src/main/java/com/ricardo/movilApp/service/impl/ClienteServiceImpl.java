package com.ricardo.movilApp.service.impl;

import com.ricardo.movilApp.dto.ClienteDTO;
import com.ricardo.movilApp.dto.ClienteUsuarioDTO;
import com.ricardo.movilApp.entity.Cliente;
import com.ricardo.movilApp.entity.Role;
import com.ricardo.movilApp.entity.Usuario;
import com.ricardo.movilApp.repository.ClienteRepository;
import com.ricardo.movilApp.repository.RoleRepository;
import com.ricardo.movilApp.repository.UsuarioRepository;
import com.ricardo.movilApp.service.ClienteService;
import com.ricardo.movilApp.util.GenericResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ricardo.movilApp.util.Global.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Override
    public GenericResponse<ClienteDTO> save(ClienteDTO clienteDTO) {
        if (usuarioRepository.findByEmail(clienteDTO.getClienteUsuarioDTO().getEmail()) != null) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "El correo electrónico ya existe. Intenta con otro", null);
        }

        if (clienteRepository.existsByNumDoc(clienteDTO.getNumDoc())) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_WARNING, "Ya existe un cliente con ese mismo numero de identificacion", null);
        }

        ClienteUsuarioDTO clienteUsuarioDTO = clienteDTO.getClienteUsuarioDTO();
        Usuario usuario = Usuario.builder()
                .username(clienteUsuarioDTO.getUsername())
                .email(clienteUsuarioDTO.getEmail())
                .contrasena(clienteUsuarioDTO.getContrasena())
                .fecha(clienteUsuarioDTO.getFecha())
                .vigencia(true)
                .build();

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "No se pudo encontrar el rol 'USER'", null);
        }

        usuario.setRoles(new HashSet<>());
        usuario.getRoles().add(userRole);

        Cliente cliente = Cliente.builder()
                .nombres(clienteDTO.getNombres())
                .apellidos(clienteDTO.getApellidos())
                .telefono(clienteDTO.getTelefono())
                .tipoDoc(clienteDTO.getTipoDoc())
                .numDoc(clienteDTO.getNumDoc())
                .direccion(clienteDTO.getDireccion())
                .provincia(clienteDTO.getProvincia())
                .capital(clienteDTO.getCapital())
                .fecha(clienteDTO.getFecha())
                .usuario(usuario)
                .build();


        cliente = clienteRepository.save(cliente);
        usuario = usuarioRepository.save(usuario);

        clienteDTO.setId(cliente.getId());
        return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Cliente registrado correctamente", clienteDTO);
    }

    @Override
    public GenericResponse<ClienteDTO> update(Long id, ClienteDTO clienteDTO) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if (optionalCliente.isEmpty()) {
            return new GenericResponse<>(TIPO_AUTH, RPTA_ERROR, "Cliente no encontrado", null);
        }

        Cliente cliente = optionalCliente.get();
        cliente.setNombres(clienteDTO.getNombres());
        cliente.setApellidos(clienteDTO.getApellidos());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setTipoDoc(clienteDTO.getTipoDoc());
        cliente.setNumDoc(clienteDTO.getNumDoc());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setProvincia(clienteDTO.getProvincia());
        cliente.setCapital(clienteDTO.getCapital());
        cliente.setFecha(clienteDTO.getFecha());

        Usuario usuario = cliente.getUsuario();
        ClienteUsuarioDTO clienteUsuarioDTO = clienteDTO.getClienteUsuarioDTO();

        usuario.setUsername(clienteUsuarioDTO.getUsername());
        usuario.setEmail(clienteUsuarioDTO.getEmail());
        usuario.setContrasena(clienteUsuarioDTO.getContrasena());
        usuario.setVigencia(clienteUsuarioDTO.isVigencia());
        usuario.setFecha(clienteUsuarioDTO.getFecha());

        clienteRepository.save(cliente);
        usuarioRepository.save(usuario);

        clienteDTO.setId(cliente.getId());
        return new GenericResponse<>(TIPO_AUTH, RPTA_OK, "Cliente actualizado correctamente", clienteDTO);
    }

    @Override
    public ClienteDTO buscarClientePorId(Long id) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if (!optionalCliente.isPresent()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        return mapToDTO(optionalCliente.get());
    }

    @Override
    public List<ClienteDTO> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        usuarioRepository.delete(cliente.getUsuario());
        clienteRepository.delete(cliente);

    }

    private ClienteDTO mapToDTO(Cliente cliente) {
        ClienteUsuarioDTO clienteUsuarioDTO = ClienteUsuarioDTO.builder()
                .id(cliente.getUsuario().getId())
                .username(cliente.getUsuario().getUsername())
                .email(cliente.getUsuario().getEmail())
                .contrasena(cliente.getUsuario().getContrasena())
                .vigencia(cliente.getUsuario().isVigencia())
                .fecha(cliente.getFecha())
                .build();

        return ClienteDTO.builder()
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
                .usuarioId(cliente.getUsuario().getId())
                .clienteUsuarioDTO(clienteUsuarioDTO)
                .build();
    }
}
