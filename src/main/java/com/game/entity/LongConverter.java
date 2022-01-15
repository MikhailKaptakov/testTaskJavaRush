package com.game.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;

@Converter(autoApply = true)
public class LongConverter implements AttributeConverter<Long, Date> {
    @Override
    public Date convertToDatabaseColumn(Long milis) {
        return new java.sql.Date(milis);
    }

    @Override
    public Long convertToEntityAttribute(Date date) {
        return date.getTime();
    }
}