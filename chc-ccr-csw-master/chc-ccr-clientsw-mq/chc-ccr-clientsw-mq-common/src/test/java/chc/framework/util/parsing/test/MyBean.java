package chc.framework.util.parsing.test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import chc.framework.util.parsing.input.annotaion.FlowioProperty;
import lombok.Data;

@Data
public class MyBean {

	private String string;
	private Long longProp;
	private Integer intProp;
	@FlowioProperty(decimanDigits = 3)
	private Double doubleProp;
	@FlowioProperty(decimanDigits = 3)
	private BigDecimal bigProp;
	private LocalDateTime dateTime;
	private LocalDate date;
	private MyEnum myEnum;
	
	public enum MyEnum{
		I_1, I_BLANK
	}
	

	
}
