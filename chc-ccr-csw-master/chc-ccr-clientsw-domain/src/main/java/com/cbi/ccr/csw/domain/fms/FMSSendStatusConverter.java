package com.cbi.ccr.csw.domain.fms;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FMSSendStatusConverter implements AttributeConverter<FMSSendStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(FMSSendStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatusBA();
	}

	@Override
	public FMSSendStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		return Stream.of(FMSSendStatus.values())
				.filter(s -> s.getStatusBA().equals(statusBA))
				.findFirst()
				.orElse(FMSSendStatus.SUBMITTED);
//				.orElseThrow(IllegalArgumentException::new);
	}

}
