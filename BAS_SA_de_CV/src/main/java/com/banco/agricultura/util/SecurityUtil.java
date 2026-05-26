package com.banco.agricultura.util;

import jakarta.faces.context.FacesContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidad para obtener el email del usuario autenticado en contextos JSF.
 * 
 * En peticiones JSF, SecurityContextHolder puede estar vacío porque
 * el filtro de Spring Security no siempre propaga el contexto.
 * Este método intenta ambas rutas: SecurityContextHolder y HttpSession.
 */
public final class SecurityUtil {

    private SecurityUtil() {}

    /**
     * Obtiene el email (username) del usuario autenticado.
     * Primero intenta SecurityContextHolder, si falla lee de HttpSession.
     * @return email del usuario o null si no hay sesión autenticada
     */
    public static String getEmailAutenticado() {
        // 1. Intentar desde SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }

        // 2. Fallback: leer directamente de la HttpSession
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession)
                    facesContext.getExternalContext().getSession(false);
            if (session != null) {
                Object ctxObj = session.getAttribute("SPRING_SECURITY_CONTEXT");
                if (ctxObj instanceof SecurityContext) {
                    SecurityContext ctx = (SecurityContext) ctxObj;
                    Authentication sessionAuth = ctx.getAuthentication();
                    if (sessionAuth != null && sessionAuth.isAuthenticated()) {
                        return sessionAuth.getName();
                    }
                }
            }
        }

        return null;
    }
}
