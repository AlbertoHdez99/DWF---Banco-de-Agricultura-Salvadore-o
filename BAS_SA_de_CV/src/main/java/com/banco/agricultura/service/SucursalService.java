package com.banco.agricultura.service;

import com.banco.agricultura.entity.AccionPersonal;
import com.banco.agricultura.entity.Sucursal;
import java.util.List;

public interface SucursalService {
    Sucursal crearSucursal(String nombre, String direccion);
    Sucursal asignarGerente(Integer idSucursal, Integer idGerente);
    List<Sucursal> listarTodas();

    // Gerente General: aprobar o rechazar acciones de personal
    AccionPersonal aprobarAccion(Integer idAccion, Integer idGerenteGeneral);
    AccionPersonal rechazarAccion(Integer idAccion, Integer idGerenteGeneral);
    List<AccionPersonal> listarAccionesPendientes();
}