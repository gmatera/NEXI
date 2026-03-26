package com.cbi.ccr.csw.service.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileRecordFormatValidator extends AbstractService {

	private static final String WRONG_LENGTH = "At least one record has the wrong length";
	
	@Value("${conversion-folder}")
	private String conversionFolder;

	// ATTENZIONE !! metodo comune siam ad interfaccia DB che MQ
	public void processFileValidation(String fileName, CodePage codePage, LineSeparator lineSeparator,
			Integer maxRecLength, RecordFormat recordFormat) throws ChcException {
		if (codePage.equals(CodePage.BINARY))
			return;
		File file = new File(fileName);

		if (recordFormat.equals(RecordFormat.FIXED)) {
			validateRecordFormatFixedLength(file, lineSeparator, maxRecLength);
		} else {
			validateRecordFormatVariableLength(file, lineSeparator, maxRecLength);
		}
	}
	// MEK MEK
//	public String checkToConvertFileFMS(String localBaId, String remoteBaId, String fileName) throws ChcException {
//
//		ConfigurationFMSDB conf = configurationService.loadFMSConfiguration(localBaId, remoteBaId);
//		if (!conf.getSndCodePage().equals(CodePage.EBCDIC))
//			return fileName;
//
//		File ebcdicFile = new File(fileName);
//		File asciiFile = new File(conversionFolder, "ascii-" + UUID.randomUUID());
//		FileConversionUtils.convertFileFromEBCDICtoASCII(ebcdicFile, asciiFile);
//
//		return asciiFile.getAbsolutePath();
//	}

	protected void validateLineSeparatorVariableLength(File sourceFile, LineSeparator lineSeparator, Integer bufferSize)
			throws ChcException {
		try (FileInputStream fis = new FileInputStream(sourceFile)) {
			boolean isWindows = lineSeparator.getOsSeparator().equals(LineSeparator.CRLF_0X0D0A.getOsSeparator());
			byte[] buffer = new byte[(isWindows) ? 2 : 1];
			int lineSeparatorCounter = 0;
			while (fis.read(buffer) != -1)
				for (int i = 0; i < buffer.length; i++) {
					log.debug("IS WINDOWS?: " + isWindows + " buffer:" + buffer[i] + ", index: " + lineSeparatorCounter
							+ ", bufferSize:" + buffer.length + "\n");
					if (!isWindows && buffer[i] == lineSeparator.getHexCode())
						lineSeparatorCounter += 1;

					if (isWindows && buffer[0] == lineSeparator.getHexCode()
							&& buffer[1] == lineSeparator.getOptionalHexCode())
						lineSeparatorCounter += 1;
				}
			if (lineSeparatorCounter < 1)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "line separator not matched");
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_FILE_NOT_FOUND);
		}
	}

	protected void validateLineSeparatorFixedlength(File sourceFile, LineSeparator lineSeparator, Integer bufferSize)
			throws ChcException {
		try (FileInputStream fis = new FileInputStream(sourceFile)) {
			boolean isWindows = lineSeparator.getOsSeparator().equals(LineSeparator.CRLF_0X0D0A.getOsSeparator());
			byte[] buffer = new byte[bufferSize];
			while (fis.read(buffer) != -1) {
				if (!isWindows && buffer[buffer.length - 1] != lineSeparator.getHexCode())
					throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "line separator not matched");
				if (isWindows && !(buffer[buffer.length - 2] == lineSeparator.getHexCode()
						&& buffer[buffer.length - 1] == lineSeparator.getOptionalHexCode()))
					throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "line separator not matched");
			}
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_FILE_NOT_FOUND);
		}
	}

	private void validateRecordFormatFixedLength(File sourceFile, LineSeparator lineSeparator, Integer maxRecLength)
			throws ChcException {

		try (FileInputStream fis = new FileInputStream(sourceFile)) {
			if (Files.size(sourceFile.toPath()) < maxRecLength)
				throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
						WRONG_LENGTH);

			if (lineSeparator == LineSeparator.NONE) {
				if (Files.size(sourceFile.toPath()) % maxRecLength != 0) {
					throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
							WRONG_LENGTH);
				}
			} else {
				boolean isWindows = lineSeparator.getOsSeparator().equals(LineSeparator.CRLF_0X0D0A.getOsSeparator());
				byte[] buffer = new byte[(isWindows) ? maxRecLength + 2 : maxRecLength + 1];
				int byteRead;
				
				Scanner scan = null;
				try  {
					scan = new Scanner(fis).useDelimiter(lineSeparator.getFullHexCode());
					while (scan.hasNext()) {
						String line = scan.next();
						
						if(line == null)
							throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION, WRONG_LENGTH);
						
						if(line.contains(lineSeparator.getFullHexCode())) {
							
							if(isWindows && line.length() != maxRecLength -2) 
								throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION, WRONG_LENGTH);
							
							if(!isWindows && line.length() != maxRecLength -1)
								throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION, WRONG_LENGTH);
							
						} else {
							if(line.length() != maxRecLength) 
								throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION, WRONG_LENGTH);
						}
						
					}
				} finally {
					if (scan != null)
						scan.close();
				}
				
//				while ( (byteRead = fis.read(buffer)) != -1) {
//					
//					if(isWindows)
//						windows(fis, buffer, byteRead);
//					else
//						unix(fis, buffer, byteRead);
//				}
			}
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_FILE_NOT_FOUND);
		}
	}

//	private void windows(FileInputStream fis, byte[] buffer, int byteRead) throws IOException, ChcException {
//
//		if (!((buffer[buffer.length - 2] == LineSeparator.CRLF_0X0D0A.getHexCode() && buffer[buffer.length - 1] == LineSeparator.CRLF_0X0D0A.getOptionalHexCode())
//				|| (buffer[buffer.length - 2] == LineSeparator.CRLF_0X0D25.getHexCode() && buffer[buffer.length - 1] == LineSeparator.CRLF_0X0D25.getOptionalHexCode())))
//			if (!(buffer[buffer.length - 2] == 0 && buffer[buffer.length - 1] == 0 && fis.read(buffer) == -1)) {
//
//				log.debug("Ultimi 2 caratteri: " + new String(buffer));
//				throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
//						WRONG_LENGTH);
//			}
//	}
//
//	private void unix(FileInputStream fis, byte[] buffer, int byteRead) throws IOException, ChcException {
//		
//		if (buffer[buffer.length - 1] != LineSeparator.LF_0X0A.getHexCode())
//			if (!(buffer[buffer.length - 1] == 0 && fis.read(buffer) == -1)) {
//				log.info("Ultimo carattere: " + new String(buffer));
//				throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
//						WRONG_LENGTH);
//			}
//	}

	private void validateRecordFormatVariableLength(File sourceFile, LineSeparator lineSeparator,
			Integer maxRecordLength) throws ChcException {
		try (FileInputStream fis = new FileInputStream(sourceFile)) {
			boolean isWindows = lineSeparator.getOsSeparator().equals(LineSeparator.CRLF_0X0D0A.getOsSeparator());

			if (!isWindows) {

				byte[] buffer = new byte[1];
				int index = 0;

				while (fis.read(buffer) != -1) {
					
//					if (index == 120)
//						log.info("Index: {}", index);
					
					for (int i = 0; i < buffer.length; i++) {
						index = checkRecord(maxRecordLength, isWindows, buffer, index);
						if (index > maxRecordLength)
							throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
									WRONG_LENGTH);
					}
				}
			} else {
				Scanner scan = null;
				try  {
					scan = new Scanner(fis).useDelimiter(lineSeparator.getFullHexCode());
					while (scan.hasNext()) {
						String line = scan.next();
						if (line != null && line.length() > maxRecordLength) {
							throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION,
									WRONG_LENGTH);
						}
					}
				} finally {
					if (scan != null)
						scan.close();
				}
			}
		}catch (FileNotFoundException e) {
			throw new ChcException(I18nCommon.ERR_FILE_NOT_FOUND);
		}
		catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS);
		}

	}

	private int checkRecord(Integer maxRecordLength, boolean isWindows, byte[] buffer, int index)
			throws ChcException {

		if (isWindows && ((buffer[0] == LineSeparator.CRLF_0X0D0A.getHexCode()
				&& buffer[1] == LineSeparator.CRLF_0X0D0A.getOptionalHexCode())
				|| (buffer[0] == LineSeparator.CRLF_0X0D25.getHexCode()
						&& buffer[1] == LineSeparator.CRLF_0X0D25.getOptionalHexCode()))) {
			if (index >= maxRecordLength) {
				CswLog.debug(log,"Ultimi 2 caratteri: " + new String(buffer));
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, WRONG_LENGTH);
			}
			index = 0;
			return index;
		}

		if (!isWindows
				&& ((buffer[0] == LineSeparator.LF_0X0A.getHexCode()) || index == maxRecordLength + 1)) {
			if (index > maxRecordLength) {
				CswLog.debug(log, "Ultimo carattere: " + new String(buffer));
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, WRONG_LENGTH);
			}
			index = 0;
			return index;
		}
		index = index + 1;
		return index;
	}

}
