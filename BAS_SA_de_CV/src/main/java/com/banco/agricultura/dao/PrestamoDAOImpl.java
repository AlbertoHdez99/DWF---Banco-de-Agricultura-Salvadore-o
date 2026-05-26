package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Prestamo;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PrestamoDAOImpl extends GenericDAOImpl<Prestamo> implements PrestamoDAO {

    public PrestamoDAOImpl() {
        super(Prestamo.class);
    }

    @Override
    public List<Prestamo> findByCliente(Integer idCliente) {
        return entityManager
                .createQuery("SELECT p FROM Prestamo p " +
                        "WHERE p.cliente.idUsuario = :idCliente", Prestamo.class)
                .setParameter("idCliente", idCliente)
                .getResultList();
    }

    @Override
    public List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado) {
        return entityManager
                .createQuery("SELECT p FROM Prestamo p " +
                        "WHERE p.estadoPrestamo = :estado", Prestamo.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    @Override
    public List<Prestamo> findByCajero(Integer idCajero) {
        return entityManager
                .createQuery("SELECT p FROM Prestamo p " +
                        "WHERE p.cajero.idUsuario = :idCajero", Prestamo.class)
                .setParameter("idCajero", idCajero)
                .getResultList();
    }
}