package com.cbi.ccr.csw.domain.fms;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FMSRecvStatusConverter implements AttributeConverter<FMSRecvStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(FMSRecvStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatusBA();
	}

	@Override
	public FMSRecvStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		return Stream.of(FMSRecvStatus.values())
				.filter(s -> s.getStatusBA().equals(statusBA))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
