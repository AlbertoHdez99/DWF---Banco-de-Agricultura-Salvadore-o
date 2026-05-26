package com.banco.agricultura.dao;

import com.banco.agricultura.entity.AccionPersonal;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AccionPersonalDAOImpl extends GenericDAOImpl<AccionPersonal> implements AccionPersonalDAO {

    public AccionPersonalDAOImpl() {
        super(AccionPersonal.class);
    }

    @Override
    public List<AccionPersonal> findByEstado(AccionPersonal.EstadoAccion estado) {
        String estadoStr = "En espera";
        if (estado == AccionPersonal.EstadoAccion.Aprobada) estadoStr = "Aprobada";
        else if (estado == AccionPersonal.EstadoAccion.Rechazada) estadoStr = "Rechazada";

        // Limpiar cache de primer nivel para obtener datos frescos de la BD
        entityManager.clear();

        return entityManager
                .createNativeQuery("SELECT * FROM acciones_personal WHERE estado_accion = :est", AccionPersonal.class)
                .setParameter("est", estadoStr)
                .getResultList();
    }

    @Override
    public List<AccionPersonal> findBySucursal(Integer idSucursal) {
        return entityManager
                .createQuery("SELECT a FROM AccionPersonal a " +
                        "JOIN FETCH a.empleado e " +
                        "WHERE e.sucursal.idSucursal = :idSucursal", AccionPersonal.class)
                .setParameter("idSucursal", idSucursal)
                .getResultList();
    }
}