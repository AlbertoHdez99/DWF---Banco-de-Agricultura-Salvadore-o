package com.banco.agricultura.exception;

// ── La cuota mensual calculada supera el 30% del salario del cliente ──────────
public class CuotaExcedeSalarioException extends RuntimeException {
    public CuotaExcedeSalarioException(String mensaje) { super(mensaje); }
}