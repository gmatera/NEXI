package com.cbi.ccr.csw.domain.i;

public interface CswFmsFtsMssConfigEntity  {
	
	public Long getId();
	
	public String getInterfaceType() ;
	public void setInterfaceType(String interfaceType);
	
	public Boolean getSndCompletionAlgo() ;
	public void setSndCompletionAlgo(Boolean sndCompletionAlgo);
	
	public Boolean getRcvCompletionAlgo();
	public void setRcvCompletionAlgo(Boolean rcvCompletionAlgo);

	public Boolean getLauEnabled();
	public void setLauEnabled(Boolean lauEnabled);
	
	public String getLauKey();
	public void setLauKey(String lauKey);
	


}
