package chc.framework.util.parsing.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.cbi.frw.common.util.DateUtils;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;

public class Test2 {

	public static void main(String[] args) {

		try {
			FileOutputStream fos = new FileOutputStream(new File("/tmp/out/", "zaza.txt"));
			fos.close();
			//sucamilla
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	void zoneDateTime() throws IOException {
		Object val = DateUtils.parseZonedDateTime("20220822122928000000+0200",FlowioFixedPositionBinder.ISO_DATE_TIME_ZONE_MICROSEC_PARSE);
		assertNotNull(val);
		
		String format = DateUtils.format(ZonedDateTime.now(), FlowioFixedPositionBinder.ISO_DATE_TIME_ZONE_MICROSEC_FORMAT);
		assertNotNull(format);
	}
}
