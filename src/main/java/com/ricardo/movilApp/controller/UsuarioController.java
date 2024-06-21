package com.ricardo.movilApp.controller;

import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.service.UsuarioService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/create")
    public GenericResponse<UsuarioDTO> createUser(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.create(usuarioDTO);
    }

    @GetMapping("/getById/{id}")
    public GenericResponse<UsuarioDTO> getUserById(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id);
    }

    @PutMapping("/update/{id}")
    public GenericResponse<UsuarioDTO> updateUser(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.update(id, usuarioDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UsuarioDTO>> login(@RequestParam String username, @RequestParam String password) {
        GenericResponse<UsuarioDTO> response = usuarioService.login(username, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/toggle-vigencia/{id}")
    public GenericResponse<UsuarioDTO> toggleUserVigencia(@PathVariable Long id, @RequestParam boolean vigencia) {
        return usuarioService.toggleUserVigencia(id, vigencia);
    }

    @PostMapping("/forgot-password")
    public GenericResponse<String> forgotPassword(@RequestParam String email) {
        return usuarioService.requestPasswordReset(email);
    }

    @PostMapping("/reset-password")
    public GenericResponse<String> verifyAndResetPassword(@RequestParam String otp, @RequestParam String newPassword) {
        return usuarioService.verifyAndResetPassword(otp, newPassword);
    }

    @DeleteMapping("/delete/{id}")
    public GenericResponse<UsuarioDTO> deleteUser(@PathVariable Long id) {
        return usuarioService.delete(id);
    }


    @GetMapping("/listar")
    public GenericResponse<List<UsuarioDTO>> listUser() {
        return usuarioService.listar();
    }

    @GetMapping("/getByIdUsuario/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioPorId(id));
    }

}
