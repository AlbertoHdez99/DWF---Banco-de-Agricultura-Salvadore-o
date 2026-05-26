package com.banco.agricultura.bean;

import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.UsuarioService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;

@Named
@RequestScoped
public class RegistroBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    private String dui;
    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private BigDecimal salario;

    public String registrar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDui(this.dui);
            nuevoUsuario.setNombres(this.nombres);
            nuevoUsuario.setApellidos(this.apellidos);
            nuevoUsuario.setEmail(this.email);
            nuevoUsuario.setPassword(this.password);
            nuevoUsuario.setSalario(this.salario);

            usuarioService.registrarCliente(nuevoUsuario);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente. Ahora puede iniciar sesión."));
            
            // Limpiar formulario
            this.dui = null;
            this.nombres = null;
            this.apellidos = null;
            this.email = null;
            this.password = null;
            this.salario = null;
            
            return "/views/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el usuario: " + e.getMessage()));
            return null;
        }
    }

    // Getters y Setters
    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
}
