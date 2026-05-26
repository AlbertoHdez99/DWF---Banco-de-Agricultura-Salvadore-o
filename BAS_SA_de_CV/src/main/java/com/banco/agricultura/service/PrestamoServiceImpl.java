package com.banco.agricultura.service;

import com.banco.agricultura.dao.PrestamoDAO;
import com.banco.agricultura.dao.UsuarioDAO;
import com.banco.agricultura.entity.Prestamo;
import com.banco.agricultura.entity.Usuario;
import com.banco.agricultura.util.PrestamoCalculadora;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoDAO prestamoDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @PersistenceContext
    private EntityManager entityManager;

    // ─── Apertura de préstamo — toda la lógica de cálculo y validación ────────
    @Override
    public Prestamo abrirPrestamo(Integer idCliente, Integer idCajero,
                                  BigDecimal montoSolicitado) {
        Usuario cliente = obtenerUsuario(idCliente);
        Usuario cajero  = obtenerUsuario(idCajero);

        // El cliente debe tener salario registrado
        if (cliente.getSalario() == null || cliente.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "El cliente no tiene salario registrado. " +
                            "No puede acceder a préstamos."
            );
        }

        BigDecimal salario    = cliente.getSalario();
        BigDecimal tasaAnual  = PrestamoCalculadora.obtenerTasaInteres(salario);

        // Valida que el monto no supere el límite por salario
        PrestamoCalculadora.validarMonto(montoSolicitado, salario);

        // Calcula años mínimos para que cuota ≤ 30% del salario
        int anios = PrestamoCalculadora.calcularAniosPlazo(
                montoSolicitado, tasaAnual, salario);

        BigDecimal cuota = PrestamoCalculadora.calcularCuotaMensual(
                montoSolicitado, tasaAnual, anios * 12);

        Prestamo prestamo = new Prestamo();
        prestamo.setMontoSolicitado(montoSolicitado);
        prestamo.setInteres(tasaAnual.multiply(new BigDecimal("100"))); // guardamos como %, ej: 3.00
        prestamo.setAniosPlazo(anios);
        prestamo.setCuotaMensual(cuota);
        prestamo.setFechaSolicitud(LocalDate.now());
        prestamo.setEstadoPrestamo(Prestamo.EstadoPrestamo.En_espera);
        prestamo.setCliente(cliente);
        prestamo.setCajero(cajero);
        prestamo.setGerenteAprobador(null);

        prestamoDAO.save(prestamo);
        return prestamo;
    }

    // ─── Aprobar préstamo (gerente de sucursal) ───────────────────────────────
    @Override
    public Prestamo aprobar(Integer idPrestamo, Integer idGerente) {
        Prestamo prestamo = obtenerPrestamo(idPrestamo);
        validarEnEspera(prestamo);

        Usuario gerente = obtenerUsuario(idGerente);
        prestamo.setEstadoPrestamo(Prestamo.EstadoPrestamo.Aprobado);
        prestamo.setGerenteAprobador(gerente);
        prestamoDAO.update(prestamo);
        return prestamo;
    }

    // ─── Rechazar préstamo (gerente de sucursal) ──────────────────────────────
    @Override
    public Prestamo rechazar(Integer idPrestamo, Integer idGerente) {
        Prestamo prestamo = obtenerPrestamo(idPrestamo);
        validarEnEspera(prestamo);

        Usuario gerente = obtenerUsuario(idGerente);
        prestamo.setEstadoPrestamo(Prestamo.EstadoPrestamo.Rechazado);
        prestamo.setGerenteAprobador(gerente);
        prestamoDAO.update(prestamo);
        return prestamo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorCliente(Integer idCliente) {
        return prestamoDAO.findByCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> listarEnEspera() {
        return prestamoDAO.findByEstado(Prestamo.EstadoPrestamo.En_espera);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorCajero(Integer idCajero) {
        return prestamoDAO.findByCajero(idCajero);
    }

    // ─── Vista previa para el formulario del cajero ───────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularCuotaPrevia(Integer idCliente, BigDecimal monto) {
        Usuario cliente  = obtenerUsuario(idCliente);
        if (cliente.getSalario() == null || cliente.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El cliente no tiene un salario registrado para aplicar a préstamos.");
        }
        PrestamoCalculadora.validarMonto(monto, cliente.getSalario());
        BigDecimal tasa  = PrestamoCalculadora.obtenerTasaInteres(cliente.getSalario());
        int anios        = PrestamoCalculadora.calcularAniosPlazo(monto, tasa, cliente.getSalario());
        return PrestamoCalculadora.calcularCuotaMensual(monto, tasa, anios * 12);
    }

    @Override
    @Transactional(readOnly = true)
    public int calcularAniosPrevio(Integer idCliente, BigDecimal monto) {
        Usuario cliente = obtenerUsuario(idCliente);
        if (cliente.getSalario() == null || cliente.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El cliente no tiene un salario registrado para aplicar a préstamos.");
        }
        PrestamoCalculadora.validarMonto(monto, cliente.getSalario());
        BigDecimal tasa = PrestamoCalculadora.obtenerTasaInteres(cliente.getSalario());
        return PrestamoCalculadora.calcularAniosPlazo(monto, tasa, cliente.getSalario());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Usuario obtenerUsuario(Integer id) {
        return usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    private Prestamo obtenerPrestamo(Integer id) {
        return prestamoDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado: " + id));
    }

    private void validarEnEspera(Prestamo prestamo) {
        if (prestamo.getEstadoPrestamo() != Prestamo.EstadoPrestamo.En_espera) {
            throw new RuntimeException(
                    "El préstamo ya fue procesado. Estado actual: " +
                            prestamo.getEstadoPrestamo()
            );
        }
    }
}