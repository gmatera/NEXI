package com.cbi.ccr.inbound.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.ccr.inbound.util.LocalCache;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CCRInboundController {

	
	@Value("${integration_test_active}")
	protected Boolean integrationTestActive;
	
	@Autowired
	protected AsyncTaskExecutor taskExecutor;


	@GetMapping(path = InboundControllerPath.SERVICE_STATUS)
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok().build();
	}

	// TEST-SIMULATION
	protected boolean mustFail(String rt, String valueToCkeck) {
		
		if(valueToCkeck.indexOf(rt) != -1) {
			if(LocalCache.getInstance().size() == 0) {
				LocalCache.getInstance().put(rt, "1");
				return true;
			} else {
				// vfn = SU-116610686 dove SU-1 è il test case e 1 corrisponde al numero di failure da generare
				// devo recuperare 1, posizione da 3 a 4
				
				int indexOfKeywork = valueToCkeck.indexOf(rt);
				
				int numberOfFailure = Integer.parseInt(valueToCkeck.substring(indexOfKeywork + 3, indexOfKeywork + 4));
				int maxRetry = Integer.parseInt(valueToCkeck.substring(indexOfKeywork + 4, indexOfKeywork + 5));
				
				// recupero l'ultimo numero di failure
				int sentFailureNumber = Integer.parseInt(LocalCache.getInstance().get(rt));
				if(sentFailureNumber >= numberOfFailure) {
					LocalCache.getInstance().remove(rt);
					return false; 
				} else {
					// quando il numero di failure è maxRetry, devo svuotre la cache,
					// perchè on ci sarà un altra chiamata
					if(maxRetry == sentFailureNumber +1) {
						LocalCache.getInstance().remove(rt);
						log.info(Color.y(String.format("########## TEST-SIMULATION %s Clean-up", rt)));
					} else {
						LocalCache.getInstance().put(rt, String.valueOf(sentFailureNumber +1));
					}					
					return true;
				}
			}
		}
		return false; 
	}
	
	// TEST-SIMULATION
	protected boolean handleReadTimeout(String valueToCkeck) {
		String rt = "RT-";
		try {
			if(mustFail(rt, valueToCkeck)) {
				log.info(Color.y("########## TEST-SIMULATION RT-x ReadTimeout"));
				
				// deve essere ccr_http_timeout + 2 sec
				Thread.sleep(12000);	
				return true;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return false;
	}
	protected boolean handleServiceUnavailable(String valueToCkeck, HttpServletResponse response) {
		String rt = "SU-";
		if(mustFail(rt, valueToCkeck)) {
			log.info(Color.y("########## TEST-SIMULATION SU-x SERVICE_UNAVAILABLE"));
			response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
			return true;
		}
		return false;
	}
}
