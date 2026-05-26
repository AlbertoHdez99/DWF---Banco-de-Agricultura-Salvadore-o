package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Usuario;
import java.util.Optional;

public interface UsuarioDAO extends GenericDAO<Usuario> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDui(String dui);
    long countCuentasByUsuario(Integer idUsuario);
    boolean existsByEmail(String email);
    boolean existsByDui(String dui);
}