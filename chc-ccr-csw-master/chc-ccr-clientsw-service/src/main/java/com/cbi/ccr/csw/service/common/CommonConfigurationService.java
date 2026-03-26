package com.cbi.ccr.csw.service.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cbi.ccr.csw.domain.ConfigRouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.dto.fms.FMSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.FTSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MSSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CommonConfigurationService extends AbstractService {

	@Autowired
	private ConfigRouteInterfaceRepository routeInterfaceRepo;

	@Autowired
	protected HubEncryptionUtil encryptionUtil;

	@Autowired
	private ApiGatewayStub apiGatewayStub;
	
	@Override
	@PostConstruct
	public void init() {
		super.init();
	}

	public RouteInterface getInterfaceFromConfigurationFMS(MessageWrapperDTO wrapper) throws ChcException {
		FMSInboundMessageDTO dto = null;
		dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())),
				FMSInboundMessageDTO.class);
		List<ConfigRouteInterface> configurations = routeInterfaceRepo
				.findByLocalBaIdAndRemoteBaIdAndService(dto.getLocalBaId(), dto.getRemoteBaId(), wrapper.getType());
		
		if(configurations.isEmpty() || configurations.get(0) == null) {
			log.info(Color.r("unable to elaborate the message received for service FMS"));
			String errorMessage = String.format("unable to find: local ba: %s and remote ba: %s on RouteInterface", dto.getLocalBaId(), dto.getRemoteBaId());
			log.info(Color.r(errorMessage));
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, errorMessage);
		}
		return configurations.get(0).getInterFace();

	}

	public RouteInterface getInterfaceFromConfigurationFTS(MessageWrapperDTO wrapper) throws ChcException {
		FTSInboundMessageDTO dto = null;
		dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())),
				FTSInboundMessageDTO.class);
		List<ConfigRouteInterface> configurations = routeInterfaceRepo
				.findByLocalBaIdAndRemoteBaIdAndService(dto.getLocalBaId(), dto.getRemoteBaId(), wrapper.getType());

		if(configurations.isEmpty() || configurations.get(0) == null) {
			log.info(Color.r("unable to elaborate the message received for service {}"), wrapper.getType());
			String errorMessage = String.format("unable to find: local ba: %s and remote ba: %s on RouteInterface", dto.getLocalBaId(), dto.getRemoteBaId());
			log.info(Color.r(errorMessage));
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, errorMessage);
		}
		
		return configurations.get(0).getInterFace();
		
	}

	public RouteInterface getInterfaceFromConfigurationMSS(MessageWrapperDTO wrapper) throws ChcException {
		MSSInboundMessageDTO dto = null;
		dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())),
				MSSInboundMessageDTO.class);
		List<ConfigRouteInterface> configurations = routeInterfaceRepo.findByLocalBaIdAndRemoteBaIdAndService(dto.getLocalBaId(), dto.getRemoteBaId(), wrapper.getType());
		
		if(configurations.isEmpty() || configurations.get(0) == null) {
			log.info(Color.r("unable to elaborate the message received for service MSS"));
			String errorMessage = String.format("unable to find: local ba: %s and remote ba: %s on RouteInterface", dto.getLocalBaId(), dto.getRemoteBaId());
			log.info(Color.r(errorMessage));
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, errorMessage);
		}
		
		return configurations.get(0).getInterFace();

	}

	public RouteInterface getInterfaceFromConfiguration(NotificationDTO dto) throws ChcException {

		List<ConfigRouteInterface> configurations = routeInterfaceRepo.findByLocalBaIdAndRemoteBaIdAndService(dto.getLocalBaId(), dto.getRemoteBaId(), dto.getService());
		
		if(configurations == null || configurations.isEmpty()) {
			String error = String.format("unable to find: ba: %s and ba: %s", dto.getLocalBaId(), dto.getRemoteBaId());
			log.info(Color.r(error));
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, error);
		}
		
		return configurations.get(0).getInterFace();

	}
	
	private byte[] getEncryptionServiceKey(UUID key) throws ChcException {
		byte[] encryptionServiceKey;
		try {
			encryptionServiceKey = apiGatewayStub.cryptoHubGetKey(key);
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | ChcStubException | DecoderException  e) {
			throw new ChcException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());
		}
		return encryptionServiceKey;
	}

}
