

export class ControllerPath{
	public static FMS_PREFIX = "/fms";
	public static FMSDB_PREFIX = "/fmsdb";
	public static FMSMQ_PREFIX = "/fmsmq";
	public static FTS_PREFIX = "/fts";
	public static FTSDB_PREFIX = "/ftsdb";
	public static FTSDBFS_PREFIX = "/ftsdbfs";
	public static FTSMQ_PREFIX = "/ftsmq";
	public static MSS_PREFIX = "/mss";
	public static MSSDB_PREFIX = "/mssdb";
	public static MSSMQ_PREFIX = "/mssmq";
	public static ADDON_PREFIX = "/addon";
	public static SYSTEM_PREFIX = "/system";
	public static MQ_PRIMITIVE_PREFIX = "/mqprimitive";
	public static GLOBAL_PROPERTIES_PREFIX = "/globalproperties";
	public static SERVICE_REGISTRY_PREFIX = "/serviceRegistry";
	public static  FEMWS_PREFIX = "/femws";
	public static  USER_PREFIX = "/user";
	public static  USER_LIST = "/list";
	public static  USER_DELETE = "/delete/";
	public static  USER_UPDATE = "/update";
	public static  SECRET_DETAILS = "/secret";
	public static ROUTE_INTERFACE_PREFIX = "/routeinterface";




}

export class PageableDTO{
    offset: number = 0;
	maxRow: number = 50;
}

export class PagedResultDTO{
    totalPages: number = 0;
	totalItems: number = 0;
	
	list: any = [];
}