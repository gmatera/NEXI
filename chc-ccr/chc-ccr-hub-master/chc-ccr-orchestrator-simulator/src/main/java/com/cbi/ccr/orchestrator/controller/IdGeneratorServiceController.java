package com.cbi.ccr.orchestrator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.dto.ChcUid;
import com.cbi.ccr.dto.IdGeneratorServicePath;
import com.cbi.ccr.dto.Uids;

@RestController
@RequestMapping()
public class IdGeneratorServiceController {

	
	@GetMapping(IdGeneratorServicePath.ID_GENERATOR_SERVICE_PATH)
	public ResponseEntity<Uids> chcPhyId(@RequestHeader("x-request-id") String id, @RequestHeader Integer size){
		
		Uids uids = new Uids();
		ChcUid[] ids = new ChcUid[size];
		
		for(int i =  0; i < size; i++) {
			ids[i] = new ChcUid(String.valueOf(System.currentTimeMillis()));
		}
		
		uids.setUids(ids);
		
		return ResponseEntity.ok(uids);
	}
}
