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
@Table(name = "comisiones")
public class Comision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comision")
    private Integer idComision;

    // Relación 1-1 con el movimiento que generó la comisión
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_movimiento", nullable = false, unique = true)
    private Movimiento movimiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dependiente", nullable = false)
    private Dependiente dependiente;

    // 5% del monto del movimiento — se calcula en el servicio
    @Column(name = "monto_comision", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoComision;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}