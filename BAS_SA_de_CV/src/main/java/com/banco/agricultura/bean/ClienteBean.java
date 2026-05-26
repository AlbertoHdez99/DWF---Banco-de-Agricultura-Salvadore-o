package com.banco.agricultura.bean;

import com.banco.agricultura.entity.Cuenta;
import com.banco.agricultura.entity.Movimiento;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class ClienteBean implements Serializable {

    @Inject
    private CuentaService cuentaService;
    
    @Inject
    private MovimientoService movimientoService;
    
    @Inject
    private UsuarioService usuarioService;

    private Usuario usuarioActual;
    private List<Cuenta> misCuentas;
    
    // Para ver movimientos de una cuenta especA-fica
    private Integer idCuentaSeleccionada;
    private List<Movimiento> movimientosSeleccionados;
    
    // Para transferencias
    private String cuentaOrigen;
    private String cuentaDestino;
    private BigDecimal montoTransferencia;

    @PostConstruct
    public void init() {
        if (getUsuarioActual() != null) {
            cargarCuentas();
        }
        movimientosSeleccionados = new ArrayList<>();
    }

    public Usuario getUsuarioActual() {
        if (usuarioActual == null) {
            String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
            if (email != null) {
                usuarioActual = usuarioService.buscarPorEmail(email).orElse(null);
            }
        }
        return usuarioActual;
    }

    public void cargarCuentas() {
        if (getUsuarioActual() != null) {
            misCuentas = cuentaService.listarPorUsuario(getUsuarioActual().getIdUsuario());
        }
    }

    public void crearCuenta() {
        try {
            if(getUsuarioActual() == null) {
                throw new RuntimeException("Sesión expirada o usuario no autenticado.");
            }
            cuentaService.crearCuenta(getUsuarioActual().getIdUsuario());
            cargarCuentas();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Nueva cuenta creada correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void verMovimientos(Integer idCuenta) {
        this.idCuentaSeleccionada = idCuenta;
        this.movimientosSeleccionados = movimientoService.listarPorCuenta(idCuenta);
    }

    public void realizarTransferencia() {
        try {
            if(getUsuarioActual() == null) {
                throw new RuntimeException("Sesión expirada o usuario no autenticado.");
            }
            movimientoService.transferir(cuentaOrigen, cuentaDestino, montoTransferencia, getUsuarioActual().getIdUsuario());
            cargarCuentas(); // Actualizar saldos
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Transferencia realizada correctamente."));
                
            // Limpiar formulario
            cuentaOrigen = null;
            cuentaDestino = null;
            montoTransferencia = null;
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en transferencia", e.getMessage()));
        }
    }

    // Getters y Setters
    public List<Cuenta> getMisCuentas() { 
        if (misCuentas == null) {
            cargarCuentas();
        }
        return misCuentas; 
    }
    public Integer getIdCuentaSeleccionada() { return idCuentaSeleccionada; }
    public void setIdCuentaSeleccionada(Integer idCuentaSeleccionada) { this.idCuentaSeleccionada = idCuentaSeleccionada; }
    public List<Movimiento> getMovimientosSeleccionados() { return movimientosSeleccionados; }
    
    public String getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(String cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public void setCuentaDestino(String cuentaDestino) { this.cuentaDestino = cuentaDestino; }
    public BigDecimal getMontoTransferencia() { return montoTransferencia; }
    public void setMontoTransferencia(BigDecimal montoTransferencia) { this.montoTransferencia = montoTransferencia; }
}
