package com.banco.agricultura.entity.converter;

import com.banco.agricultura.entity.Empleado.EstadoContratacion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoContratacionConverter implements AttributeConverter<EstadoContratacion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoContratacion attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case En_espera:
                return "En espera";
            case Activo:
                return "Activo";
            case Inactivo:
                return "Inactivo";
            default:
                throw new IllegalArgumentException("EstadoContratacion desconocido: " + attribute);
        }
    }

    @Override
    public EstadoContratacion convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case "En espera":
                return EstadoContratacion.En_espera;
            case "Activo":
                return EstadoContratacion.Activo;
            case "Inactivo":
                return EstadoContratacion.Inactivo;
            default:
                throw new IllegalArgumentException("Valor desconocido: " + dbData);
        }
    }
}
