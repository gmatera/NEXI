package com.cbi.ccr.csw.domain.mss;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MSSRecvEumStringConverter implements AttributeConverter<MSSRecvStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(MSSRecvStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatus();
	}

	@Override
	public MSSRecvStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		return Stream.of(MSSRecvStatus.values())
				.filter(s -> s.getStatus().equals(statusBA))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
