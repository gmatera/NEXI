package com.cbi.ccr.csw.femws.service.dto;

/**
 * Data holder per il contenuto dello header SOAP di una richiesta in arrivo
 * 
 * @author TFSCostabileMichele
 *
 */
public class FemsWsHeaderIn {
	private String actor;
	private Boolean mustUnderstand;
	/** codice ABI del nodo emittente */
	private String ClientNetCode;
	/** codice ABI del nodo destinatario */
	private String ServerNetCode;
	/** User data Reference, un riferimento applicativo ad uso del nodo client */
	private String UDR;
	/** codice applicativo di rete */
	private String ApplCode;
	/**
	 * codice indicativo dell'ambiente. Esempio 00 (doppio zero) produzione, PR test
	 */
	private String Env;
	/**
	 * Identificativo univoco generato dal FEMS-WS client. Usato per il tracciamento
	 */
	private String suid;
	/** Label della chiave di autenticazione utilizzata */
	private String lau;
	/** timestamp aggiunto durante il percorso, per il tracciamento */
	private String ReqDT;
	/** timestamp aggiunto durante il percorso, per il tracciamento */
	private String ResDT;
	/** timestamp aggiunto durante il percorso, per il tracciamento */
	private String ReqST;
	/** timestamp aggiunto durante il percorso, per il tracciamento */
	private String ResST;

	public String getReqDT() {
		return ReqDT;
	}

	public void setReqDT(String reqDT) {
		ReqDT = reqDT;
	}

	public String getResDT() {
		return ResDT;
	}

	public void setResDT(String resDT) {
		ResDT = resDT;
	}

	public String getReqST() {
		return ReqST;
	}

	public void setReqST(String reqST) {
		ReqST = reqST;
	}

	public String getResST() {
		return ResST;
	}

	public void setResST(String resST) {
		ResST = resST;
	}

	public String getLau() {
		return lau;
	}

	public void setLau(String lau) {
		this.lau = lau;
	}

	public String getSuid() {
		return suid;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public Boolean getMustUnderstand() {
		return mustUnderstand;
	}

	public void setMustUnderstand(Boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}

	public String getClientNetCode() {
		return ClientNetCode;
	}

	public void setClientNetCode(String clientNetCode) {
		ClientNetCode = clientNetCode;
	}

	public String getServerNetCode() {
		return ServerNetCode;
	}

	public void setServerNetCode(String serverNetCode) {
		ServerNetCode = serverNetCode;
	}

	public String getUDR() {
		return UDR;
	}

	public void setUDR(String uDR) {
		UDR = uDR;
	}

	public String getApplCode() {
		return ApplCode;
	}

	public void setApplCode(String applCode) {
		ApplCode = applCode;
	}

	public String getEnv() {
		return Env;
	}

	public void setEnv(String env) {
		Env = env;
	}
}
