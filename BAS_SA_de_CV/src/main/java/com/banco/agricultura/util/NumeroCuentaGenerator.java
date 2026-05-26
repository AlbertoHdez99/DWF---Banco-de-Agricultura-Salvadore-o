package com.banco.agricultura.util;

import java.util.Random;

/**
 * Genera números de cuenta bancaria con prefijo BA y 10 dígitos.
 */
public class NumeroCuentaGenerator {

    private static final String PREFIJO = "BA";
    private static final int    DIGITOS = 10;
    private static final Random RANDOM  = new Random();

    private NumeroCuentaGenerator() {}

    /**
     * Genera un número de cuenta aleatorio.
     */
    public static String generar() {
        StringBuilder sb = new StringBuilder(PREFIJO);
        for (int i = 0; i < DIGITOS; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}