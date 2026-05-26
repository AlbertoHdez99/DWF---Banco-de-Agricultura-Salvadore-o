package com.banco.agricultura.service;

import com.banco.agricultura.dao.CuentaDAO;
import com.banco.agricultura.dao.MovimientoDAO;
import com.banco.agricultura.entity.*;
import com.banco.agricultura.exception.CuentaNoCorrespondeException;
import com.banco.agricultura.exception.SaldoInsuficienteException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired
    private MovimientoDAO movimientoDAO;

    @Autowired
    private CuentaDAO cuentaDAO;

    @PersistenceContext
    private EntityManager entityManager;

    private static final BigDecimal COMISION_PORCENTAJE = new BigDecimal("0.05");

    // ─── Depósito ─────────────────────────────────────────────────────────────
    @Override
    public Movimiento depositar(String numeroCuenta, BigDecimal monto,
                                Integer idUsuarioEjecutor, Integer idDependiente) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        Usuario ejecutor = entityManager.find(Usuario.class, idUsuarioEjecutor);

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaDAO.update(cuenta);

        Movimiento mov = crearMovimiento(
                Movimiento.TipoMovimiento.Depósito,
                monto, cuenta, null, ejecutor
        );

        if (idDependiente != null) {
            Dependiente dep = entityManager.find(Dependiente.class, idDependiente);
            mov.setDependiente(dep);
            movimientoDAO.save(mov);
            registrarComision(mov, dep, monto);
        } else {
            movimientoDAO.save(mov);
        }

        return mov;
    }

    // ─── Retiro ───────────────────────────────────────────────────────────────
    @Override
    public Movimiento retirar(String numeroCuenta, BigDecimal monto,
                              Integer idUsuarioEjecutor, Integer idDependiente) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente. Saldo disponible: $" + cuenta.getSaldo() +
                            ", monto solicitado: $" + monto
            );
        }

        Usuario ejecutor = entityManager.find(Usuario.class, idUsuarioEjecutor);
        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaDAO.update(cuenta);

        Movimiento mov = crearMovimiento(
                Movimiento.TipoMovimiento.Retiro,
                monto, cuenta, null, ejecutor
        );

        if (idDependiente != null) {
            Dependiente dep = entityManager.find(Dependiente.class, idDependiente);
            mov.setDependiente(dep);
            movimientoDAO.save(mov);
            registrarComision(mov, dep, monto);
        } else {
            movimientoDAO.save(mov);
        }

        return mov;
    }

    // ─── Transferencia ────────────────────────────────────────────────────────
    @Override
    public Movimiento transferir(String numeroCuentaOrigen, String numeroCuentaDestino,
                                 BigDecimal monto, Integer idUsuarioEjecutor) {
        Cuenta origen  = obtenerCuenta(numeroCuentaOrigen);
        Cuenta destino = obtenerCuenta(numeroCuentaDestino);

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente para la transferencia. " +
                            "Saldo disponible: $" + origen.getSaldo()
            );
        }

        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));
        cuentaDAO.update(origen);
        cuentaDAO.update(destino);

        Usuario ejecutor = entityManager.find(Usuario.class, idUsuarioEjecutor);

        Movimiento mov = crearMovimiento(
                Movimiento.TipoMovimiento.Transferencia,
                monto, origen, destino, ejecutor
        );
        movimientoDAO.save(mov);
        return mov;
    }

    // ─── Depósito con validación de DUI (cajero) ──────────────────────────────
    @Override
    public Movimiento depositarConValidacionDui(String dui, String numeroCuenta,
                                                BigDecimal monto, Integer idCajero) {
        validarDuiCuenta(dui, numeroCuenta);
        return depositar(numeroCuenta, monto, idCajero, null);
    }

    // ─── Retiro con validación de DUI (cajero) ────────────────────────────────
    @Override
    public Movimiento retirarConValidacionDui(String dui, String numeroCuenta,
                                              BigDecimal monto, Integer idCajero) {
        validarDuiCuenta(dui, numeroCuenta);
        return retirar(numeroCuenta, monto, idCajero, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorCuenta(Integer idCuenta) {
        return movimientoDAO.findByCuenta(idCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarTodos() {
        return movimientoDAO.findAll();
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private Cuenta obtenerCuenta(String numeroCuenta) {
        return cuentaDAO.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new RuntimeException(
                        "Cuenta no encontrada: " + numeroCuenta));
    }

    private void validarDuiCuenta(String dui, String numeroCuenta) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        if (!cuenta.getUsuario().getDui().equals(dui)) {
            throw new CuentaNoCorrespondeException(
                    "El DUI " + dui + " no corresponde al titular de la cuenta " + numeroCuenta
            );
        }
    }

    private Movimiento crearMovimiento(Movimiento.TipoMovimiento tipo,
                                       BigDecimal monto,
                                       Cuenta origen, Cuenta destino,
                                       Usuario ejecutor) {
        Movimiento mov = new Movimiento();
        mov.setTipoMovimiento(tipo);
        mov.setMonto(monto);
        mov.setFechaMovimiento(LocalDateTime.now());
        mov.setCuentaOrigen(origen);
        mov.setCuentaDestino(destino);
        mov.setUsuarioEjecutor(ejecutor);
        return mov;
    }

    // ─── Registrar comisión del 5% para el dependiente ───────────────────────
    private void registrarComision(Movimiento movimiento,
                                   Dependiente dependiente,
                                   BigDecimal monto) {
        Comision comision = new Comision();
        comision.setMovimiento(movimiento);
        comision.setDependiente(dependiente);
        comision.setMontoComision(
                monto.multiply(COMISION_PORCENTAJE)
                        .setScale(2, java.math.RoundingMode.HALF_UP)
        );
        comision.setFechaRegistro(LocalDateTime.now());
        entityManager.persist(comision);
    }
}