package com.cbi.ccr.csw.domain.csw.config;

import java.util.stream.Stream;

/**
 * CRLF and LF are not allowed if sndCodePage= BINARY.
 * @author dave
 *
 */
public enum LineSeparator {

	
	O("0",(char) 0X00, (char) 0X00, "99", 0),
	/*
	 * WINDOWS
	 */
	CRLF_0X0D0A("CRLF", (char) 0X0D, (char) 0X0A, "2", 1),
    @Deprecated
	CRLF_0X0D15("CRLF", (char) 0X0D, (char) 0X15, "4", 5),
	CRLF_0X0D25("CRLF", (char) 0X0D, (char) 0X25, "6", 7),
	/*
	 * UNIX
	 */
	LF_0X0A("LF",(char) 0X0A, '0', "1", 3),
    @Deprecated
	LF_0X15("LF", (char) 0X15, '0', "3", 4),
    @Deprecated
	LF_0X25("LF",(char) 0X25, '0', "5", 6),
	
	NONE("", '0', '0', "0", 2)
	;
	
	private String osSeparator;
	private char hexCode;
	private char optionalHexCode;
	private String labelDb;
	private Integer labelMq;
	
	LineSeparator(String osSeparator, char hexCode, char optionalHexCode, String labelDb, Integer labelMq){
		this.osSeparator = osSeparator;
		this.hexCode = hexCode;
		this.optionalHexCode = optionalHexCode;
		this.labelDb = labelDb;
		this.labelMq = labelMq;

		
	}
	
	public String getOsSeparator() { 
        return osSeparator;
    }

	public char getHexCode() { 
        return hexCode;
    }
	
	public char getOptionalHexCode() {
		return optionalHexCode;
	}
	
	public String getFullHexCode() {
		String hex = "" + getHexCode();
		
		if(getOptionalHexCode() != '0')
			hex += getOptionalHexCode();
		
		return hex;
	}
	
	public static LineSeparator getEnumByLabelDb(String label) {
		if(label == null) {
			return null;
		}
		return Stream.of(LineSeparator.values())
				.filter(s -> s.getLabelDb().equals(label))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static LineSeparator getEnumByLabelMq(Integer label) {
		if(label == null) {
			return null;
		}
		return Stream.of(LineSeparator.values())
				.filter(s -> s.getLabelMq().equals(label))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	public String getLabelDb() {
		return labelDb;
	}
	
	public Integer getLabelMq() {
		return labelMq;
	}

	
}
