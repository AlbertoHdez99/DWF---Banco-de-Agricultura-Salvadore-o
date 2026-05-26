package com.banco.agricultura.bean;

import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.MovimientoService;
import com.banco.agricultura.service.PrestamoService;
import com.banco.agricultura.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Named
@ViewScoped
public class CajeroBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private MovimientoService movimientoService;

    @Inject
    private PrestamoService prestamoService;

    private Usuario cajeroActual;

    // Campos para registro de usuarios (Cliente o Dependiente)
    private Usuario nuevoUsuario;
    private String tipoRegistro = "ROLE_CLIENTE";

    // Campos para operaciones
    private String operacionDui;
    private String operacionCuenta;
    private BigDecimal operacionMonto;

    // Campos para préstamos
    private String prestamoDui;
    private BigDecimal prestamoMonto;
    private BigDecimal prestamoCuotaCalculada;
    private int prestamoAniosCalculados;
    private Usuario clientePrestamo;

    public Usuario getCajeroActual() {
        if (cajeroActual == null) {
            String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
            if (email != null) {
                cajeroActual = usuarioService.buscarPorEmail(email).orElse(null);
            }
        }
        return cajeroActual;
    }

    @PostConstruct
    public void init() {
        nuevoUsuario = new Usuario();
    }

    // --- Registro ---
    public void registrarUsuario() {
        try {
            if ("Cliente".equals(tipoRegistro)) {
                usuarioService.registrarCliente(nuevoUsuario);
            } else {
                usuarioService.registrarEmpleado(nuevoUsuario, tipoRegistro);
            }
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente."));
            nuevoUsuario = new Usuario(); // Reset form
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en registro", e.getMessage()));
        }
    }

    // --- Operaciones ---
    private Integer getIdCajeroActual() {
        String email = com.banco.agricultura.util.SecurityUtil.getEmailAutenticado();
        if (email != null) {
            return usuarioService.buscarPorEmail(email)
                    .map(Usuario::getIdUsuario)
                    .orElseThrow(() -> new RuntimeException("Cajero no encontrado en BD"));
        }
        throw new RuntimeException("No hay sesión de cajero activa");
    }

    public void realizarDeposito() {
        try {
            movimientoService.depositarConValidacionDui(operacionDui, operacionCuenta, operacionMonto, getIdCajeroActual());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Depósito realizado."));
            limpiarOperacion();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void realizarRetiro() {
        try {
            movimientoService.retirarConValidacionDui(operacionDui, operacionCuenta, operacionMonto, getIdCajeroActual());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Retiro realizado."));
            limpiarOperacion();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    private void limpiarOperacion() {
        operacionDui = null; operacionCuenta = null; operacionMonto = null;
    }

    // --- Préstamos ---
    public void buscarClientePrestamo() {
        Optional<Usuario> opt = usuarioService.buscarPorDui(prestamoDui);
        if (opt.isPresent() && ("ROLE_CLIENTE".equals(opt.get().getRol().getNombreRol()) || "Cliente".equals(opt.get().getRol().getNombreRol()))) {
            clientePrestamo = opt.get();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cliente no encontrado o no es válido."));
            clientePrestamo = null;
        }
    }

    public void calcularPrestamo() {
        if (clientePrestamo == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Busque primero al cliente."));
            return;
        }
        try {
            prestamoCuotaCalculada = prestamoService.calcularCuotaPrevia(clientePrestamo.getIdUsuario(), prestamoMonto);
            prestamoAniosCalculados = prestamoService.calcularAniosPrevio(clientePrestamo.getIdUsuario(), prestamoMonto);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de cálculo", e.getMessage()));
            prestamoCuotaCalculada = null;
        }
    }

    public void abrirPrestamo() {
        if (clientePrestamo != null && prestamoMonto != null) {
            try {
                prestamoService.abrirPrestamo(clientePrestamo.getIdUsuario(), getIdCajeroActual(), prestamoMonto);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Préstamo en espera de aprobación."));
                clientePrestamo = null;
                prestamoMonto = null;
                prestamoCuotaCalculada = null;
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Debe buscar al cliente y calcular la cuota antes de solicitar el préstamo."));
        }
    }

    // Getters / Setters
    public Usuario getNuevoUsuario() { return nuevoUsuario; }
    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    public String getOperacionDui() { return operacionDui; }
    public void setOperacionDui(String operacionDui) { this.operacionDui = operacionDui; }
    public String getOperacionCuenta() { return operacionCuenta; }
    public void setOperacionCuenta(String operacionCuenta) { this.operacionCuenta = operacionCuenta; }
    public BigDecimal getOperacionMonto() { return operacionMonto; }
    public void setOperacionMonto(BigDecimal operacionMonto) { this.operacionMonto = operacionMonto; }
    public String getPrestamoDui() { return prestamoDui; }
    public void setPrestamoDui(String prestamoDui) { this.prestamoDui = prestamoDui; }
    public BigDecimal getPrestamoMonto() { return prestamoMonto; }
    public void setPrestamoMonto(BigDecimal prestamoMonto) { this.prestamoMonto = prestamoMonto; }
    public BigDecimal getPrestamoCuotaCalculada() { return prestamoCuotaCalculada; }
    public int getPrestamoAniosCalculados() { return prestamoAniosCalculados; }
    public Usuario getClientePrestamo() { return clientePrestamo; }
}
