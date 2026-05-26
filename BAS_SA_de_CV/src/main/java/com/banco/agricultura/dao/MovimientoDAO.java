package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Movimiento;
import java.util.List;

public interface MovimientoDAO extends GenericDAO<Movimiento> {
    List<Movimiento> findByCuenta(Integer idCuenta);
    List<Movimiento> findAll();
}