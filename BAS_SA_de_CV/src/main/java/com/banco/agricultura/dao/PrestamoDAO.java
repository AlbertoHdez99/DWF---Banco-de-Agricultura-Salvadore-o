package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Prestamo;
import java.util.List;

public interface PrestamoDAO extends GenericDAO<Prestamo> {
    List<Prestamo> findByCliente(Integer idCliente);
    List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado);
    List<Prestamo> findByCajero(Integer idCajero);
}