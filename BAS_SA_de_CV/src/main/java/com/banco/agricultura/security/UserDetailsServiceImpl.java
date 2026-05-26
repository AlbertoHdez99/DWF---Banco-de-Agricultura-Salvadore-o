package com.banco.agricultura.security;

import com.banco.agricultura.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Carga el usuario desde la BD por email ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        try {
            // Buscar usuario por email junto con su rol
            Usuario usuario = entityManager
                    .createQuery(
                            "SELECT u FROM Usuario u " +
                                    "JOIN FETCH u.rol r " +
                                    "WHERE u.email = :email " +
                                    "AND u.estadoUsuario = 'Activo'",
                            Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();

            // Mapear el rol al formato que Spring Security espera: ROLE_NombreRol
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    "ROLE_" + usuario.getRol().getNombreRol()
            );

            // Retornar UserDetails con email, password hasheado y rol
            return new User(
                    usuario.getEmail(),
                    usuario.getPassword(),
                    Collections.singletonList(authority)
            );

        } catch (NoResultException e) {
            throw new UsernameNotFoundException(
                    "Usuario no encontrado o inactivo: " + email
            );
        }
    }
}