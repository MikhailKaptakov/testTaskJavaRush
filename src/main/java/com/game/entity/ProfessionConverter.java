package com.game.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProfessionConverter implements AttributeConverter<Profession, String> {
    @Override
    public String convertToDatabaseColumn(Profession profession) {
        return profession.name();
    }

    @Override
    public Profession convertToEntityAttribute(String code) {
        for (Profession profession : Profession.values()) {
            if (profession.name().equals(code)) {
                return profession;
            }
        }
        throw new IllegalArgumentException();
    }
}