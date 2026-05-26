package com.banco.agricultura.exception;

// ── El cliente ya tiene 3 cuentas activas ─────────────────────────────────────
public class LimiteCuentasAlcanzadoException extends RuntimeException {
    public LimiteCuentasAlcanzadoException(String mensaje) { super(mensaje); }
}