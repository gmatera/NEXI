package com.cbi.ccr.csw.domain.i;

import javax.validation.constraints.NotNull;

public interface CswAddonConfigEntiry extends CswEntity {

	public Long getId();
	
	public String getSndPath();
	public void setSndPath(@NotNull String sndPath);
	
	public String getSendingPrefix();
	public void setSendingPrefix(String sendingPrefix);
	
	public String getErrorPrefix();
	public void setErrorPrefix(String errorPrefix);
	
	public String getSentPrefix();
	public void setSentPrefix(String sentPrefix);
	
	public String getErrorDeliverPrefix() ;
	public void setErrorDeliverPrefix(String errorDeliverPrefix);
	
}
