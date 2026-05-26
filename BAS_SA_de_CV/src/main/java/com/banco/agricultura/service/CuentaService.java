package com.banco.agricultura.service;

import com.banco.agricultura.entity.Cuenta;
import java.util.List;
import java.util.Optional;

public interface CuentaService {
    Cuenta crearCuenta(Integer idUsuario);
    List<Cuenta> listarPorUsuario(Integer idUsuario);
    List<Cuenta> listarPorDui(String dui);
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);
}