package com.banco.agricultura.entity;
import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@EqualsAndHashCode(exclude = {"cuentaOrigen", "cuentaDestino", "usuarioEjecutor", "dependiente"})
@Entity
@Table(name = "empleados")
public class Empleado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "estado_contratacion", nullable = false, columnDefinition = "ENUM('Activo', 'Inactivo', 'En espera')")
    private EstadoContratacion estadoContratacion = EstadoContratacion.En_espera;

    public enum EstadoContratacion {
        Activo, Inactivo, En_espera
    }
}