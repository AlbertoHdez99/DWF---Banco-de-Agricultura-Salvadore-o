package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Empleado;
import java.util.List;
import java.util.Optional;

public interface EmpleadoDAO extends GenericDAO<Empleado> {
    List<Empleado> findBySucursal(Integer idSucursal);
    Optional<Empleado> findByUsuario(Integer idUsuario);
}