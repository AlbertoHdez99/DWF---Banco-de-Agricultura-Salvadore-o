package com.banco.agricultura.service;

import com.banco.agricultura.dao.UsuarioDAO;
import com.banco.agricultura.entity.Rol;
import com.banco.agricultura.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Registrar cliente (auto-registro o via cajero) ───────────────────────
    @Override
    public Usuario registrarCliente(Usuario usuario) {
        validarDuplicados(usuario);

        // Hashear contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstadoUsuario(Usuario.EstadoUsuario.Activo);

        // Asignar rol Cliente (id_rol = 1)
        Rol rolCliente = entityManager.find(Rol.class, 1);
        usuario.setRol(rolCliente);

        usuarioDAO.save(usuario);
        return usuario;
    }

    // ─── Registrar empleado con rol específico (cajero, limpieza, etc.) ───────
    @Override
    public Usuario registrarEmpleado(Usuario usuario, String nombreRol) {
        validarDuplicados(usuario);

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstadoUsuario(Usuario.EstadoUsuario.Activo);

        // Buscar rol por nombre
        Rol rol = entityManager
                .createQuery("SELECT r FROM Rol r WHERE r.nombreRol = :nombre", Rol.class)
                .setParameter("nombre", nombreRol)
                .getSingleResult();
        usuario.setRol(rol);

        usuarioDAO.save(usuario);
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorDui(String dui) {
        return usuarioDAO.findByDui(dui);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioDAO.findAll();
    }

    // ─── Baja lógica — nunca elimina el registro ──────────────────────────────
    @Override
    public void desactivar(Integer idUsuario) {
        Usuario usuario = usuarioDAO.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));
        usuario.setEstadoUsuario(Usuario.EstadoUsuario.Inactivo);
        usuarioDAO.update(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioDAO.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDui(String dui) {
        return usuarioDAO.existsByDui(dui);
    }

    // ─── Validación de duplicados antes de persistir ──────────────────────────
    private void validarDuplicados(Usuario usuario) {
        if (usuarioDAO.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        if (usuarioDAO.existsByDui(usuario.getDui())) {
            throw new RuntimeException("Ya existe un usuario con el DUI: " + usuario.getDui());
        }
    }
}