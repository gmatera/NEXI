package com.cbi.ccr.csw.domain.mss;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MSSSendEumStringConverter implements AttributeConverter<MSSSendStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(MSSSendStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatus();
	}

	@Override
	public MSSSendStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		
		
		return Stream.of(MSSSendStatus.values())
				.filter(s -> s.getStatus().equals(statusBA))
				.findFirst()
				.orElse(MSSSendStatus.NEW_TRAFFIC);
//				.orElseThrow(IllegalArgumentException::new);
	}

}
