package com.banco.agricultura.entity;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@EqualsAndHashCode(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@Entity
@Table(name = "movimientos")
public class Movimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    @PrePersist
    protected void onCreate() {
        this.fechaMovimiento = LocalDateTime.now();
    }

    // Cuenta desde donde sale el dinero (siempre requerida)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cuenta_origen", nullable = false)
    private Cuenta cuentaOrigen;

    // Cuenta destino — solo en transferencias
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cuenta_destino")
    private Cuenta cuentaDestino;

    // Usuario que ejecuta la operación (cliente, cajero o dependiente)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_ejecutor", nullable = false)
    private Usuario usuarioEjecutor;

    // Solo se llena si el movimiento fue realizado por un dependiente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dependiente")
    private Dependiente dependiente;

    public enum TipoMovimiento {
        Depósito, Retiro, Transferencia
    }
}