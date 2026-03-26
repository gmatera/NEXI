package com.cbi.ccr.csw.domain.csw;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {
    private final String GROUP_DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> stringList) {
        if (stringList == null) {
            return new String();
        }
        return String.join(GROUP_DELIMITER, stringList);
    }

    @Override
    public Set<String> convertToEntityAttribute(String string) {
        Set<String> resultingSet = new HashSet<>();
        StringTokenizer st = new StringTokenizer(string, GROUP_DELIMITER);
        while (st.hasMoreTokens())
            resultingSet.add(st.nextToken());
        return resultingSet;
    }
}
