package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Usuario;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario> implements UsuarioDAO {

    public UsuarioDAOImpl() {
        super(Usuario.class);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        try {
            return Optional.of(entityManager
                    .createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol " +
                            "WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> findByDui(String dui) {
        try {
            return Optional.of(entityManager
                    .createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol " +
                            "WHERE u.dui = :dui", Usuario.class)
                    .setParameter("dui", dui)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public long countCuentasByUsuario(Integer idUsuario) {
        return entityManager
                .createQuery("SELECT COUNT(c) FROM Cuenta c " +
                        "WHERE c.usuario.idUsuario = :idUsuario", Long.class)
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager
                .createQuery("SELECT COUNT(u) FROM Usuario u " +
                        "WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByDui(String dui) {
        Long count = entityManager
                .createQuery("SELECT COUNT(u) FROM Usuario u " +
                        "WHERE u.dui = :dui", Long.class)
                .setParameter("dui", dui)
                .getSingleResult();
        return count > 0;
    }
}