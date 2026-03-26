package com.cbi.ccr.csw.dashboard.dto;


public class ControllerPath {

	private ControllerPath() {}
	
	private static final String PREFIX = "/api";
	
	public static final String UI = "/ui";
	public static final String LMI = PREFIX + "/lmi";
	public static final String FMS_PREFIX = LMI + "/fms";
	public static final String FMSDB_PREFIX = LMI + "/fmsdb";
	public static final String FMSMQ_PREFIX = LMI + "/fmsmq";
	public static final String FTS_PREFIX = LMI + "/fts";
	public static final String FTSDB_PREFIX = LMI + "/ftsdb";
	public static final String FTSDBFS_PREFIX = LMI + "/ftsdbfs";
	public static final String FTSMQ_PREFIX = LMI + "/ftsmq";
	public static final String MSS_PREFIX = LMI + "/mss";
	public static final String MSSDB_PREFIX = LMI + "/mssdb";
	public static final String MSSMQ_PREFIX = LMI + "/mssmq";
	public static final String ADDON_PREFIX = LMI + "/addon";
	public static final String SYSTEM_PREFIX = LMI + "/system";
	public static final String MQ_PRIMITIVE_PREFIX = LMI + "/mqprimitive";
	public static final String GLOBAL_PROPERTIES_PREFIX = LMI + "/globalproperties";
	public static final String FEMWS_PREFIX = LMI + "/femws";
	public static final String ROUTE_INTERFACE_PREFIX = LMI + "/routeinterface";


	
	public static final String SIGN_IN = "/signin";
	public static final String REFRESH_TOKEN = "/refreshtoken";

	public static final String USER_PREFIX = LMI + "/user";
	public static final String USER_LIST = "/list";
	public static final String USER_SAVE = "/save";
	public static final String USER_DELETE = "/delete";
	public static final String USER_UPDATE = "/update";
	public static final String CHANGE_PASSWORD = "/changepassword";
	public static final String RESET_PASSWORD = "/resetpassword";
	public static final String SECRET_DETAILS = "/secret";


	
	public static final String INBOUND = "/inbound";
	public static final String OUTBOUND = "/outbound";
	public static final String IN_OUT_LIST = "/list";
	public static final String RETRY_INVALID_BA_AND_INVALID_INTERFACE = "/retryInvalidBa";
	public static final String DB = "/db";
	public static final String MQ = "/mq";

	
	public static final String DASH = LMI +"/dashboard";
	public static final String CHART_MESSAGE_BY_MONTH_OUTBOUND = "/monthlyMessageOutbound";
	public static final String CHART_MESSAGE_BY_MONTH_INBOUND = "/monthlyMessageInbound";
	
	public static final String CHART_COUNT_FMS_OUTBOUND = "/countFMSOutbound";
	public static final String CHART_COUNT_FTS_OUTBOUND = "/countFTSOutbound";
	public static final String CHART_COUNT_MSS_OUTBOUND = "/countMSSOutbound";
	
	public static final String CHART_COUNT_FMS_INBOUND = "/countFMSInbound";
	public static final String CHART_COUNT_FTS_INBOUND = "/countFTSInbound";
	public static final String CHART_COUNT_MSS_INBOUND = "/countMSSInbound";
	
	public static final String CHART_RETRY_MESSAGE_OUTBOUND = "/countWaitingForRetryOutbound";
	public static final String CHART_SENDING_MESSAGE_OUTBOUND = "/countSendingOutbound";
	public static final String CHART_ONHUB_MESSAGE_OUTBOUND = "/countOnhubOutbound";
	public static final String CHART_SUCCESS_MESSAGE_OUTBOUND = "/countSuccessOutbound";
	
	public static final String CHART_FAILED_MESSAGE_INBOUND = "/countFailedMessageInbound";
	public static final String CHART_RETRY_MESSAGE_INBOUND = "/countWaitingForRetryInbound";
	public static final String CHART_RECEIVING_MESSAGE_INBOUND = "/countReceivingInbound";

	public static final String CONFIG = "/configurations";
	public static final String CONFIG_LIST = "/list";
	public static final String CONFIG_SAVE = "/save";
	public static final String CONFIG_DELETE = "/delete";


}
