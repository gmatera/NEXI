package com.cbi.ccr.csw.domain.fts;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FTSRecvStatusConverter implements AttributeConverter<FTSRecvStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(FTSRecvStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatus();
	}

	@Override
	public FTSRecvStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		return Stream.of(FTSRecvStatus.values())
				.filter(s -> s.getStatus().equals(statusBA))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
