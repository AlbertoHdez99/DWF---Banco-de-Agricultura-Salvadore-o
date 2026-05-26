package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Movimiento;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovimientoDAOImpl extends GenericDAOImpl<Movimiento> implements MovimientoDAO {

    public MovimientoDAOImpl() {
        super(Movimiento.class);
    }

    @Override
    public List<Movimiento> findByCuenta(Integer idCuenta) {
        return entityManager
                .createQuery("SELECT m FROM Movimiento m " +
                        "WHERE m.cuentaOrigen.idCuenta = :idCuenta " +
                        "OR m.cuentaDestino.idCuenta = :idCuenta " +
                        "ORDER BY m.fechaMovimiento DESC", Movimiento.class)
                .setParameter("idCuenta", idCuenta)
                .getResultList();
    }

    @Override
    public List<Movimiento> findAll() {
        return entityManager
                .createQuery("SELECT m FROM Movimiento m " +
                        "ORDER BY m.fechaMovimiento DESC", Movimiento.class)
                .getResultList();
    }
}