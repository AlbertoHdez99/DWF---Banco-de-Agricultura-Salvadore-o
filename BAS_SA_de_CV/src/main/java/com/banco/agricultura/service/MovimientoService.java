package com.banco.agricultura.service;

import com.banco.agricultura.entity.Movimiento;
import java.math.BigDecimal;
import java.util.List;

public interface MovimientoService {
    Movimiento depositar(String numeroCuenta, BigDecimal monto,
                         Integer idUsuarioEjecutor, Integer idDependiente);

    Movimiento retirar(String numeroCuenta, BigDecimal monto,
                       Integer idUsuarioEjecutor, Integer idDependiente);

    Movimiento transferir(String numeroCuentaOrigen, String numeroCuentaDestino,
                          BigDecimal monto, Integer idUsuarioEjecutor);

    // Cajero: valida que DUI coincida con la cuenta antes de operar
    Movimiento depositarConValidacionDui(String dui, String numeroCuenta,
                                         BigDecimal monto, Integer idCajero);

    Movimiento retirarConValidacionDui(String dui, String numeroCuenta,
                                       BigDecimal monto, Integer idCajero);

    List<Movimiento> listarPorCuenta(Integer idCuenta);
    List<Movimiento> listarTodos();  // Solo gerente general
}