package com.banco.agricultura.exception;

// ── El DUI no coincide con el titular de la cuenta ───────────────────────────
public class CuentaNoCorrespondeException extends RuntimeException {
    public CuentaNoCorrespondeException(String mensaje) { super(mensaje); }
}