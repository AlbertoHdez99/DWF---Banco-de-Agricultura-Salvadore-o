package com.banco.agricultura.bean;

import com.banco.agricultura.entity.Empleado;
import com.banco.agricultura.entity.Prestamo;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.EmpleadoService;
import com.banco.agricultura.service.PrestamoService;
import com.banco.agricultura.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class GerenteSucursalBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private PrestamoService prestamoService;

    private Usuario gerenteActual;
    private Empleado empleadoGerente;

    // Empleados
    private List<Empleado> empleadosSucursal;
    private Usuario nuevoEmpleado;
    private String rolNuevoEmpleado;

    // Préstamos
    private List<Prestamo> prestamosEnEspera;

    @PostConstruct
    public void init() {
        String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
        if (email != null) {
            gerenteActual = usuarioService.buscarPorEmail(email).orElse(null);
            if (gerenteActual != null) {
                Optional<Empleado> empOpt = empleadoService.buscarPorUsuario(gerenteActual.getIdUsuario());
                if (empOpt.isPresent()) {
                    empleadoGerente = empOpt.get();
                    cargarEmpleados();
                    cargarPrestamos();
                }
            }
        }
        nuevoEmpleado = new Usuario();
    }

    // Getters / Setters con Lazy Loading para evitar problemas de ciclo de vida con Spring Security

    public Usuario getGerenteActual() {
        if (gerenteActual == null) {
            String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
            if (email != null) {
                gerenteActual = usuarioService.buscarPorEmail(email).orElse(null);
            }
        }
        return gerenteActual;
    }

    public Empleado getEmpleadoGerente() {
        if (empleadoGerente == null && getGerenteActual() != null) {
            empleadoService.buscarPorUsuario(getGerenteActual().getIdUsuario())
                           .ifPresent(emp -> empleadoGerente = emp);
        }
        return empleadoGerente;
    }

    public List<Empleado> getEmpleadosSucursal() {
        if (empleadosSucursal == null) {
            cargarEmpleados();
        }
        return empleadosSucursal;
    }

    public void cargarEmpleados() {
        if (getEmpleadoGerente() != null) {
            empleadosSucursal = empleadoService.listarPorSucursal(getEmpleadoGerente().getSucursal().getIdSucursal());
        }
    }

    public List<Prestamo> getPrestamosEnEspera() {
        if (prestamosEnEspera == null) {
            cargarPrestamos();
        }
        return prestamosEnEspera;
    }

    public void cargarPrestamos() {
        prestamosEnEspera = prestamoService.listarEnEspera();
    }

    public void aprobarPrestamo(Integer idPrestamo) {
        try {
            prestamoService.aprobar(idPrestamo, getGerenteActual().getIdUsuario());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Préstamo aprobado exitosamente."));
            prestamosEnEspera = null; // Forzar recarga
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void rechazarPrestamo(Integer idPrestamo) {
        try {
            prestamoService.rechazar(idPrestamo, getGerenteActual().getIdUsuario());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Préstamo rechazado."));
            prestamosEnEspera = null; // Forzar recarga
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void registrarEmpleado() {
        try {
            Usuario usuarioCreado = usuarioService.registrarEmpleado(nuevoEmpleado, rolNuevoEmpleado);
            empleadoService.contratarEmpleado(usuarioCreado.getIdUsuario(), 
                getEmpleadoGerente().getSucursal().getIdSucursal(), 
                rolNuevoEmpleado, 
                getGerenteActual().getIdUsuario());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleado registrado. Se generó acción de personal en espera."));
            nuevoEmpleado = new Usuario(); // reset
            empleadosSucursal = null; // Forzar recarga
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void darDeBaja(Integer idEmpleado) {
        try {
            empleadoService.darDeBaja(idEmpleado, getGerenteActual().getIdUsuario());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Se solicitó la baja. Acción de personal en espera."));
            empleadosSucursal = null; // Forzar recarga
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public Usuario getNuevoEmpleado() { return nuevoEmpleado; }
    public String getRolNuevoEmpleado() { return rolNuevoEmpleado; }
    public void setRolNuevoEmpleado(String rolNuevoEmpleado) { this.rolNuevoEmpleado = rolNuevoEmpleado; }
}
