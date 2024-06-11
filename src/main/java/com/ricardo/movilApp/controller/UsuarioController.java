package com.ricardo.movilApp.controller;

import com.ricardo.movilApp.dto.UsuarioDTO;
import com.ricardo.movilApp.service.UsuarioService;
import com.ricardo.movilApp.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UsuarioDTO>> login(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok(usuarioService.login(email, password));
    }

    @PostMapping("/toggle-vigencia")
    public GenericResponse<UsuarioDTO> toggleUserVigencia(@RequestParam Long userId, @RequestParam boolean vigencia) {
        return usuarioService.toggleUserVigencia(userId, vigencia);
    }

    @PostMapping("/forgot-password-")
    public GenericResponse<String> forgotPassword(@RequestParam String email) {
        return usuarioService.requestPasswordReset(email);
    }

    @PostMapping("/reset-password")
    public GenericResponse<String> verifyAndResetPassword(@RequestParam String otp, @RequestParam String newPassword) {
        return usuarioService.verifyAndResetPassword(otp, newPassword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

}
