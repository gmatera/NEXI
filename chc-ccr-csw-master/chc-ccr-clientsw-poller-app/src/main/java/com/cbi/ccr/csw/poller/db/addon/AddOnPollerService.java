package com.cbi.ccr.csw.poller.db.addon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnFTSConfiguration;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.poller.db.I18nPoller;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.common.util.FileUtils;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.extern.slf4j.Slf4j;

@Profile("POLLER_ADDON")
@Slf4j
@Service
public class AddOnPollerService extends AbstractService {

	@Autowired 
	private AddOnConfigurationFTSRepository repo;
	
	@Autowired 
	private FTSSendDBRepository ftsRepo;
	
	private List<AddOnFTSConfiguration> configurations;
	
	@Value("${outbound_worker_threads}")
	private int outboundWorkerThreads;
	
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.POLLER_ADDON)));
	}
	
	@Scheduled(fixedDelay = 5000)
	public void process() throws ChcException, IOException {
		if(configurations == null) {
			configurations = loadConfigurations();
		}
		
		if(configurations != null) {
			for(AddOnFTSConfiguration config: configurations) {
				List<File> tempList = scanDirectoryForNewFiles(new File(config.getSndPath() + File.separator), getPrefixList(config));
				if(!tempList.isEmpty())
					submit(tempList, config);
			}
		}
	}
	
	private void submit(List<File> files, AddOnFTSConfiguration config) throws IOException, ChcException {
		for(File file: files) {
			
			String oldFileName = String.valueOf(file.getAbsolutePath());
			String newFileName = config.getSndPath() + File.separator + config.getSendingPrefix()+ file.getName();
			Path source = Paths.get(oldFileName);
			
//			if(ftsRepo.countByFileNameAndFtsInterface(oldFileName, RouteInterface.FS) > 1) {
//				String error = config.getSndPath() + File.separator + config.getErrorPrefix()+ file.getName();
//				Files.move(source , Paths.get(error), StandardCopyOption.REPLACE_EXISTING);
//				return;
//			}
			
			FTSSend entity = buildEntity(config, oldFileName, file);

			FTSSend ftsCheck = ftsRepo.findOneByLocalBaIdAndRemoteBaIdAndFtsInterfaceAndFileName(entity.getLocalBaId(), entity.getRemoteBaId(), RouteInterface.FS, newFileName);
			if(ftsCheck != null)
				fileAlreadyExists(config, file, source);

			
			transactionTemplate.executeWithoutResult(t -> ftsRepo.save(entity));
			log.debug("file saved into db {}", entity.getFileName());
		

			Files.move(source , Paths.get(newFileName));
			 
		    log.debug("file renamed with Sending Prefix:",newFileName);
		}
	}

	private void fileAlreadyExists(AddOnFTSConfiguration config, File file, Path source)
			throws IOException, ChcException {
		String errorFileName = config.getSndPath() + File.separator + config.getErrorPrefix()+ file.getName();
		Files.deleteIfExists(Paths.get(errorFileName));
		Files.move(source , Paths.get(errorFileName));
		throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS, "File already exists");
	}
	
	private FTSSend buildEntity(AddOnFTSConfiguration config, String newFileName, File file) throws IOException {
		FTSSend message = new FTSSend();
		message.setLocalBaId(config.getLocalBaId());
		message.setRemoteBaId(config.getRemoteBaId());
		message.setVfn(buildVfn(config));
		message.setFileName(newFileName);
		Path filePath = Paths.get(file.getAbsolutePath());
		long size = Files.size(filePath);
		message.setFileSize(size);
		message.setFileDigestAlg("SHA-256");
		try (FileInputStream fis = new FileInputStream(file)) {
			message.setFileDigest(Base64.getEncoder().encodeToString(DigestUtils.sha256(fis)));
		}
		message.setComplete(0);
		message.setStatus(FTSSendStatus.FILE_TO_BE_PROCESSED);
		message.setStsCode(FTSSendStatus.FILE_TO_BE_PROCESSED.getStsCode());
		message.setApplCheck(0);
		message.setFtsInterface(RouteInterface.FS);
		message.setRetryCounter(0);
		return message;
	}
	
	private String buildVfn(AddOnFTSConfiguration config) {
		StringBuilder sb = new StringBuilder();
		return sb
				.append(config.getLocalBaId().substring(0, 5)).
				append(config.getRemoteBaId().substring(0, 5)).
				append(config.getLocalBaId().substring(5, 12)).
				append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"))).toString();
	}

	private List<AddOnFTSConfiguration> loadConfigurations() throws ChcException {
		List<AddOnFTSConfiguration> configurations = repo.findAll();
		
		if(configurations.isEmpty()) {
			log.error(Color.r("MISSING_CONFIGURATION_ADD_ON_FTS, will try again later on... "));
			return null;
		}
		log.info("loaded ADD-ON configurations: {}", JSON.toJson(configurations));
		
		if(configurations.isEmpty())
			throw new ChcException(I18nPoller.MISSING_CONFIGURATION_ADD_ON_FTS);
		
		return configurations;
	}
	


	private List<String> getPrefixList(AddOnFTSConfiguration config) {
		List<String> prefixList = new ArrayList<>();
		prefixList.add(config.getErrorDeliverPrefix());
		prefixList.add(config.getErrorPrefix());
		prefixList.add(config.getSendingPrefix());
		prefixList.add(config.getSentPrefix());
		return prefixList;
	}
	
	private List<File> scanDirectoryForNewFiles(File sndPath, List<String> prefixList) {
		
		if(!Files.exists(sndPath.toPath())){
			try {
				FileUtils.createFolders(sndPath.getAbsolutePath());
			} catch (IOException e) {
				log.error("Unable to create folders {}, {}", sndPath.getAbsolutePath(), e.toString());
			}
			return Collections.emptyList();
		}
		
		log.debug("Scanning directory: {}", sndPath.getAbsolutePath());
		List<File> fileToProcess = new ArrayList<>();
		
		File[] files =  sndPath.listFiles();
		for (int i = 0; i < files.length; i++) {
			log.debug("File scanned: {}", files[i].getName());
			boolean toInclude = true;
			
			for(String prefix: prefixList) {
				if(files[i].getName().startsWith(".") || files[i].getName().contains(prefix)) {
					log.debug("file ignored: {}", files[i].getName());
					toInclude = false;
					break;
				}
			}
			if(toInclude) {
				fileToProcess.add(files[i]);
			}
		}
		
		return fileToProcess;
	}
	
}
