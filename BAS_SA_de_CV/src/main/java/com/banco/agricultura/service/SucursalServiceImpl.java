package com.banco.agricultura.service;

import com.banco.agricultura.dao.AccionPersonalDAO;
import com.banco.agricultura.dao.EmpleadoDAO;
import com.banco.agricultura.dao.UsuarioDAO;
import com.banco.agricultura.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SucursalServiceImpl implements SucursalService {

    @Autowired
    private AccionPersonalDAO accionPersonalDAO;

    @Autowired
    private EmpleadoDAO empleadoDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Crear nueva sucursal (Gerente General) ───────────────────────────────
    @Override
    public Sucursal crearSucursal(String nombre, String direccion) {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombreSucursal(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setGerente(null);
        entityManager.persist(sucursal);
        return sucursal;
    }

    // ─── Asignar gerente a una sucursal ───────────────────────────────────────
    @Override
    public Sucursal asignarGerente(Integer idSucursal, Integer idGerente) {
        Sucursal sucursal = entityManager.find(Sucursal.class, idSucursal);
        if (sucursal == null) throw new RuntimeException("Sucursal no encontrada: " + idSucursal);

        Usuario gerente = usuarioDAO.findById(idGerente)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idGerente));

        // Validar que el gerente no esté ya asignado a otra sucursal
        Long count = entityManager.createQuery("SELECT COUNT(s) FROM Sucursal s WHERE s.gerente.idUsuario = :idGerente AND s.idSucursal != :idSucursal", Long.class)
                .setParameter("idGerente", idGerente)
                .setParameter("idSucursal", idSucursal)
                .getSingleResult();
        
        if (count > 0) {
            throw new RuntimeException("El gerente seleccionado ya está asignado a otra sucursal. Debe seleccionar un gerente diferente.");
        }

        sucursal.setGerente(gerente);
        entityManager.merge(sucursal);
        return sucursal;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> listarTodas() {
        return entityManager
                .createQuery("SELECT s FROM Sucursal s", Sucursal.class)
                .getResultList();
    }

    // ─── Aprobar acción de personal (Gerente General) ────────────────────────
    @Override
    public AccionPersonal aprobarAccion(Integer idAccion, Integer idGerenteGeneral) {
        AccionPersonal accion = obtenerAccion(idAccion);
        validarEnEspera(accion);

        Usuario gg = usuarioDAO.findById(idGerenteGeneral)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idGerenteGeneral));

        accion.setEstadoAccion(AccionPersonal.EstadoAccion.Aprobada);
        accion.setGerenteGeneral(gg);
        accionPersonalDAO.update(accion);

        // Si es contratación aprobada -> activar al empleado
        // Si es baja aprobada -> desactivar al empleado y usuario
        Empleado emp = accion.getEmpleado();
        if (accion.getTipoAccion() == AccionPersonal.TipoAccion.Contratación) {
            emp.setEstadoContratacion(Empleado.EstadoContratacion.Activo);
        } else if (accion.getTipoAccion() == AccionPersonal.TipoAccion.Baja) {
            emp.setEstadoContratacion(Empleado.EstadoContratacion.Inactivo);
            emp.getUsuario().setEstadoUsuario(Usuario.EstadoUsuario.Inactivo);
            usuarioDAO.update(emp.getUsuario());
        }
        empleadoDAO.update(emp);

        return accion;
    }

    // ─── Rechazar acción de personal (Gerente General) ───────────────────────
    @Override
    public AccionPersonal rechazarAccion(Integer idAccion, Integer idGerenteGeneral) {
        AccionPersonal accion = obtenerAccion(idAccion);
        validarEnEspera(accion);

        Usuario gg = usuarioDAO.findById(idGerenteGeneral)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idGerenteGeneral));

        accion.setEstadoAccion(AccionPersonal.EstadoAccion.Rechazada);
        accion.setGerenteGeneral(gg);
        accionPersonalDAO.update(accion);

        // Si se rechaza la contratación -> desactivar al empleado y usuario
        // Si se rechaza la baja -> devolver al empleado a Activo
        Empleado emp = accion.getEmpleado();
        if (accion.getTipoAccion() == AccionPersonal.TipoAccion.Contratación) {
            emp.setEstadoContratacion(Empleado.EstadoContratacion.Inactivo);
            emp.getUsuario().setEstadoUsuario(Usuario.EstadoUsuario.Inactivo);
            usuarioDAO.update(emp.getUsuario());
        } else if (accion.getTipoAccion() == AccionPersonal.TipoAccion.Baja) {
            emp.setEstadoContratacion(Empleado.EstadoContratacion.Activo);
        }
        empleadoDAO.update(emp);

        return accion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccionPersonal> listarAccionesPendientes() {
        return accionPersonalDAO.findByEstado(AccionPersonal.EstadoAccion.En_espera);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private AccionPersonal obtenerAccion(Integer id) {
        return accionPersonalDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Acción de personal no encontrada: " + id));
    }

    private void validarEnEspera(AccionPersonal accion) {
        if (accion.getEstadoAccion() != AccionPersonal.EstadoAccion.En_espera) {
            throw new RuntimeException(
                    "La acción ya fue procesada. Estado actual: " + accion.getEstadoAccion()
            );
        }
    }
}