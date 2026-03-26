package chc.framework.util.parsing.test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;
import chc.framework.util.parsing.input.FlowioInputMapper;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import chc.framework.util.parsing.input.FlowioPropertyFixed;
import chc.framework.util.parsing.input.exception.WrongChecksumException;

public class FlowioTest {

	private FlowioFixedPositionBinder<MyBean> binder;
	
	@BeforeEach
	public void before(){
		binder = new FlowioFixedPositionBinder<>(MyBean.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 3));
		binder.addProperty(new FlowioPropertyFixed("baLoc", 4, 15));
		binder.addProperty(new FlowioPropertyFixed("intProp", 20, 30));
		binder.addProperty(new FlowioPropertyFixed("doubleProp", 30, 40));
		binder.addProperty(new FlowioPropertyFixed("bigProp", 40, 50));
		binder.addProperty(new FlowioPropertyFixed("dateTime", 50, 79));
		binder.addProperty(new FlowioPropertyFixed("date", 79, 89));
	}
	
	
	@Test
	public void testOut() throws IOException, URISyntaxException, NoSuchFieldException, ChcException {
	
		FlowioOutputMapper<MyBean> flowioMapper = new FlowioOutputMapper<>(binder);
		
		MyBean bean = new MyBean();
		bean.setBigProp(BigDecimal.valueOf(-25.123));
		bean.setDoubleProp(null);
		bean.setDoubleProp(Double.valueOf(12.123));
		bean.setIntProp(23);
		bean.setLongProp(12L);
		bean.setString("abcdefghiL");
		bean.setDateTime(LocalDateTime.parse("2003-01-27T00:00:00.000000000"));
		bean.setDate(LocalDate.of(2005, 2, 25));
		String stringBean = new String(flowioMapper.writeByte(bean));
	    
	    assertEquals(stringBean.substring(0,10), bean.getString());
	}
	
	@Test
	public void testReturnOut() throws IOException, URISyntaxException, NoSuchFieldException, ChcException {
	
		MyBean bean = new MyBean();
		bean.setBigProp(BigDecimal.valueOf(-25.123));
		bean.setDoubleProp(null);
		bean.setDoubleProp(Double.valueOf(12.123));
		bean.setIntProp(23);
		bean.setLongProp(12L);
		bean.setString("abcdefghiL");
		bean.setDateTime(LocalDateTime.parse("2003-01-27T00:00:00.000000000"));
		bean.setDate(LocalDate.of(2005, 2, 25));  
	    System.out.println(binder.toStringRow(bean)); 
	}
	
}
