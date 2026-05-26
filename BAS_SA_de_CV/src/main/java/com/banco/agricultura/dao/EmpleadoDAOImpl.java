package com.banco.agricultura.dao;

import com.banco.agricultura.entity.Empleado;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoDAOImpl extends GenericDAOImpl<Empleado> implements EmpleadoDAO {

    public EmpleadoDAOImpl() {
        super(Empleado.class);
    }

    @Override
    public List<Empleado> findBySucursal(Integer idSucursal) {
        return entityManager
                .createQuery("SELECT e FROM Empleado e JOIN FETCH e.usuario " +
                        "WHERE e.sucursal.idSucursal = :idSucursal", Empleado.class)
                .setParameter("idSucursal", idSucursal)
                .getResultList();
    }

    @Override
    public Optional<Empleado> findByUsuario(Integer idUsuario) {
        try {
            return Optional.of(entityManager
                    .createQuery("SELECT e FROM Empleado e JOIN FETCH e.usuario " +
                            "WHERE e.usuario.idUsuario = :idUsuario", Empleado.class)
                    .setParameter("idUsuario", idUsuario)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}