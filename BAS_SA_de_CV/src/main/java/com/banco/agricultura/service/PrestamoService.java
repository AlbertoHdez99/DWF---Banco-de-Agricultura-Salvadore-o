package com.banco.agricultura.service;

import com.banco.agricultura.entity.Prestamo;
import java.math.BigDecimal;
import java.util.List;

public interface PrestamoService {
    Prestamo abrirPrestamo(Integer idCliente, Integer idCajero, BigDecimal montoSolicitado);
    Prestamo aprobar(Integer idPrestamo, Integer idGerenteSucursal);
    Prestamo rechazar(Integer idPrestamo, Integer idGerenteSucursal);
    List<Prestamo> listarPorCliente(Integer idCliente);
    List<Prestamo> listarEnEspera();
    List<Prestamo> listarPorCajero(Integer idCajero);

    // Para mostrar en el formulario del cajero antes de confirmar
    BigDecimal calcularCuotaPrevia(Integer idCliente, BigDecimal monto);
    int calcularAniosPrevio(Integer idCliente, BigDecimal monto);
}