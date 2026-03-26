package com.cbi.ccr.csw.domain.csw.config;

import java.util.stream.Stream;

public enum CodePage {
	
	O("00", 0),
	BINARY("01", 1),
	ASCII("02", 2),
	EBCDIC("03", 3)
;
	private String valueDb;
	private Integer valueMq;

	
	CodePage(String valueDb, Integer valueMq ) {
		this.valueDb = valueDb;
		this.valueMq = valueMq;
	}
	
	public String getValueDB() { 
        return valueDb;
    }
	
	public Integer getValueMq() { 
        return valueMq;
    }
	
	public static CodePage getEnumByValueDB(String value) {
		if(value == null) {
			return null;
		}
		return Stream.of(CodePage.values())
				.filter(s -> s.getValueDB().equals(value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static CodePage getEnumByValueMQ(Integer value) {
		if(value == null) {
			return null;
		}
		return Stream.of(CodePage.values())
				.filter(s -> s.getValueMq().equals(value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
