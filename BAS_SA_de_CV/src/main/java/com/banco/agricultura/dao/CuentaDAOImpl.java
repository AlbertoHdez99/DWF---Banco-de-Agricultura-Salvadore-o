package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Cuenta;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CuentaDAOImpl extends GenericDAOImpl<Cuenta> implements CuentaDAO {

    public CuentaDAOImpl() {
        super(Cuenta.class);
    }

    @Override
    public List<Cuenta> findByUsuario(Integer idUsuario) {
        return entityManager
                .createQuery("SELECT c FROM Cuenta c " +
                        "WHERE c.usuario.idUsuario = :idUsuario", Cuenta.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        try {
            return Optional.of(entityManager
                    .createQuery("SELECT c FROM Cuenta c " +
                            "WHERE c.numeroCuenta = :numeroCuenta", Cuenta.class)
                    .setParameter("numeroCuenta", numeroCuenta)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Cuenta c " +
                        "WHERE c.numeroCuenta = :numeroCuenta", Long.class)
                .setParameter("numeroCuenta", numeroCuenta)
                .getSingleResult();
        return count > 0;
    }
}