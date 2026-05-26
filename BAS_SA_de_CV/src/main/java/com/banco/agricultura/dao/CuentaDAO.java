package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Cuenta;
import java.util.List;
import java.util.Optional;

public interface CuentaDAO extends GenericDAO<Cuenta> {
    List<Cuenta> findByUsuario(Integer idUsuario);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    boolean existsByNumeroCuenta(String numeroCuenta);
}