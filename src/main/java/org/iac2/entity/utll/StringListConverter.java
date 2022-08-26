package org.iac2.entity.utll;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(List<String> longs) {
        // convert to string and join with ;
        return String.join(DELIMITER, longs);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return List.of(s.split(DELIMITER));
    }
}
