package com.cbi.ccr.csw.domain.fts;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FTSSendStatusConverter implements AttributeConverter<FTSSendStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(FTSSendStatus status) {
		if(status == null) {
			return null;
		}
		return status.getStatus();
	}

	@Override
	public FTSSendStatus convertToEntityAttribute(String statusBA) {
		if(statusBA == null) {
			return null;
		}
		return Stream.of(FTSSendStatus.values())
				.filter(s -> s.getStatus().equals(statusBA))
				.findFirst()
				.orElse(FTSSendStatus.FILE_TO_BE_PROCESSED);
//				.orElseThrow(IllegalArgumentException::new);
	}

}
