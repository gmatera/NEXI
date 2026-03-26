package com.cbi.ccr.csw.inbound.common.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.fms.FMSRecv;
import com.cbi.ccr.csw.domain.fts.FTSRecv;
import com.cbi.ccr.csw.domain.i.CswInboundEntity;
import com.cbi.ccr.csw.domain.i.HasFile;
import com.cbi.ccr.csw.domain.mss.MSSRecv;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcSystemException;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.common.util.FileConversionUtils;
import com.cbi.frw.common.util.FileUtils;
import com.cbi.frw.http.ChcStubException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class CSWCommonInboundService<E extends CswInboundEntity, R extends JpaRepository<E, Long>, D extends ClientMessageDTO> 
	extends CSWCommonService{

	@Value("${local_storage_path_decrypted}")
	protected String localStoragePathDecrypted;
	
	@NonNull
	protected R dbRepository;
	
	@NonNull
	protected Class<E> entityClass;

	public abstract ValidationService getValidationService();
	
	@Override
	@PostConstruct
	public void init() {
		super.init();
		try {
			FileUtils.createFolders(localStoragePathDecrypted);
		} catch (IOException e) {
			throw new ChcSystemException(e.getMessage());
		}
	}


	protected void log(E entity, String message, LogLevel level, Exception e) {
		log.error(String.format("%s %s id:%s localBA:%s remoteBA:%s", getType(entity), message, entity.getId(), entity.getLocalBaId(),
				entity.getRemoteBaId()));
	}

	protected void log(E entity, String message, LogLevel level) {
		switch (level) {
		case DEBUG:
			log.debug(String.format("%s %s id:%s localBA:%s remoteBA:%s", getType(entity), message, entity.getId(),
					entity.getLocalBaId(), entity.getRemoteBaId()));
			break;
		case INFO:
			log.info(String.format("%s %s id:%s localBA:%s remoteBA:%s", getType(entity), message, entity.getId(),
					entity.getLocalBaId(), entity.getRemoteBaId()));
			break;
		default:
			break;
		}
	}

	private String getType(E entity) {
		if (entity instanceof FMSRecv) {
			return ServiceType.FMS.name();
		}
		if (entity instanceof FTSRecv) {
			return ServiceType.FTS.name();
		}
		if (entity instanceof MSSRecv) {
			return ServiceType.MSS.name();
		}
		return entity.getClass().getSimpleName();
	}

	protected <C extends ConfigurationFMSFTSCommon> long handleFile(HasFile cswEntity, InputStream file, D dto, C config, UUID fileKey) throws ChcException, IOException {
		Path rcvFolder = Paths.get(config.getRcvPath());
		if (!Files.exists(rcvFolder)) {
			try {
				Files.createDirectories(rcvFolder);
			} catch (IOException e) {
				throw new ChcException(I18nService.ERR_CREATING_RECEVING_DIRECTORY);
			}
		}

		File decriptedFile = new File(cswEntity.getFileName());
		
		byte[] encryptionServiceKey;
		try {
			encryptionServiceKey = apiGatewayStub.cryptoHubGetKey(fileKey);
			encryptionUtil.decryptFile(file, decriptedFile, encryptionServiceKey);
			
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | ChcStubException | DecoderException  e) {
			throw new ChcException(I18nCommon.ERR_DECRYPT, e.getLocalizedMessage());
		}
		
		
		

		// il CodePage, LineSeparator, MaxRecordLength, RecordFormat sono null se il messaggio arriva dalla PA, senza parire da un'altro CSW
		
		if(dto.getCodePage() == null) {
			if(config.getHubCodePage() == null) {
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing hub code page");
			} else {
				dto.setCodePage(config.getHubCodePage().name());
			}
		}
		
		verifyHubCodePage(cswEntity, dto, config);
		
		getValidationService().validateSize(decriptedFile.getAbsolutePath(), decriptedFile.length(),
				ValidationService.FILE_MAX_SIZE, false);
		
		convertFileFromHUB(cswEntity, dto, config, decriptedFile);
		

//		if(dto.getCodePage().equals(CodePage.BINARY.name()))
//			return fileSize;
//		
//		if(dto.getLineSeparator() == null) {
//			if(config.getHubLineSeparator() == null) {
//				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing hub line separator");
//			} else {
//				dto.setLineSeparator(config.getHubLineSeparator().name());
//			}
//		}
//		
//		if(config.getRcvLineSeparator() == null)
//			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing rcv line separator");
		
//		if(dto.getLineSeparator().equals(LineSeparator.O.name())) 
//			dto.setLineSeparator(config.getRcvLineSeparator().name());
//		if (dto.getCodePage().equals(CodePage.ASCII.name())
//				&& !config.getRcvLineSeparator().equals(LineSeparator.CRLF_0X0D0A)
//				&& !dto.getLineSeparator().equals(config.getRcvLineSeparator().name())) {
//			fileSize = checkAndConvertLineSeparator(cswEntity, dto, config, decriptedFile);
//		}
//		//TO MERGE IN V20
//		if (!dto.getCodePage().equals(CodePage.BINARY.name()))
//			checkAndConvertCodePage(cswEntity, dto, config, decriptedFile);
		return decriptedFile.length();

	}
	
	private <C extends ConfigurationFMSFTSCommon> void verifyHubCodePage(HasFile cswEntity, D dto, C config) throws ChcException {
		
		
		if(dto.getCodePage().equals(CodePage.ASCII.name())) {
			if(dto.getLineSeparator() == null) {
				if(config.getHubLineSeparator() == null) {
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing hub line separator");
				} else {
					dto.setLineSeparator(config.getHubLineSeparator().name());
				}
			}
			
			if(config.getRcvLineSeparator() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing rcv line separator");
			
			if (dto.getMaxRecordLength() == null) {
				dto.setMaxRecordLength(config.getRcvMaxRecLength());
			}
			
			if (dto.getRecordFormat() == null) {
				dto.setRecordFormat(config.getRcvRecordFormat().name());
			}
		}
	}
	
	private <C extends ConfigurationFMSFTSCommon> void convertFileFromHUB(HasFile cswEntity, D dto, C config, File decriptedFile) throws ChcException, IOException {
		
		// dal CCR può arrivare un Codepage BINARY o ASCII
		// se ASCII devo eventualmente convertire il LineSeparator in base alla configurazione locale
		
		if(dto.getCodePage().equals(CodePage.ASCII.name())) {
			// FIle ASCII
			
			/**
			 *  Il LineSeparator ricevuto dal CCR è sempre CRLF_0X0D0A 
			 *  con CodePage ASCII (in questo pezzo di codice), 
			 *  
			 *  Il LineSeparator CRLF_0X0D25 è la rappresentazione esadecimale per i file EBCDIC di CRLF_0X0D0A, 
			 *  di conseguenza è impossibile che arrivi dal CCR un file contenente questo lineSeparator, 
			 *  questo significa che la conversione non va effettua per questo specifico LS. 
			 *  
			 *  La conversione va quindi fatta sollo in presenza di un LS da sistemi UNIX (LF_0X0A)
			 */
			if (!LineSeparator.CRLF_0X0D0A.equals(config.getRcvLineSeparator()) && !LineSeparator.CRLF_0X0D25.equals(config.getRcvLineSeparator())) {
				checkAndConvertLineSeparator(cswEntity, dto, config, decriptedFile);
			}
			
			checkAndConvertCodePage(cswEntity, dto, config, decriptedFile);
				
		} 

	}

	private <C extends ConfigurationFMSFTSCommon> void checkAndConvertCodePage(
			HasFile cswEntity, D dto, C config, File decriptedFile) throws ChcException, IOException {
		if (!dto.getCodePage().equals(config.getRcvCodePage().name())) {
			File tempFile = new File(cswEntity.getFileName() + "_Converted");
			if (config.getRcvCodePage().equals(CodePage.ASCII)) {
				FileConversionUtils.convertFileFromEBCDICtoASCII(decriptedFile, tempFile);
				Files.delete(decriptedFile.toPath());
				Files.move(tempFile.toPath(), decriptedFile.toPath());
			} else if (config.getRcvCodePage().equals(CodePage.EBCDIC)) {
				FileConversionUtils.converFileFromASCIItoEBCDIC(decriptedFile, tempFile);
				Files.delete(decriptedFile.toPath());
				Files.move(tempFile.toPath(), decriptedFile.toPath());
			}
		}
	}

	private <C extends ConfigurationFMSFTSCommon> File checkAndConvertLineSeparator(HasFile cswEntity, D dto, C config,
			File decriptedFile) throws IOException {
		
		File updateLS = new File(cswEntity.getFileName() + "_updatingLS");
		LineSeparator dtoLS = Enum.valueOf(LineSeparator.class, dto.getLineSeparator());
		LineSeparator configLS = config.getRcvLineSeparator();
		
		if (!dtoLS.equals(configLS)) {
			try (BufferedReader reader = new BufferedReader(new FileReader(decriptedFile));
					PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(updateLS)));) {

				String str;
				while ((str = reader.readLine()) != null) {
					if (config.getRcvLineSeparator().equals(LineSeparator.NONE))
						writer.print(str);
					else
						writer.print(String.format("%s%s", str, config.getRcvLineSeparator().getFullHexCode()));
				}
				writer.flush();

			} finally {
				Files.delete(decriptedFile.toPath());
				// creo il file convertito nello steddo path di decriptedFile
				Files.move(updateLS.toPath(), decriptedFile.toPath());
			}
		}
		return decriptedFile;
	}

	private <C extends ConfigurationFMSFTSCommon> byte[] convertLineSeparatorTo(C config, boolean isToWindows) {
		byte[] bufferWriter;

		if (isToWindows) {
			bufferWriter = new byte[2];
			bufferWriter[0] = (byte) config.getRcvLineSeparator().getHexCode();
			bufferWriter[1] = (byte) config.getRcvLineSeparator().getOptionalHexCode();
		} else {
			bufferWriter = new byte[1];
			bufferWriter[0] = (byte) config.getRcvLineSeparator().getHexCode();
		}

		return bufferWriter;
	}

	protected ChcException mapException(Exception e) {
		if (e instanceof ChcException) {
			return (ChcException) e;
		}
		log.error("Errore ", e);
		return new ChcException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());

	}

	protected String createDSN(DSNCreationAlgo rcvDSNCreationAlgo, String rcvDsnPrefix, String vfn, String remoteBa,
			String localBa) throws ChcException {
		StringBuilder sb = new StringBuilder();
		final String YYMMDD = "yyMMdd";
		final String HHMMSS = "HHmmss";
		final String ZZZ = "SSS";
		LocalDateTime date = LocalDateTime.now();

		RandomStringUtils.random(3, true, true);
		switch (rcvDSNCreationAlgo) {
		case DSN_MINUS_2:
			return UUID.randomUUID().toString();
		case DSN_MINUS_1:
			return vfn;
		case DSN_1:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return rcvDsnPrefix;
		case DSN_2:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".A").append(remoteBa.substring(0, 5)).append(".D")
					.append(date.format(DateTimeFormatter.ofPattern(YYMMDD))).append(".H")
					.append(date.format(DateTimeFormatter.ofPattern(HHMMSS))).toString();
		case DSN_3:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".A").append(remoteBa.substring(0, 5)).append(".D")
					.append(vfn.substring(24, 30)).append(".V").append(vfn.substring(30, 32)).toString();
		case DSN_4:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".A").append(localBa.substring(0, 5)).append(".A")
					.append(remoteBa.substring(0, 5)).append(".D").append(vfn.substring(24, 30)).append(".V")
					.append(vfn.substring(30, 32)).toString();
		case DSN_5:
			
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".D")
					.append(date.format(DateTimeFormatter.ofPattern(YYMMDD))).append(".H")
					.append(date.format(DateTimeFormatter.ofPattern(HHMMSS))).append(".U")
					.append(date.format(DateTimeFormatter.ofPattern(ZZZ)))
					.append(RandomStringUtils.random(3, true, true)).toString();
		case DSN_6:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".A").append(remoteBa.substring(0, 5)).append(".D")
					.append(date.format(DateTimeFormatter.ofPattern(YYMMDD))).append(".H")
					.append(date.format(DateTimeFormatter.ofPattern(HHMMSS))).append(".U")
					.append(date.format(DateTimeFormatter.ofPattern(ZZZ)))
					.append(RandomStringUtils.random(3, true, true)).toString();
		case DSN_7:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".C").append(remoteBa.substring(5, 10)).append(".A")
					.append(remoteBa.substring(0, 5)).append(".D").append(vfn.substring(24, 30)).append(".V")
					.append(vfn.substring(30, 32)).toString();
		case DSN_8:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".C").append(remoteBa.substring(5, 10)).append(".A")
					.append(localBa.substring(0, 5)).append(".A").append(remoteBa.substring(0, 5)).append(".D")
					.append(vfn.substring(24, 30)).append(".V").append(vfn.substring(30, 32)).toString();
		case DSN_9:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".C").append(remoteBa.substring(5, 10)).append(".A")
					.append(remoteBa.substring(0, 5)).append(".D")
					.append(date.format(DateTimeFormatter.ofPattern(YYMMDD))).append(".P")
					.append(RandomStringUtils.random(5, true, true)).toString();
		case DSN_10:
			if(StringUtils.isEmpty(rcvDsnPrefix))
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
			return sb.append(rcvDsnPrefix).append(".D")
					.append(date.format(DateTimeFormatter.ofPattern(YYMMDD))).append(".P")
					.append(RandomStringUtils.random(5, true, true)).toString();
		default:
			throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, rcvDSNCreationAlgo);
		}

	}

	protected String createStatusInfo(CodePage codePage, LineSeparator lineSeparator, RecordFormat recordFormat,
			Integer maxRecLength) {

		// FILEINFO=(RECORD_FORMAT=F,MAX_RECORD_LENGTH=120,LINE_SEPARATOR=1,CODE_PAGE=02)

		if (codePage == CodePage.BINARY)
			return "FILEINFO=(CODE_PAGE=" + codePage.getValueDB() + ")";

		return "FILEINFO=(RECORD_FORMAT=" + recordFormat.getLabelDB() + ",MAX_RECORD_LENGTH=" + maxRecLength
				+ ",LINE_SEPARATOR=" + lineSeparator.getLabelDb() + ",CODE_PAGE=" + codePage.getValueDB() + ")";

	}

}
