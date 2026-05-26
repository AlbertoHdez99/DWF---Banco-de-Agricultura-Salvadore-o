package com.banco.agricultura.dao;

import com.banco.agricultura.entity.AccionPersonal;
import java.util.List;

public interface AccionPersonalDAO extends GenericDAO<AccionPersonal> {
    List<AccionPersonal> findByEstado(AccionPersonal.EstadoAccion estado);
    List<AccionPersonal> findBySucursal(Integer idSucursal);
}