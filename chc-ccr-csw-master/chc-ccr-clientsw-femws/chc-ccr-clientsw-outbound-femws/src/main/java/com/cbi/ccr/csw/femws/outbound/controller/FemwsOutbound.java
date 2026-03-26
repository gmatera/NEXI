package com.cbi.ccr.csw.femws.outbound.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.femws.outbound.service.FemwsOutboundService;
import com.cbi.ccr.csw.femws.service.FemsConfiguration;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@RequestMapping(CSWOutboundControllerPath.BASE+CSWOutboundControllerPath.SOAP_OUTBOUND)
@RequestMapping("/")

public class FemwsOutbound {
	
	@Autowired
	private FemwsOutboundService femwsService;
	@Value("${csw_version}")
	private String cswVersion;

	@Autowired
	private FemsConfiguration configuration;
	
	@GetMapping
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ChcException, ChcStubException {
		response.addHeader("FEMS-WS-version", cswVersion);
        response.sendError(405, "Method not allowed. This service expects POST requests");
	}
	
	
	@PostMapping
	public void doPost(HttpServletRequest req, HttpServletResponse response){
		
		CswLog.getLogData().setModule(ModuleEnum.SOAP_OUTBOUND.name());
		CswLog.getLogData().setFunction("post");
		
		femwsService.processOutbound(req, response);
	}
	
	@PostMapping("ping")
	public void ping(HttpServletRequest request, HttpServletResponse response) throws IOException, ChcException, ChcStubException {
		
		try {
            SOAPMessage soapMsg = null;

            soapMsg = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
            SOAPBody soapBody = soapMsg.getSOAPBody();
            SOAPFault fault = soapBody.addFault();
            fault.setFaultCode("ping reply");
            fault.setFaultString(configuration.getFemswsName());
            fault.setFaultActor("Client");
            soapMsg.saveChanges();
            soapMsg.writeTo(response.getOutputStream());
            response.setContentType("text/xml");
            response.flushBuffer();
        } catch (SOAPException e) {
            try {
                response.getOutputStream().write(
                        ("<SOAP-ENV:Envelope><SOAP-ENV-Body/>\n" +
                         "    <SOAP-ENV:Fault>\n" +
                         "        <faultcode>soap exception</faultcode>\n" +
                         "        <faultstring>exception</faultstring>\n" +
                         "        <faultactor>client</faultactor>\n" +
                         "    </SOAP-ENV:Fault>\n" +
                         "</SOAP-ENV:Envelope>").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e1) {
                log.error("cannot write into response {}", e.toString());
            }
        } catch (IOException e) {
            log.error("cannot write ping response {}", e.toString());
        }
	}
}
