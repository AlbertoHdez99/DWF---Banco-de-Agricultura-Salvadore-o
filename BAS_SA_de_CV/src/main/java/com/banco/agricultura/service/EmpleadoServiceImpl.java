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

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoDAO empleadoDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private AccionPersonalDAO accionPersonalDAO;

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Contratar empleado + crear acción de personal ───────────────────────
    @Override
    public Empleado contratarEmpleado(Integer idUsuario, Integer idSucursal,
                                      String cargo, Integer idGerenteSucursal) {
        Usuario usuario   = obtenerUsuario(idUsuario);
        Sucursal sucursal = entityManager.find(Sucursal.class, idSucursal);
        Usuario gerente   = obtenerUsuario(idGerenteSucursal);

        // Crear el empleado con estado "En_espera" hasta que el GG apruebe
        Empleado empleado = new Empleado();
        empleado.setUsuario(usuario);
        empleado.setSucursal(sucursal);
        empleado.setCargo(cargo);
        empleado.setEstadoContratacion(Empleado.EstadoContratacion.En_espera);
        empleadoDAO.save(empleado);

        // Crear acción de personal de tipo Contratación
        crearAccion(empleado, AccionPersonal.TipoAccion.Contratación, gerente);

        return empleado;
    }

    // ─── Dar de baja + crear acción de personal ───────────────────────────────
    @Override
    public void darDeBaja(Integer idEmpleado, Integer idGerenteSucursal) {
        Empleado empleado = empleadoDAO.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + idEmpleado));
        Usuario gerente = obtenerUsuario(idGerenteSucursal);

        // Validar que el gerente no se esté dando de baja a sí mismo
        if (empleado.getUsuario().getIdUsuario().equals(idGerenteSucursal)) {
            throw new RuntimeException("Un Gerente de Sucursal no puede solicitar su propia baja de personal.");
        }

        // Cambiar estado a "En_espera" para indicar que la baja está pendiente de aprobación
        empleado.setEstadoContratacion(Empleado.EstadoContratacion.En_espera);
        empleadoDAO.update(empleado);

        // Crear la acción de personal de tipo Baja
        crearAccion(empleado, AccionPersonal.TipoAccion.Baja, gerente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> listarPorSucursal(Integer idSucursal) {
        return empleadoDAO.findBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Empleado> buscarPorUsuario(Integer idUsuario) {
        return empleadoDAO.findByUsuario(idUsuario);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Usuario obtenerUsuario(Integer id) {
        return usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    private void crearAccion(Empleado empleado, AccionPersonal.TipoAccion tipo,
                             Usuario gerenteSucursal) {
        AccionPersonal accion = new AccionPersonal();
        accion.setEmpleado(empleado);
        accion.setTipoAccion(tipo);
        accion.setEstadoAccion(AccionPersonal.EstadoAccion.En_espera);
        accion.setFechaSolicitud(LocalDateTime.now());
        accion.setGerenteSucursal(gerenteSucursal);
        accion.setGerenteGeneral(null);
        accionPersonalDAO.save(accion);
    }
}