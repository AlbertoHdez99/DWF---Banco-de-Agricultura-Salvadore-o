package com.banco.agricultura.service;

import com.banco.agricultura.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario registrarCliente(Usuario usuario);
    Usuario registrarEmpleado(Usuario usuario, String nombreRol);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorDui(String dui);
    List<Usuario> listarTodos();
    void desactivar(Integer idUsuario);
    boolean existeEmail(String email);
    boolean existeDui(String dui);
}