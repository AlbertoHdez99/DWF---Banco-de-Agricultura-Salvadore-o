package com.banco.agricultura.util;

import java.util.Random;

/**
 * Genera números de cuenta bancaria únicos.
 * Formato: "BA" + 10 dígitos aleatorios  →  ej: "BA4823019562"
 * La unicidad final se verifica en CuentaService contra la BD.
 */
public class NumeroCuentaGenerator {

    private static final String PREFIJO = "BA";
    private static final int    DIGITOS = 10;
    private static final Random RANDOM  = new Random();

    private NumeroCuentaGenerator() {}

    /**
     * Genera un número de cuenta candidato.
     * Siempre verificar unicidad con CuentaDAO.existsByNumeroCuenta() antes de persistir.
     */
    public static String generar() {
        StringBuilder sb = new StringBuilder(PREFIJO);
        for (int i = 0; i < DIGITOS; i++) {
            sb.append(RANDOM.nextInt(10));  // 0–9 por dígito
        }
        return sb.toString();
    }
}