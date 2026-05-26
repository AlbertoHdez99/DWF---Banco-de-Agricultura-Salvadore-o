package com.banco.agricultura.exception;

// ── El monto solicitado supera el límite permitido según salario ──────────────
public class MontoPrestamoExcedidoException extends RuntimeException {
    public MontoPrestamoExcedidoException(String mensaje) { super(mensaje); }
}