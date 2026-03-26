package com.cbi.ccr.common.udr;

import org.apache.commons.lang3.StringUtils;

import com.cbi.ccr.dto.mq.command.orch.UDRFmews;
import com.cbi.ccr.dto.mq.command.orch.UDRunbulked;

public abstract class UdrUtils {

	private UdrUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static UDRunbulked parseUDR(String udr) {
		UDRunbulked udrUmbulked = new UDRunbulked();
		udrUmbulked.setCSG(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 0, 8), null));
		udrUmbulked.setQA(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 8, 9), null));
		udrUmbulked.setIRS(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 9, 26), null));
		udrUmbulked.setCSC(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 26, 29), null));
		udrUmbulked.setQTM(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 29, 31), null));
		udrUmbulked.setPTM(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 31, 34), null));
		udrUmbulked.setCGM(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 34, 42), null));
		
		//fisso 2
		
		udrUmbulked.setSvcPhySender(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 44, 52), null));
		udrUmbulked.setSvcPhyReceiver(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 52, 60), null));
		udrUmbulked.setSvcLogSender(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 61, 69), null));
		udrUmbulked.setSvcLogReceiver(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 69, 77), null));
		udrUmbulked.setIDE2E(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 0, 44), null));
		
//		udrUmbulked.setCmf(udrUmbulked.getSvcPhySender());
//		udrUmbulked.setCdf(udrUmbulked.getSvcPhyReceiver());
//		udrUmbulked.setCml(udrUmbulked.getSvcLogSender());
//		udrUmbulked.setCdl(udrUmbulked.getSvcLogReceiver());
		
		return udrUmbulked;
	}
	
	public static UDRFmews parseUDRFemws(String udr) {
		return UDRFmews.builder()
				.csc(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 0, 8), null))
				.qa(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 8, 9), null))
				.irs(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 9, 26), null))
				.csc(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 26, 29), null))
				.qtm(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 29, 31), null))
				.ptm(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 31, 34), null))
				.cgm(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 34, 42), null))
				//fisso 42 - 44
				//cnc 44 -- 49
				//ac 49 -- 54
				//snc 54 --59
				//id 59 --60
				.cml(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 60, 68), null))
				.cdl(StringUtils.defaultIfEmpty(StringUtils.substring(udr, 68, 76), null))
				.build();
		
	}
	
}
