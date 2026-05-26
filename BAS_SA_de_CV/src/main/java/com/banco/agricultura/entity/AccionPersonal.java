package com.banco.agricultura.entity;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@EqualsAndHashCode(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@Entity
@Table(name = "acciones_personal")
public class AccionPersonal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_accion")
    private Integer idAccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false)
    private TipoAccion tipoAccion;

    @Column(name = "estado_accion", nullable = false, columnDefinition = "ENUM('En espera', 'Aprobada', 'Rechazada')")
    private EstadoAccion estadoAccion = EstadoAccion.En_espera;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }

    // Gerente de sucursal que generó la acción
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gerente_sucursal", nullable = false)
    private Usuario gerenteSucursal;

    // Gerente general que aprueba o rechaza — null hasta que actúa
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gerente_general")
    private Usuario gerenteGeneral;

    public enum TipoAccion {
        Contratación, Baja
    }

    public enum EstadoAccion {
        En_espera, Aprobada, Rechazada
    }
}
