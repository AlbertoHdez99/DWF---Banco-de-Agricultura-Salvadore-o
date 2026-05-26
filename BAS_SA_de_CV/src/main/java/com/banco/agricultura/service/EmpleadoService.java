package com.banco.agricultura.service;

import com.banco.agricultura.entity.AccionPersonal;
import com.banco.agricultura.entity.Empleado;
import java.util.List;

public interface EmpleadoService {
    /**
     * Registra el usuario como empleado en la sucursal y crea
     * automáticamente una AccionPersonal de tipo Contratación en estado En_espera.
     */
    Empleado contratarEmpleado(Integer idUsuario, Integer idSucursal,
                               String cargo, Integer idGerenteSucursal);

    /**
     * Cambia el estado del empleado a Inactivo y crea una AccionPersonal
     * de tipo Baja en estado En_espera para aprobación del gerente general.
     */
    void darDeBaja(Integer idEmpleado, Integer idGerenteSucursal);

    List<Empleado> listarPorSucursal(Integer idSucursal);
    
    java.util.Optional<Empleado> buscarPorUsuario(Integer idUsuario);
}