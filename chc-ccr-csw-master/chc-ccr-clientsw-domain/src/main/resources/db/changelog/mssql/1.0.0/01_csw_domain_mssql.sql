create table ADD_ON_CONFIGURATION_FTS (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION not null,
    LOCALBAID varchar(12),
    REMOTEBAID varchar(12),
    ERRORDELIVER_PREFIX varchar(255),
    ERROR_PREFIX varchar(255),
    RCV_PATH varchar(255),
    SENDING_PREFIX varchar(255),
    SENT_PREFIX varchar(255),
    SND_PATH varchar(255),
        CONSTRAINT [pk_ADD_ON_CONFIGURATION_FTS] PRIMARY KEY CLUSTERED ( 
        	[id] ASC 
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONF_ROUTE_INTERFACE (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    INTERFACE varchar(255) not null,
    LOCALBA_ID varchar(12),
    REMOTEBA_ID varchar(12),
    SERVICE varchar(255) not null,
        CONSTRAINT [pk_CONF_ROUTE_INTERFACE] PRIMARY KEY CLUSTERED ( 
        	[id] ASC 
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_FEM_WS (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    BAID varchar(12) not null,
    WEB_SERVER_URL varchar(255),
    WS_SOAP_ACTION varchar(50),
        CONSTRAINT [pk_CONFIGURATION_FEM_WS] PRIMARY KEY CLUSTERED ( 
        	[id] ASC 
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_FMS_LOCALBA_REMOTEBA (
    	id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    	LOCALBAID varchar(12),
		REMOTEBAID varchar(12),
    	INTERFACE_TYPE varchar(255) not null,
   	 	LAU_ENABLED bit DEFAULT 0,
    	RCV_CODE_PAGE varchar(255),
        RCV_COMPLETION_ALGO bit DEFAULT 1,
        RCV_DSN_CREATION_ALGO int,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT int,
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO bit DEFAULT 1,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        LAU_KEY varchar(255),
        HUB_CODE_PAGE varchar(255),
 		HUB_LINE_SEPARATOR varchar(255),
        CONSTRAINT [pk_CONFIGURATION_FMS_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
        	[id] ASC 
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    LOCALBAID varchar(12),
    REMOTEBAID varchar(12),
    INTERFACE_TYPE varchar(255) not null,
    LAU_ENABLED bit,
    LAU_KEY varchar(255),
    RCV_COMPLETION_ALGO bit,
    SND_COMPLETION_ALGO bit,
    HUB_CODE_PAGE varchar(255),
    HUB_LINE_SEPARATOR varchar(255),
    RCV_CODE_PAGE varchar(255),
    RCV_LINE_SEPARATOR varchar(255),
    RCV_MAX_REC_LENGHT int,
    RCV_PATH varchar(255),
    RCV_RECORD_FORMAT varchar(255),
    SND_CODE_PAGE varchar(255),
    SND_LINE_SEPARATOR varchar(255),
    SND_MAX_REC_LENGHT int,
    SND_RECORD_FORMAT varchar(255),
    MQI_POS_CREATE_IND bit,
    RCV_AUTO_READ bit,
    RCV_PRIM_CONV_FORMAT varchar(255),
    UPLOAD_Q_NAME varchar(255),
    CONSTRAINT [pk_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_FTS_LOCALBA_REMOTEBA (
   		id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
		LOCALBAID varchar(12),
		REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED bit DEFAULT 0,
        RCV_CODE_PAGE varchar(255),
        RCV_COMPLETION_ALGO bit DEFAULT 1,
        RCV_DSN_CREATION_ALGO int,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT int,
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO bit DEFAULT 1,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        LAU_KEY varchar(255),
        HUB_CODE_PAGE varchar(255),
  		HUB_LINE_SEPARATOR varchar(255),
        CONSTRAINT [pk_CONFIGURATION_FTS_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
        	[id] ASC 
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
 create table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    LOCALBAID varchar(12),
    REMOTEBAID varchar(12),
    INTERFACE_TYPE varchar(255) not null,
    LAU_ENABLED bit,
    LAU_KEY varchar(255),
    RCV_COMPLETION_ALGO bit,
    SND_COMPLETION_ALGO bit,
    HUB_CODE_PAGE varchar(255),
    HUB_LINE_SEPARATOR varchar(255),
    RCV_CODE_PAGE varchar(255),
    RCV_LINE_SEPARATOR varchar(255),
    RCV_MAX_REC_LENGHT int,
    RCV_PATH varchar(255),
    RCV_RECORD_FORMAT varchar(255),
    SND_CODE_PAGE varchar(255),
    SND_LINE_SEPARATOR varchar(255),
    SND_MAX_REC_LENGHT int,
    SND_RECORD_FORMAT varchar(255),
    MQI_POS_CREATE_IND bit,
    RCV_AUTO_READ bit,
    RCV_PRIM_CONV_FORMAT varchar(255),
    UPLOAD_Q_NAME varchar(255),
    CONSTRAINT [pk_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_MQ_PRIMITIVE (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    MQ_CHANNEL varchar(255),
    PRIMITIVE varchar(255),
    QUEUE_NAME varchar(255),
    TO_LOAD bit,
    CONSTRAINT [pk_CONFIGURATION_MQ_PRIMITIVE] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_MSS_LOCALBA_REMOTEBA (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    INTERFACE_TYPE varchar(255) not null,
    LAU_ENABLED bit,
    LAU_KEY varchar(255),
    LOCALBAID varchar(12),
    RCV_COMPLETION_ALGO bit,
    REMOTEBAID varchar(12),
    SND_COMPLETION_ALGO bit,
    CONSTRAINT [pk_CONFIGURATION_MSS_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    LOCALBAID varchar(12),
    REMOTEBAID varchar(12),
    INTERFACE_TYPE varchar(255) not null,
    LAU_ENABLED bit,
    LAU_KEY varchar(255),
    RCV_COMPLETION_ALGO bit,
    SND_COMPLETION_ALGO bit,
    RCV_PRIM_CONV_FORMAT varchar(255),
    CONSTRAINT [pk_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table csw_service_registry (
   id varchar(60) not null,
    last_update datetime2,
    group_id varchar(60),
    host_name varchar(80),
    port int,
    roles varchar(255),
    service_status varchar(30),
    CONSTRAINT [pk_csw_service_registry] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table csw_service_registry_log (
   id varchar(50) not null,
    insert_date datetime2,
    log varchar(1024),
    log_type varchar(30),
    service_registry_id varchar(50),
    CONSTRAINT [pk_csw_service_registry_log] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table FMS_RECV_MQI (
   ID bigint not null,
    MESSAGE varbinary(MAX),
    ACCEPT_TMS datetime2,
    ADF varchar(80),
    ADF_LEN int,
    BA_PROCESS_TMS datetime2,
    CATAPPL varchar(4),
    COMPLETE int,
    CORRELATION_ID varchar(30),
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(60),
    END_READ_TMS datetime2,
    FEN_SUB_TMS datetime2,
    FER_SUB_TIME datetime2,
    FILE_DIGEST varchar(128),
    FILE_DIGEST_ALG varchar(8),
    FILE_DIGEST_LEN bigint,
    FNAME varchar(1024),
    FSIZE bigint not null,
    FIRST_BA_DLV_TMS datetime2,
    GROUP_ID varchar(48),
    HOST_FIRST_DEL_TMS datetime2,
    HOST_FIRST_SUB_TMS datetime2,
    LINE_SEPARATOR varchar(255),
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCAL_BA_DATA varchar(80),
    LOCALBA_ID varchar(12) not null,
    MAX_REC_LEN int,
    MESSAGELEN int not null,
    MESSAGETYPE varchar(3) not null,
    MSG_DIGEST varchar(128),
    MSG_DIGEST_ALG varchar(8),
    MSG_DIGEST_LEN bigint,
    NET_FILE_SIZE bigint,
    PRIMITIVE_ERROR varchar(50),
    QUEUE_FILENAME varchar(48),
    RCV_CHAR_TYPE varchar(255),
    RECORD_FORMAT varchar(255),
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    SND_CHAR_TYPE varchar(255) not null,
    START_READ_TMS datetime2,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TRANSFER_ID varchar(16) not null,
    TUR varchar(16),
    UDR varchar(80) not null,
    UDR_LEN int not null,
    CSW_VERSION bigint,
    VFN varchar(32) not null,
    CONSTRAINT [pk_FMS_RECV_MQI] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table FMS_SEND_MQI (
    ID_SYNC_SEND bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    MESSAGE varbinary(MAX),
    ACCEPTTIME datetime2,
    ADF varchar(80),
    ADF_LEN int,
    BA_PROCESS_TMS datetime2,
    CATAPPL varchar(4),
    CHAR_TYPE varchar(255) not null,
    COMPLETE int,
    COMPLETETIME datetime2,
    CORRELATION_ID varchar(30),
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(60),
    END_CREATE_TIMESTAMP datetime2,
    ERROR_TIMESTAMP datetime2,
    FILE_DIGEST varchar(128),
    FILE_DIGEST_ALG varchar(8),
    FILE_DIGEST_LEN bigint,
    FNAME varchar(1024),
    FSIZE bigint not null,
    GROUP_ID varchar(48) not null,
    LINE_SEPARATOR varchar(255) not null,
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCAL_BA_DATA varchar(80),
    LOCALBA_ID varchar(12) not null,
    MAX_REC_LEN int,
    MESSAGELEN int not null,
    MESSAGETYPE varchar(3) not null,
    MSG_DIGEST varchar(128),
    MSG_DIGEST_ALG varchar(8),
    MSG_DIGEST_LEN bigint,
    NEG_DETAIL_OPER varchar(1024),
    NET_FILE_SIZE bigint,
    NOTIFYTIME datetime2,
    CSW_PRIMITIVE varchar(20),
    PRIMITIVE_ERROR varchar(50),
    QUEUE_FILENAME varchar(48) not null,
    RECORD_FORMAT varchar(255),
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    SEND_ERROR_TIMESTAMP datetime2,
    SENDTIME datetime2,
    SEND_TYPE int,
    START_CREATE_TIMESTAMP datetime2,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TRANSFER_ID varchar(16),
    TUR varchar(16),
    UDR varchar(80) not null,
    UDR_LEN int not null,
    CSW_VERSION bigint,
    VFN varchar(32) not null,
    ORIGINAL_PRIMITIVE varbinary(max),
    CONSTRAINT [pk_FMS_SEND_MQI] PRIMARY KEY CLUSTERED ( 
    	[ID_SYNC_SEND] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table FTS_RECV_MQI (
   ID bigint not null,
    ACCEPT_TMS datetime2,
    ADF varchar(80),
    ADF_LEN int,
    BA_PROCESS_TMS datetime2,
    COMPLETE int,
    CORRELATION_ID varchar(30),
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(60),
    END_READ_TMS datetime2,
    FEN_SUB_TMS datetime2,
    FER_SUB_TIME datetime2,
    FILE_DIGEST varchar(128),
    FILE_DIGEST_ALG varchar(8),
    FILE_DIGEST_LEN bigint,
    FNAME varchar(1024),
    FSIZE bigint,
    FIRST_BA_DLV_TMS datetime2,
    GROUP_ID varchar(48),
    HOST_FIRST_DEL_TMS datetime2,
    HOST_FIRST_SUB_TMS datetime2,
    LINE_SEPARATOR varchar(255),
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCAL_BA_DATA varchar(80),
    LOCALBA_ID varchar(12) not null,
    MAX_REC_LEN int,
    NET_FILE_SIZE bigint not null,
    PRIMITIVE_ERROR varchar(50),
    QUEUE_FILENAME varchar(48),
    RCV_CHAR_TYPE varchar(255),
    RECORD_FORMAT varchar(255),
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    SND_CHAR_TYPE varchar(255) not null,
    START_READ_TMS datetime2,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TRANSFER_ID varchar(16) not null,
    TUR varchar(16),
    CSW_VERSION bigint,
    VFN varchar(32) not null,
    CONSTRAINT [pk_FTS_RECV_MQI] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
 create table FTS_SEND_MQI (
   ID_SEND_FILE bigint IDENTITY(1,1) NOT FOR REPLICATION  not null,
    ACCEPTTIME datetime2,
    ADF varchar(80),
    ADF_LEN int,
    BA_PROCESS_TMS datetime2,
    CHAR_TYPE varchar(255) not null,
    COMPLETE int,
    COMPLETETIME datetime2,
    CORRELATION_ID varchar(30),
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(60),
    END_CREATE_TIMESTAMP datetime2,
    ERROR_TIMESTAMP datetime2,
    FILE_DIGEST varchar(128),
    FILE_DIGEST_ALG varchar(8),
    FILE_DIGEST_LEN bigint,
    FNAME varchar(1024),
    FSIZE bigint not null,
    GROUP_ID varchar(48) not null,
    LINE_SEPARATOR varchar(255) not null,
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCAL_BA_DATA varchar(80),
    LOCALBA_ID varchar(12) not null,
    MAX_REC_LEN int,
    MESSAGETYPE varchar(3),
    NEG_DETAIL_OPER varchar(1024),
    NET_FILE_SIZE bigint,
    NOTIFYTIME datetime2,
    CSW_PRIMITIVE varchar(20),
    PRIMITIVE_ERROR varchar(50),
    QUEUE_FILENAME varchar(48) not null,
    RECORD_FORMAT varchar(255),
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    SEND_ERROR_TIMESTAMP datetime2,
    SENDTIME datetime2,
    SEND_TYPE int,
    START_CREATE_TIMESTAMP datetime2,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TRANSFER_ID varchar(16),
    CSW_VERSION bigint,
    VFN varchar(32) not null,
    ORIGINAL_PRIMITIVE varbinary(max),
    CONSTRAINT [pk_FTS_SEND_MQI] PRIMARY KEY CLUSTERED ( 
    	[ID_SEND_FILE] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table GLOBAL_CONFIGURATION (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION not null,
    DISPLAY_NAME varchar(255),
    MANDATORY bit,
    MAX_LENGTH int,
    MIN_LENGTH int,
    PROPERTY varchar(255),
    REQUIRED_MSG varchar(255),
    TYPE varchar(255),
    VALUE varchar(3000),
    CONSTRAINT [pk_GLOBAL_CONFIGURATION] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table MSS_RECV_MQI (
   ID bigint not null,
    MESSAGE varbinary(MAX),
    BA_PROCESS_TMS datetime2,
    CATAPPL varchar(4),
    COMPLETE int,
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(255),
    FEN_SUB_TMS datetime2,
    FER_SUB_TIME datetime2,
    FIRST_BA_DLV_TMS datetime2,
    HOST_FIRST_DEL_TMS datetime2,
    HOST_FIRST_SUB_TMS datetime2,
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCALBA_ID varchar(12) not null,
    MESSAGELEN int not null,
    MESSAGETYPE varchar(3) not null,
    MSG_DIGEST varchar(128),
    MSG_DIGEST_ALG varchar(8),
    MSG_DIGEST_LEN bigint,
    MSGID varchar(30) not null,
    PRIMITIVE_ERROR varchar(50),
    PRIORITY int not null,
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TUR varchar(16),
    UDR varchar(80),
    UDR_LEN int,
    CSW_VERSION bigint,
    CONSTRAINT [pk_MSS_RECV_MQI] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table MSS_SEND_MQI (
   FAS_SEQID bigint IDENTITY(1,1) NOT FOR REPLICATION not null,
    MESSAGE varbinary(MAX),
    ACCEPTTIME datetime2,
    BA_PROCESS_TMS datetime2,
    BA_REQ_TMS datetime2,
    CAT_APPL varchar(4),
    COMPLETE int,
    COMPLETETIME datetime2,
    CORRELATION_ID varchar(30),
    CSW_INSERT_TIMESTAMP datetime2,
    CSW_PROCESS_TMS datetime2,
    CSW_STATUS varchar(255),
    SEND_ERROR_TIMESTAMP datetime2,
    LOCAL_AUTH_INFO varchar(128),
    LOCAL_AUTH_INFO_ALG varchar(8),
    LOCAL_AUTH_INFO_LEN bigint,
    LOCAL_BA_DATA varchar(100),
    LOCALBA_ID varchar(12) not null,
    MESSAGELEN int not null,
    MSG_TYPE varchar(3) not null,
    MSG_DIGEST varchar(128),
    MSG_DIGEST_ALG varchar(8),
    MSG_DIGEST_LEN bigint,
    NOTIFYTIME datetime2,
    PRIMITIVE_ERROR varchar(50),
    PRIORITY int not null,
    REJECT_REASON int,
    REMOTEBA_ID varchar(12) not null,
    CSW_RETRY_CNT int,
    SENDTIME datetime2,
    STATUS varchar(255),
    STATUS_INFO varchar(1024),
    TUR varchar(16),
    UDR varchar(80),
    UDR_LEN int,
    CSW_VERSION bigint,
    CSW_PRIMITIVE VARCHAR(20),
    ORIGINAL_PRIMITIVE varbinary(max)
    CONSTRAINT [pk_MSS_SEND_MQI] PRIMARY KEY CLUSTERED ( 
    	[FAS_SEQID] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
create table USERS (
   id bigint IDENTITY(1,1) NOT FOR REPLICATION not null,
    full_Name varchar(255),
    password varchar(255),
    roles varchar(300),
    SECRET_ANSWER_ONE varchar(255),
    SECRET_ANSWER_TWO varchar(255),
    SECRET_RESPONSE_ONE varchar(255),
    SECRET_RESPONSE_TWO varchar(255),
    username varchar(255),
    CONSTRAINT [pk_USERS] PRIMARY KEY CLUSTERED ( 
    	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/
alter table ADD_ON_CONFIGURATION_FTS 
   add constraint UNIQUE_ADD_ON_CONFIGURATION_FTS unique NONCLUSTERED (LOCALBAID, REMOTEBAID) 
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table FMS_SEND_MQI
  add constraint FMS_SEND_MQI_VFN_LBA_RBA unique NONCLUSTERED (VFN, UDR, LOCALBA_ID, REMOTEBA_ID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONF_ROUTE_INTERFACE 
   add constraint UNIQUE_CONF_ROUTE_INTERFACE unique NONCLUSTERED (LOCALBA_ID, REMOTEBA_ID, INTERFACE, SERVICE)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_FEM_WS 
   add constraint UNIQUE_CONFIGURATION_FEMWS unique NONCLUSTERED (BAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_FMS_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_FTS_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_FTS_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_MSS_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA 
   add constraint UNIQUE_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA unique NONCLUSTERED (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
alter table USERS 
   add constraint UNIQUE_USER unique NONCLUSTERED (username)
   WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE SYNC_SEND ADD CSW_STATUS VARCHAR(255)
/
ALTER TABLE SYNC_SEND ADD CSW_VERSION bigint DEFAULT 0
/
ALTER TABLE SYNC_RECV ADD CSW_STATUS VARCHAR(255)
/
ALTER TABLE SEND_FILE ADD CSW_STATUS VARCHAR(255)
/
ALTER TABLE SEND_FILE ADD CSW_VERSION bigint DEFAULT 0
/
ALTER TABLE SEND_FILE ADD FTS_INTERFACE VARCHAR(2) DEFAULT 'DB'
/
ALTER TABLE RECV_FILE ADD FTS_INTERFACE VARCHAR(2) DEFAULT 'DB'
/
ALTER TABLE RECV_FILE ADD CSW_STATUS VARCHAR(255)
/
ALTER TABLE FAS_MSG_SEND ADD CSW_STATUS VARCHAR(255)
/
ALTER TABLE FAS_MSG_SEND ADD CSW_VERSION bigint DEFAULT 0
/
ALTER TABLE FAS_MSG_SEND ADD CSW_RETRY_CNT int
/
ALTER TABLE FAS_MSG_RECV ADD CSW_STATUS VARCHAR(255)
/


