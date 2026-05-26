package com.banco.agricultura.entity.converter;

import com.banco.agricultura.entity.AccionPersonal.EstadoAccion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoAccionConverter implements AttributeConverter<EstadoAccion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoAccion attribute) {
        if (attribute == null) {
            return null;
        }
        switch (attribute) {
            case En_espera:
                return "En espera";
            case Aprobada:
                return "Aprobada";
            case Rechazada:
                return "Rechazada";
            default:
                throw new IllegalArgumentException("EstadoAccion desconocido: " + attribute);
        }
    }

    @Override
    public EstadoAccion convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        switch (dbData) {
            case "En espera":
                return EstadoAccion.En_espera;
            case "Aprobada":
                return EstadoAccion.Aprobada;
            case "Rechazada":
                return EstadoAccion.Rechazada;
            default:
                throw new IllegalArgumentException("Valor desconocido: " + dbData);
        }
    }
}
