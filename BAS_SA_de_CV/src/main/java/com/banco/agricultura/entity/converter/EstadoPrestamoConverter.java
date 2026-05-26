package com.banco.agricultura.entity.converter;

import com.banco.agricultura.entity.Prestamo.EstadoPrestamo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoPrestamoConverter implements AttributeConverter<EstadoPrestamo, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPrestamo attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case En_espera:
                return "En espera";
            case Aprobado:
                return "Aprobado";
            case Rechazado:
                return "Rechazado";
            default:
                throw new IllegalArgumentException("EstadoPrestamo desconocido: " + attribute);
        }
    }

    @Override
    public EstadoPrestamo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case "En espera":
                return EstadoPrestamo.En_espera;
            case "Aprobado":
                return EstadoPrestamo.Aprobado;
            case "Rechazado":
                return EstadoPrestamo.Rechazado;
            default:
                throw new IllegalArgumentException("Valor de base de datos desconocido para EstadoPrestamo: " + dbData);
        }
    }
}
