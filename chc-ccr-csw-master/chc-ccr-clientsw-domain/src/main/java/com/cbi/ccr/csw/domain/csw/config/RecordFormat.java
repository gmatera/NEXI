package com.cbi.ccr.csw.domain.csw.config;

import java.util.stream.Stream;

public enum RecordFormat {
	
	O("0", 0),
	FIXED("F", 1),
	VARIABLE("V", 2);
	
	private String labelDB;
	private Integer labelMQ;
	
	RecordFormat(String labelDB, Integer labelMQ) {
		this.labelDB = labelDB;
		this.labelMQ=labelMQ;
	}

	public String getLabelDB() {
		return labelDB;
	}
	
	public Integer getLabelMQ() {
		return labelMQ;
	}
	
	public static RecordFormat getEnumByValueDB(String label) {
		if(label == null) {
			return null;
		}
		return Stream.of(RecordFormat.values())
				.filter(s -> s.getLabelDB().equals(label))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static RecordFormat getEnumByValueMQ(Integer label) {
		if(label == null) {
			return null;
		}
		return Stream.of(RecordFormat.values())
				.filter(s -> s.getLabelMQ().equals(label))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
