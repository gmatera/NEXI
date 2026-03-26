package chc.framework.util.parsing.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import chc.framework.util.parsing.input.FlowioPropertyFixed;
import chc.framework.util.parsing.test.model.AccountSimple;





public class FlowioOuptputTest {
	FlowioFixedPositionBinder<AccountSimple> binder;
	
	@BeforeEach
	public void before() {
		binder = new FlowioFixedPositionBinder<AccountSimple>(AccountSimple.class);

		binder.addProperty(new FlowioPropertyFixed("number", 0, 10));
		binder.addProperty(new FlowioPropertyFixed("orario", 48, 77));
		binder.addProperty(new FlowioPropertyFixed("iii", 77, 82));
		binder.addProperty(new FlowioPropertyFixed("lll", 82, 93));
		binder.addProperty(new FlowioPropertyFixed("ddd", 107, 127));
		binder.addProperty(new FlowioPropertyFixed("fff", 107, 127));
		binder.addProperty(new FlowioPropertyFixed("bbb", 107, 127));
		binder.addProperty(new FlowioPropertyFixed("data", 93, 103));
	
	}
	
	@Test
	public void testOutput() throws IOException, NoSuchFieldException, SecurityException, ChcException {
		
		FlowioOutputMapper<AccountSimple> flowioMapper = new FlowioOutputMapper<AccountSimple>(binder);

		AccountSimple account = new AccountSimple();
		account.setBbb(new BigDecimal(7000.89));
		account.setData(LocalDate.now());
		account.setFff(100.23f);
		account.setIii(200);
		account.setLll(300L);
		account.setDdd(-400.56d);
		account.setNumber("qwertyuiopfrejf43rfbreijfbrejfbn");
		account.setOrario(LocalDateTime.now());

		System.out.println(flowioMapper.writeByte(account));
	}
}
