package com.cbi.ccr.csw.domain.csw.config;

public enum DSNCreationAlgo {
	
	DSN_MINUS_2(-2),
	DSN_MINUS_1(-1),
	DSN_1(1),
	DSN_2(2),
	DSN_3(3),
	DSN_4(4),
	DSN_5(5),
	DSN_6(6),
	DSN_7(7),
	DSN_8(8),
	DSN_9(9),
	DSN_10(10),
	;
	
	private int value;
	
	DSNCreationAlgo(int value) {
		this.value = value;
	}
	
	public int getValue() { 
        return value;
    }
	
	
	
}
