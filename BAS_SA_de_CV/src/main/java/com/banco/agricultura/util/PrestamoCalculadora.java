package com.banco.agricultura.util;

import com.banco.agricultura.exception.CuotaExcedeSalarioException;
import com.banco.agricultura.exception.MontoPrestamoExcedidoException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Utilidad para realizar cálculos de préstamos.
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

    private static final BigDecimal PORCENTAJE_CUOTA_MAX = new BigDecimal("0.30");

    /**
     * Obtiene la tasa de interés anual según el salario.
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
     * Obtiene el monto máximo de préstamo según el salario.
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
     * Calcula la cuota mensual mediante amortización francesa.
     */
    public static BigDecimal calcularCuotaMensual(BigDecimal monto,
                                                  BigDecimal tasaAnual,
                                                  int meses) {
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);

        BigDecimal r = tasaAnual.divide(BigDecimal.valueOf(12), mc);

        double base    = 1.0 + r.doubleValue();
        double factor  = Math.pow(base, meses);

        BigDecimal factorBD = BigDecimal.valueOf(factor);
        BigDecimal numerador   = monto.multiply(r, mc).multiply(factorBD, mc);
        BigDecimal denominador = factorBD.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula los años mínimos de plazo para no superar la cuota máxima.
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
     * Valida que el monto solicitado no supere el límite permitido por el salario.
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
     * Calcula la cuota máxima permitida (30% del salario).
     */
    public static BigDecimal calcularCuotaMaximaPermitida(BigDecimal salario) {
        return salario.multiply(PORCENTAJE_CUOTA_MAX).setScale(2, RoundingMode.HALF_UP);
    }
}