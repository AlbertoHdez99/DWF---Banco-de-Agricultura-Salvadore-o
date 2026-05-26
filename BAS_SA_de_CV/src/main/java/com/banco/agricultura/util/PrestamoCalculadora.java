package com.banco.agricultura.util;

import com.banco.agricultura.exception.CuotaExcedeSalarioException;
import com.banco.agricultura.exception.MontoPrestamoExcedidoException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Utilidad para todos los cálculos relacionados con préstamos.
 * Centraliza las reglas de negocio según el caso de estudio.
 *
 * Tabla de límites según salario:
 *   < $365          → máx $10,000  al 3%
 *   $365 – $599.99  → máx $25,000  al 3%
 *   $600 – $899.99  → máx $35,000  al 4%
 *   ≥ $1,000        → máx $50,000  al 5%
 */
public class PrestamoCalculadora {

    private static final BigDecimal SALARIO_RANGO_1 = new BigDecimal("365");
    private static final BigDecimal SALARIO_RANGO_2 = new BigDecimal("600");
    private static final BigDecimal SALARIO_RANGO_3 = new BigDecimal("900");
    private static final BigDecimal SALARIO_RANGO_4 = new BigDecimal("1000");

    private static final BigDecimal MONTO_MAX_RANGO_1 = new BigDecimal("10000");
    private static final BigDecimal MONTO_MAX_RANGO_2 = new BigDecimal("25000");
    private static final BigDecimal MONTO_MAX_RANGO_3 = new BigDecimal("35000");
    private static final BigDecimal MONTO_MAX_RANGO_4 = new BigDecimal("50000");

    private static final BigDecimal TASA_3_PORCIENTO  = new BigDecimal("0.03");
    private static final BigDecimal TASA_4_PORCIENTO  = new BigDecimal("0.04");
    private static final BigDecimal TASA_5_PORCIENTO  = new BigDecimal("0.05");

    // Cuota máxima = 30% del salario
    private static final BigDecimal PORCENTAJE_CUOTA_MAX = new BigDecimal("0.30");

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Determina la tasa de interés anual según el salario del cliente.
     */
    public static BigDecimal obtenerTasaInteres(BigDecimal salario) {
        if (salario.compareTo(SALARIO_RANGO_1) < 0) {
            return TASA_3_PORCIENTO;
        } else if (salario.compareTo(SALARIO_RANGO_2) < 0) {
            return TASA_3_PORCIENTO;
        } else if (salario.compareTo(SALARIO_RANGO_3) < 0) {
            return TASA_4_PORCIENTO;
        } else {
            return TASA_5_PORCIENTO;
        }
    }

    /**
     * Determina el monto máximo que puede solicitar el cliente según su salario.
     */
    public static BigDecimal obtenerMontoMaximo(BigDecimal salario) {
        if (salario.compareTo(SALARIO_RANGO_1) < 0) {
            return MONTO_MAX_RANGO_1;
        } else if (salario.compareTo(SALARIO_RANGO_2) < 0) {
            return MONTO_MAX_RANGO_2;
        } else if (salario.compareTo(SALARIO_RANGO_3) < 0) {
            return MONTO_MAX_RANGO_3;
        } else {
            return MONTO_MAX_RANGO_4;
        }
    }

    /**
     * Calcula la cuota mensual usando la fórmula de amortización francesa:
     *   cuota = (monto × r) / (1 − (1 + r)^−n)
     * donde r = tasa_anual / 12  y  n = meses totales.
     *
     * @param monto       Monto del préstamo
     * @param tasaAnual   Tasa de interés anual (ej: 0.03 para 3%)
     * @param meses       Plazo en meses
     * @return Cuota mensual redondeada a 2 decimales
     */
    public static BigDecimal calcularCuotaMensual(BigDecimal monto,
                                                  BigDecimal tasaAnual,
                                                  int meses) {
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);

        // r = tasa mensual
        BigDecimal r = tasaAnual.divide(BigDecimal.valueOf(12), mc);

        // (1 + r)^n usando double para el exponente — precisión suficiente para montos bancarios
        double base    = 1.0 + r.doubleValue();
        double factor  = Math.pow(base, meses);

        // cuota = (monto × r) / (1 − (1+r)^−n) = (monto × r × factor) / (factor − 1)
        BigDecimal factorBD = BigDecimal.valueOf(factor);
        BigDecimal numerador   = monto.multiply(r, mc).multiply(factorBD, mc);
        BigDecimal denominador = factorBD.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula los años de plazo mínimos necesarios para que la cuota mensual
     * no supere el 30% del salario del cliente.
     * Busca desde 1 año hasta 30 años. Si no encuentra un plazo viable, lanza excepción.
     *
     * @param monto     Monto del préstamo
     * @param tasaAnual Tasa anual
     * @param salario   Salario mensual del cliente
     * @return Años de plazo mínimos donde la cuota ≤ 30% del salario
     * @throws CuotaExcedeSalarioException si ningún plazo de hasta 30 años lo permite
     */
    public static int calcularAniosPlazo(BigDecimal monto,
                                         BigDecimal tasaAnual,
                                         BigDecimal salario) {
        BigDecimal cuotaMaxima = salario.multiply(PORCENTAJE_CUOTA_MAX)
                .setScale(2, RoundingMode.HALF_UP);

        for (int anios = 1; anios <= 30; anios++) {
            int meses = anios * 12;
            BigDecimal cuota = calcularCuotaMensual(monto, tasaAnual, meses);
            if (cuota.compareTo(cuotaMaxima) <= 0) {
                return anios;
            }
        }

        throw new CuotaExcedeSalarioException(
                "No es posible estructurar el préstamo de $" + monto +
                        " con el salario de $" + salario +
                        ". La cuota máxima permitida es $" + cuotaMaxima +
                        " (30% del salario) y ningún plazo de hasta 30 años la cumple."
        );
    }

    /**
     * Valida que el monto solicitado no exceda el límite permitido por salario.
     * @throws MontoPrestamoExcedidoException si el monto supera el máximo
     */
    public static void validarMonto(BigDecimal montoSolicitado, BigDecimal salario) {
        BigDecimal maximo = obtenerMontoMaximo(salario);
        if (montoSolicitado.compareTo(maximo) > 0) {
            throw new MontoPrestamoExcedidoException(
                    "El monto solicitado ($" + montoSolicitado +
                            ") supera el máximo permitido ($" + maximo +
                            ") para un salario de $" + salario + "."
            );
        }
    }

    /**
     * Cuota máxima permitida = 30% del salario.
     */
    public static BigDecimal calcularCuotaMaximaPermitida(BigDecimal salario) {
        return salario.multiply(PORCENTAJE_CUOTA_MAX).setScale(2, RoundingMode.HALF_UP);
    }
}