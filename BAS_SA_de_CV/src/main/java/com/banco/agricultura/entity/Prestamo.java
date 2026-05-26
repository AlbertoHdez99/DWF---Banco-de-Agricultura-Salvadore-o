package com.banco.agricultura.entity;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@EqualsAndHashCode(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@Entity
@Table(name = "prestamos")
public class Prestamo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Integer idPrestamo;

    @Column(name = "monto_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "interes", nullable = false, precision = 5, scale = 2)
    private BigDecimal interes;

    @Column(name = "anios_plazo", nullable = false)
    private Integer aniosPlazo;

    @Column(name = "cuota_mensual", nullable = false, precision = 15, scale = 2)
    private BigDecimal cuotaMensual;

    @Column(name = "fecha_solicitud", nullable = false)
    @JdbcTypeCode(Types.DATE)
    private LocalDate fechaSolicitud;

    @Column(name = "estado_prestamo", nullable = false, columnDefinition = "ENUM('En espera', 'Aprobado', 'Rechazado')")
    private EstadoPrestamo estadoPrestamo = EstadoPrestamo.En_espera;

    // Cliente que solicita el préstamo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario cliente;

    // Cajero que apertura el préstamo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cajero", nullable = false)
    private Usuario cajero;

    // Gerente que aprueba o rechaza — null hasta que actúa
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gerente_aprobador")
    private Usuario gerenteAprobador;

    public enum EstadoPrestamo {
        En_espera, Aprobado, Rechazado
    }
}