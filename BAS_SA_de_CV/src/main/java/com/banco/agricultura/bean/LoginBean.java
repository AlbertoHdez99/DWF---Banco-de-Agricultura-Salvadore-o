package com.banco.agricultura.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;

@Named
@RequestScoped
public class LoginBean {

    private String email;
    private String password;

    public String login() {
        System.out.println("=== INTENTO DE LOGIN INICIADO PARA: " + email + " ===");
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        ServletContext servletContext = (ServletContext) externalContext.getContext();

        try {
            ApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            AuthenticationManager authManager = springContext.getBean(AuthenticationManager.class);
            
            System.out.println("=== AUTENTICANDO CON SPRING SECURITY ===");
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            System.out.println("=== AUTENTICACIÓN EXITOSA PARA: " + auth.getName() + " ===");

            com.banco.agricultura.dao.UsuarioDAO usuarioDAO = springContext.getBean(com.banco.agricultura.dao.UsuarioDAO.class);
            usuarioDAO.findByEmail(auth.getName()).ifPresent(u -> {
                request.getSession().setAttribute("nombreUsuario", u.getNombres() + " " + u.getApellidos());
            });

            // Redirigir según el rol del usuario autenticado
            boolean isCliente = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Cliente"));
            boolean isCajero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Cajero"));
            boolean isGerenteSucursal = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Gerente de Sucursal"));
            boolean isGerenteGeneral = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Gerente General"));
            boolean isDependiente = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Dependiente"));

            System.out.println("=== REDIRIGIENDO SEGÚN ROL ===");
            if (isCliente) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/cliente/dashboard.xhtml");
            } else if (isCajero) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/cajero/dashboard.xhtml");
            } else if (isGerenteSucursal) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/gerente-sucursal/dashboard.xhtml");
            } else if (isGerenteGeneral) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/gerente-general/dashboard.xhtml");
            } else if (isDependiente) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/dependiente/dashboard.xhtml");
            } else {
                externalContext.redirect(externalContext.getRequestContextPath() + "/views/dashboard.xhtml");
            }
            return null;
        } catch (Exception e) {
            System.out.println("=== EXCEPCIÓN DURANTE LOGIN ===");
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fallido", "Credenciales incorrectas o usuario inactivo."));
            return null;
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String logout() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            
            // Invalidar sesión web y Spring Security
            request.logout();
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();
            
            externalContext.redirect(externalContext.getRequestContextPath() + "/views/login.xhtml?logout=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
