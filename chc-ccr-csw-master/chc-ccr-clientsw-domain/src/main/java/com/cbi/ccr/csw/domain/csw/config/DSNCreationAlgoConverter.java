package com.cbi.ccr.csw.domain.csw.config;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DSNCreationAlgoConverter implements AttributeConverter<DSNCreationAlgo, Integer> {
	
	@Override
	public Integer convertToDatabaseColumn(DSNCreationAlgo dsn) {
		if(dsn == null) {
			return null;
		}
		return dsn.getValue();
	}
	
	public DSNCreationAlgo convertToEntityAttribute(Integer value) {
		if(value == null) {
			return null;
		}
		return Stream.of(DSNCreationAlgo.values())
				.filter(s -> s.getValue() == value)
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
