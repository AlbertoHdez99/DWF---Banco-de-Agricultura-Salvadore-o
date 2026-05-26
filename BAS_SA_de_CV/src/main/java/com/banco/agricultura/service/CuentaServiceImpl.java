package com.banco.agricultura.service;

import com.banco.agricultura.dao.CuentaDAO;
import com.banco.agricultura.dao.UsuarioDAO;
import com.banco.agricultura.entity.Cuenta;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.exception.LimiteCuentasAlcanzadoException;
import com.banco.agricultura.util.NumeroCuentaGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private static final int MAX_CUENTAS = 3;

    @Autowired
    private CuentaDAO cuentaDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    // ─── Crear cuenta — valida máximo 3, genera número único ─────────────────
    @Override
    public Cuenta crearCuenta(Integer idUsuario) {
        long total = usuarioDAO.countCuentasByUsuario(idUsuario);
        if (total >= MAX_CUENTAS) {
            throw new LimiteCuentasAlcanzadoException(
                    "El cliente ya tiene " + MAX_CUENTAS + " cuentas activas. " +
                            "No se pueden crear más cuentas."
            );
        }

        Usuario usuario = usuarioDAO.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        // Generar número único — reintentar si ya existe
        String numeroCuenta;
        int intentos = 0;
        do {
            numeroCuenta = NumeroCuentaGenerator.generar();
            intentos++;
            if (intentos > 10) {
                throw new RuntimeException("No se pudo generar un número de cuenta único.");
            }
        } while (cuentaDAO.existsByNumeroCuenta(numeroCuenta));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setUsuario(usuario);

        cuentaDAO.save(cuenta);
        return cuenta;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarPorUsuario(Integer idUsuario) {
        return cuentaDAO.findByUsuario(idUsuario);
    }

    // ─── Usado por cajero y dependiente: buscar cuentas por DUI ──────────────
    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarPorDui(String dui) {
        Usuario usuario = usuarioDAO.findByDui(dui)
                .orElseThrow(() -> new RuntimeException("No se encontró cliente con DUI: " + dui));
        return cuentaDAO.findByUsuario(usuario.getIdUsuario());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> buscarPorNumero(String numeroCuenta) {
        return cuentaDAO.findByNumeroCuenta(numeroCuenta);
    }
}