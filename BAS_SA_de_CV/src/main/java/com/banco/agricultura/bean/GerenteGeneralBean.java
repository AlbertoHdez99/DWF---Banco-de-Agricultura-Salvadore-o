package com.banco.agricultura.bean;

import com.banco.agricultura.entity.AccionPersonal;
import com.banco.agricultura.entity.Movimiento;
import com.banco.agricultura.entity.Sucursal;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.MovimientoService;
import com.banco.agricultura.service.SucursalService;
import com.banco.agricultura.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class GerenteGeneralBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private SucursalService sucursalService;

    @Inject
    private MovimientoService movimientoService;

    private Usuario gerenteGeneralActual;

    // Sucursales
    private List<Sucursal> sucursales;
    private Sucursal nuevaSucursal;
    private String emailGerenteAsignar;

    // Nuevo Gerente
    private Usuario nuevoGerente;

    // Acciones de Personal
    private List<AccionPersonal> accionesPendientes;
    private transient boolean accionesYaCargadas = false;

    // Auditoria
    private List<Movimiento> todosMovimientos;

    @PostConstruct
    public void init() {
        String email = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
        if (email != null) {
            gerenteGeneralActual = usuarioService.buscarPorEmail(email).orElse(null);
        }
        nuevaSucursal = new Sucursal();
        nuevoGerente = new Usuario();
        cargarDatos();
    }

    public Usuario getGerenteGeneralActual() {
        if (gerenteGeneralActual == null) {
            String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
            if (email != null) {
                gerenteGeneralActual = usuarioService.buscarPorEmail(email).orElse(null);
            }
        }
        return gerenteGeneralActual;
    }

    public void cargarDatos() {
        sucursales = sucursalService.listarTodas();
        accionesPendientes = sucursalService.listarAccionesPendientes();
        accionesYaCargadas = true;
    }

    // Llamado por f:viewAction en acciones.xhtml para garantizar datos frescos en cada visita
    public void cargarAccionesFrescas() {
        accionesPendientes = sucursalService.listarAccionesPendientes();
        accionesYaCargadas = true;
    }

    public void cargarMovimientos() {
        if (todosMovimientos == null) {
            todosMovimientos = movimientoService.listarTodos();
        }
    }

    // --- Nuevo Gerente ---
    public void registrarNuevoGerente() {
        try {
            usuarioService.registrarEmpleado(nuevoGerente, "Gerente de Sucursal");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Nuevo Gerente de Sucursal registrado correctamente."));
            nuevoGerente = new Usuario();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // --- Sucursales ---
    public void crearSucursal() {
        try {
            Sucursal creada = sucursalService.crearSucursal(nuevaSucursal.getNombreSucursal(), nuevaSucursal.getDireccion());
            
            if (emailGerenteAsignar != null && !emailGerenteAsignar.isEmpty()) {
                Usuario gerente = usuarioService.buscarPorEmail(emailGerenteAsignar)
                    .orElseThrow(() -> new RuntimeException("Gerente con email " + emailGerenteAsignar + " no encontrado"));
                sucursalService.asignarGerente(creada.getIdSucursal(), gerente.getIdUsuario());
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Sucursal creada exitosamente."));
            nuevaSucursal = new Sucursal();
            emailGerenteAsignar = null;
            sucursales = null; // Forzar recarga
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // --- Acciones de Personal ---
    public void aprobarAccion(Integer idAccion) {
        try {
            sucursalService.aprobarAccion(idAccion, getGerenteGeneralActual().getIdUsuario());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Acción de personal aprobada."));
            cargarAccionesFrescas(); // Recargar de la base de datos
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void rechazarAccion(Integer idAccion) {
        try {
            sucursalService.rechazarAccion(idAccion, getGerenteGeneralActual().getIdUsuario());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Acción de personal rechazada."));
            cargarAccionesFrescas(); // Recargar de la base de datos
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Getters y Setters
    public List<Sucursal> getSucursales() { 
        if (sucursales == null) cargarDatos();
        return sucursales; 
    }
    public Sucursal getNuevaSucursal() { return nuevaSucursal; }
    public void setNuevaSucursal(Sucursal nuevaSucursal) { this.nuevaSucursal = nuevaSucursal; }
    
    public Usuario getNuevoGerente() { return nuevoGerente; }
    public void setNuevoGerente(Usuario nuevoGerente) { this.nuevoGerente = nuevoGerente; }

    public String getEmailGerenteAsignar() { return emailGerenteAsignar; }
    public void setEmailGerenteAsignar(String emailGerenteAsignar) { this.emailGerenteAsignar = emailGerenteAsignar; }
    
    public List<AccionPersonal> getAccionesPendientes() { 
        if (!accionesYaCargadas) {
            accionesPendientes = sucursalService.listarAccionesPendientes();
            accionesYaCargadas = true;
        }
        return accionesPendientes; 
    }
    public List<Movimiento> getTodosMovimientos() { 
        if (todosMovimientos == null) cargarMovimientos();
        return todosMovimientos; 
    }
}
