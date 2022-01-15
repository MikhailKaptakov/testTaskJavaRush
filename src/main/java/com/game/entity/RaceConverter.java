package com.game.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter(autoApply = true)
public class RaceConverter implements AttributeConverter<Race, String> {
    @Override
    public String convertToDatabaseColumn(Race race) {
        return race.name();
    }

    @Override
    public Race convertToEntityAttribute(String code) {
        for (Race race : Race.values()) {
            if (race.name().equals(code)) {
                return race;
            }
        }
        throw new IllegalArgumentException();
    }
}

