package com.banco.agricultura.controller;

import com.banco.agricultura.dao.CuentaDAO;
import com.banco.agricultura.dao.UsuarioDAO;
import com.banco.agricultura.entity.Cuenta;
import com.banco.agricultura.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DebugController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private CuentaDAO cuentaDAO;

    @GetMapping("/debug-user/{email}")
    public ResponseEntity<?> debugUser(@PathVariable("email") String email) {
        try {
            Usuario u = usuarioDAO.findByEmail(email).orElse(null);
            if (u == null) return ResponseEntity.ok("Usuario " + email + " no encontrado.");

            long count = usuarioDAO.countCuentasByUsuario(u.getIdUsuario());
            List<Cuenta> cuentas = cuentaDAO.findByUsuario(u.getIdUsuario());
            
            String ctasStr = cuentas.stream()
                .map(c -> c.getNumeroCuenta() + " ($" + c.getSaldo() + ")")
                .collect(Collectors.joining(", "));

            return ResponseEntity.ok(
                "ID: " + u.getIdUsuario() + "\n" +
                "Email: " + u.getEmail() + "\n" +
                "Cuentas Count (via JPQL COUNT): " + count + "\n" +
                "Cuentas List (via JPQL SELECT) size: " + cuentas.size() + "\n" +
                "Cuentas: [" + ctasStr + "]"
            );
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage() + "\n" + e.toString());
        }
    }
}
