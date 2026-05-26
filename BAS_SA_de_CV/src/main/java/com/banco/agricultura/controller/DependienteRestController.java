package com.banco.agricultura.controller;

import com.banco.agricultura.entity.Cuenta;
import com.banco.agricultura.entity.Dependiente;
import com.banco.agricultura.entity.Movimiento;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.service.CuentaService;
import com.banco.agricultura.service.MovimientoService;
import com.banco.agricultura.service.UsuarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dependiente")
public class DependienteRestController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private MovimientoService movimientoService;

    @PersistenceContext
    private EntityManager entityManager;

    private Dependiente getDependienteAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
            
        List<Dependiente> deps = entityManager.createQuery("SELECT d FROM Dependiente d WHERE d.usuario.idUsuario = :id", Dependiente.class)
            .setParameter("id", usuario.getIdUsuario())
            .getResultList();
            
        if (deps.isEmpty()) {
            throw new RuntimeException("El usuario no tiene un registro de comercio/dependiente asociado");
        }
        return deps.get(0);
    }

    @PostMapping("/buscar-cliente")
    public ResponseEntity<?> buscarCliente(@RequestBody Map<String, String> payload) {
        String dui = payload.get("dui");
        if (dui == null || dui.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El DUI es obligatorio"));
        }

        java.util.Optional<Usuario> optCliente = usuarioService.buscarPorDui(dui);
        if (optCliente.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cliente no encontrado con ese DUI"));
        }
        
        Usuario cliente = optCliente.get();
        if (!"Cliente".equals(cliente.getRol().getNombreRol()) && !"ROLE_CLIENTE".equals(cliente.getRol().getNombreRol())) {
            return ResponseEntity.badRequest().body(Map.of("error", "El DUI no pertenece a un cliente válido"));
        }
        
        List<Cuenta> cuentas = cuentaService.listarPorUsuario(cliente.getIdUsuario());
        
        // Retornamos un DTO simple
        List<Map<String, Object>> cuentasResponse = cuentas.stream().map(c -> 
            Map.<String, Object>of(
                "numeroCuenta", c.getNumeroCuenta(),
                "saldo", c.getSaldo()
            )
        ).collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.<String, Object>of(
            "cliente", cliente.getNombres() + " " + cliente.getApellidos(),
            "cuentas", cuentasResponse
        ));
    }

    @PostMapping("/deposito")
    public ResponseEntity<?> realizarDeposito(@RequestBody Map<String, Object> payload) {
        try {
            String numeroCuenta = (String) payload.get("numeroCuenta");
            BigDecimal monto = new BigDecimal(payload.get("monto").toString());
            
            Dependiente dependiente = getDependienteAutenticado();
            
            Movimiento mov = movimientoService.depositar(numeroCuenta, monto, dependiente.getUsuario().getIdUsuario(), dependiente.getIdDependiente());
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Depósito realizado exitosamente (Comisión 5% cobrada)",
                "movimientoId", mov.getIdMovimiento(),
                "monto", mov.getMonto()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/retiro")
    public ResponseEntity<?> realizarRetiro(@RequestBody Map<String, Object> payload) {
        try {
            String numeroCuenta = (String) payload.get("numeroCuenta");
            BigDecimal monto = new BigDecimal(payload.get("monto").toString());
            
            Dependiente dependiente = getDependienteAutenticado();
            
            Movimiento mov = movimientoService.retirar(numeroCuenta, monto, dependiente.getUsuario().getIdUsuario(), dependiente.getIdDependiente());
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Retiro realizado exitosamente (Comisión 5% cobrada)",
                "movimientoId", mov.getIdMovimiento(),
                "monto", mov.getMonto()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Autowired
    private com.banco.agricultura.dao.UsuarioDAO usuarioDAO;
    
    @Autowired
    private com.banco.agricultura.dao.CuentaDAO cuentaDAO;

    @GetMapping("/test-cuentas/{idUsuario}")
    public ResponseEntity<?> testCuentas(@PathVariable("idUsuario") Integer idUsuario) {
        long count = usuarioDAO.countCuentasByUsuario(idUsuario);
        java.util.List<com.banco.agricultura.entity.Cuenta> cuentas = cuentaDAO.findByUsuario(idUsuario);
        return ResponseEntity.ok("Count: " + count + ", Cuentas size: " + (cuentas != null ? cuentas.size() : "null"));
    }
}
