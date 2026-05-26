package com.banco.agricultura.bean;

import com.banco.agricultura.entity.Cuenta;
import com.banco.agricultura.entity.Dependiente;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.CuentaService;
import com.banco.agricultura.service.MovimientoService;
import com.banco.agricultura.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Named
@ViewScoped
public class DependienteBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private CuentaService cuentaService;

    @Inject
    private MovimientoService movimientoService;

    @PersistenceContext
    private EntityManager entityManager;

    private Usuario dependienteActual;
    private Dependiente entidadDependiente;

    // Búsqueda
    private String buscarDui;
    private Usuario clienteEncontrado;
    private List<Cuenta> cuentasCliente;

    // Operación
    private String cuentaSeleccionada;
    private BigDecimal montoOperacion;

    @PostConstruct
    @Transactional
    public void init() {
        String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
        if (email != null) {
            dependienteActual = usuarioService.buscarPorEmail(email).orElse(null);
            if (dependienteActual != null) {
                // Buscar la entidad Dependiente asociada al usuario
                List<Dependiente> deps = entityManager.createQuery("SELECT d FROM Dependiente d WHERE d.usuario.idUsuario = :id", Dependiente.class)
                    .setParameter("id", dependienteActual.getIdUsuario())
                    .getResultList();
                if (!deps.isEmpty()) {
                    entidadDependiente = deps.get(0);
                } else {
                    // Si no existe, la creamos por defecto para evitar errores en las comisiones
                    Dependiente nuevoDep = new Dependiente();
                    nuevoDep.setUsuario(dependienteActual);
                    nuevoDep.setNombreComercio("Comercio de " + dependienteActual.getNombres());
                    entityManager.persist(nuevoDep);
                    entidadDependiente = nuevoDep;
                }
            }
        }
    }

    public void buscarCliente() {
        usuarioService.buscarPorDui(buscarDui).ifPresentOrElse(cliente -> {
            if ("ROLE_CLIENTE".equals(cliente.getRol().getNombreRol()) || "Cliente".equals(cliente.getRol().getNombreRol())) {
                clienteEncontrado = cliente;
                cuentasCliente = cuentaService.listarPorUsuario(cliente.getIdUsuario());
                if (cuentasCliente.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "El cliente no tiene cuentas activas."));
                }
            } else {
                clienteEncontrado = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El DUI no pertenece a un cliente."));
            }
        }, () -> {
            clienteEncontrado = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cliente no encontrado."));
        });
    }

    public void realizarDeposito() {
        if (entidadDependiente == null) return;
        try {
            movimientoService.depositar(cuentaSeleccionada, montoOperacion, dependienteActual.getIdUsuario(), entidadDependiente.getIdDependiente());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Depósito realizado. Comisión del 5% aplicada automáticamente."));
            limpiarOperacion();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void realizarRetiro() {
        if (entidadDependiente == null) return;
        try {
            movimientoService.retirar(cuentaSeleccionada, montoOperacion, dependienteActual.getIdUsuario(), entidadDependiente.getIdDependiente());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Retiro realizado. Comisión del 5% aplicada automáticamente."));
            limpiarOperacion();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    private void limpiarOperacion() {
        cuentaSeleccionada = null;
        montoOperacion = null;
        buscarCliente(); // Refrescar saldos
    }

    public BigDecimal getTotalComisiones() {
        if (entidadDependiente == null) return BigDecimal.ZERO;
        try {
            BigDecimal total = entityManager.createQuery("SELECT SUM(c.montoComision) FROM Comision c WHERE c.dependiente.idDependiente = :id", BigDecimal.class)
                .setParameter("id", entidadDependiente.getIdDependiente())
                .getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // Getters y Setters
    public Dependiente getEntidadDependiente() { return entidadDependiente; }
    public String getBuscarDui() { return buscarDui; }
    public void setBuscarDui(String buscarDui) { this.buscarDui = buscarDui; }
    public Usuario getClienteEncontrado() { return clienteEncontrado; }
    public List<Cuenta> getCuentasCliente() { return cuentasCliente; }
    public String getCuentaSeleccionada() { return cuentaSeleccionada; }
    public void setCuentaSeleccionada(String cuentaSeleccionada) { this.cuentaSeleccionada = cuentaSeleccionada; }
    public BigDecimal getMontoOperacion() { return montoOperacion; }
    public void setMontoOperacion(BigDecimal montoOperacion) { this.montoOperacion = montoOperacion; }
}
