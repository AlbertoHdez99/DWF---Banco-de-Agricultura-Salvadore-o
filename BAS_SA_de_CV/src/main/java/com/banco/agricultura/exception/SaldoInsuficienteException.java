package com.banco.agricultura.exception;

// ── Saldo insuficiente para retiro o transferencia ────────────────────────────
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) { super(mensaje); }
}