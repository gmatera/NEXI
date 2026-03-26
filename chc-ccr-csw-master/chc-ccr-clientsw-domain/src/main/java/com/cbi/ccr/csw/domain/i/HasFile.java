package com.cbi.ccr.csw.domain.i;

import javax.validation.constraints.NotNull;

public interface HasFile{

	public Long getFileSize();
	public void setFileSize(Long fileSize);
	public String getFileDigest();
	public void setFileDigest(String fileDigest);
	public String getFileDigestAlg();
	public void setFileDigestAlg(String fileDigestAlg);
	public void setFileDigestLen(Long fileDigestLen);
	
	public String getFileName();
	public void setFileName(@NotNull String fileName);
	
	public String getFileMD5();
	public void setFileMD5(String md5);
	
}
