package com.banco.agricultura.config;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // ─── 1. Contexto raíz de Spring (AppConfig + SecurityConfig) ──────────
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class, SecurityConfig.class);

        // Registrar el listener que arranca el contexto raíz
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // ─── 2. Contexto de Spring MVC (WebConfig) ────────────────────────────
        AnnotationConfigWebApplicationContext webContext =
                new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);

        // ─── 3. DispatcherServlet de Spring MVC ───────────────────────────────
        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(webContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/*");

        // 🔹 4. Encoding UTF-8 para formularios JSF 🔹
        FilterRegistration.Dynamic encodingFilter =
                servletContext.addFilter("encodingFilter",
                        new org.springframework.web.filter.CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");

        // 🔹 5. Spring Security Filter Chain 🔹
        FilterRegistration.Dynamic securityFilter =
                servletContext.addFilter("springSecurityFilterChain",
                        new org.springframework.web.filter.DelegatingFilterProxy());
        securityFilter.addMappingForUrlPatterns(null, true, "/*");
    }
}