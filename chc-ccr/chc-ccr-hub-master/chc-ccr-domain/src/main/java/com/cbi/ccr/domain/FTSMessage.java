package com.cbi.ccr.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ccr_fts_message")
public class FTSMessage extends CommonEntity {
	
	@Size(max = 50)
	@Column(name = "repo_file_id", length = 50)
	private String repoFileId;
	
	@Column(name = "VFN", length = 32, nullable = false)
	@Size(max = 32)
	private String vfn;
	
	@Column(name = "FNAME", length = 1024)
	@Size(max = 1024)
	private String fileName;
	
	@Column(name = "FSIZE")
	private Long fileSize;
	
	@Column(name = "FMD5", length = 32)
	@Size(max = 32)
	private String fileHash;
	
	@Column(name = "FILE_DIGEST_ALG", length = 8)
	@Size(max = 8)
	private String fileDigestAlg;
	
	@Column(name = "FILE_DIGEST", length = 128)
	@Size(max = 128)
	private String fileDigest;
	
	@Column(name = "RECORD_FORMAT")
	private String recordFormat;
	
	// Lunghezza massima della registrazione 
	// da 1 a 32752 per variabile
	// da 1 a 32760 per fissa
	@Column(name = "MAX_RECORD_LENGTH")
	private Integer maxRecordLength;
	
	// 0 (nessun separatore di riga)
	// 1 LF (0X0A)
	// 2 CRLF (0X0DOA)
	// 3 LFE15 (0x15)
	// 4 CRLFE15 (0x0D15)
	// 5 LFE25 (0x25)
	// 6 CRLFE25 (0x0D25)
	@Column(name = "LINE_SEPARATOR")
//	@Size(max = 1)
	private String lineSeparator;
	
	// 01 (binario)
	// 02 (ASCII)
	// 03 (EBCDIC)
	// 04 (923 ASCII)
	// 05 (924 EBCDIC)
	// 06 (858 ASCII)
	// 07 (1140 EBCDIC)
	// 08 (1148 EBCDIC)
	@Column(name = "CODE_PAGE")
	@Size(max = 10)
	private String codePage;
	
	@Column(name = "FTS_INTERFACE")
	private FTSInterface ftsInterface = FTSInterface.DB;
	
	@Column(name = "CORRELATION_ID")
	@Size(max = 30)
	private String correlationId;
	
}
