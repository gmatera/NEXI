-- *************************************************************************
-- conf_route_interface
-- *************************************************************************
create sequence SEQ_CONF_ROUTE_INTERFACE start with 1 increment by  1
/
create table CONF_ROUTE_INTERFACE (
    id number(19,0) not null,
    INTERFACE varchar2(255 char) not null,
    LOCALBA_ID varchar2(12 char) not null,
    REMOTEBA_ID varchar2(12 char) not null,
    SERVICE varchar2(255 char) not null
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table CONF_ROUTE_INTERFACE add constraint PK_CONF_ROUTE_INTERFACE 
 primary key (id)
 using index tablespace ${DB_USER}_INDEX
/

alter table CONF_ROUTE_INTERFACE add constraint UNIQUE_CONF_ROUTE_INTERFACE 
 unique (LOCALBA_ID, REMOTEBA_ID, INTERFACE, SERVICE)
 using index tablespace ${DB_USER}_INDEX
/

-- *************************************************************************
-- add_on_configuration_fts
-- *************************************************************************
create sequence SEQ_ADD_ON_CONFIGURATION_FTS start with 1 increment by  1
/
create table ADD_ON_CONFIGURATION_FTS (
    id number(19,0) not null,
	LOCALBAID VARCHAR2(12),
    REMOTEBAID VARCHAR2(12),
    SND_PATH VARCHAR2(255) not null,
    SENDING_PREFIX VARCHAR2(255),
    ERROR_PREFIX VARCHAR2(255),
    SENT_PREFIX VARCHAR2(255),
    ERRORDELIVER_PREFIX VARCHAR2(255),
    RCV_PATH VARCHAR2(255)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table ADD_ON_CONFIGURATION_FTS add constraint PK_ADD_ON_CONFIGURATION_FTS 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table ADD_ON_CONFIGURATION_FTS add constraint UNIQUE_ADD_ON_CONFIGURATION_FTS 
 unique (LOCALBAID, REMOTEBAID) 
 using index tablespace ${DB_USER}_INDEX
/
alter table ADD_ON_CONFIGURATION_FTS add constraint SND_PATH_CONSTRAINT 
 unique (SND_PATH) 
 using index tablespace ${DB_USER}_INDEX
/

-- *************************************************************************
-- configuration_fms_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA start with 1 increment by  1
/
create table CONFIGURATION_FMS_LOCALBA_REMOTEBA (
    id number(19,0) not null,
	LOCALBAID VARCHAR2(12),
	REMOTEBAID VARCHAR2(12),
    INTERFACE_TYPE VARCHAR2(255) not null,
    LAU_ENABLED NUMBER(1) DEFAULT 0,
    RCV_CODE_PAGE VARCHAR2(255),
    RCV_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    RCV_DIGEST_FILE_ALG NUMBER(10),
    RCV_DSN_CREATION_ALGO NUMBER(10),
    RCV_DSN_PREFIX VARCHAR2(44),
    RCV_LINE_SEPARATOR VARCHAR2(255),
    RCV_RECORD_FORMAT VARCHAR2(255),
    RCV_MAX_REC_LENGHT NUMBER(10),
    RCV_PATH VARCHAR2(255),
    SND_CODE_PAGE VARCHAR2(255),
    SND_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    SND_LINE_SEPARATOR VARCHAR2(255),
    SND_MAX_REC_LENGHT NUMBER(10),
    SND_PATH VARCHAR2(255),
    SND_RECORD_FORMAT VARCHAR2(255),
    LAU_KEY VARCHAR2(255),
    HUB_CODE_PAGE VARCHAR2(255),
 	HUB_LINE_SEPARATOR VARCHAR2(255)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_FMS_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_FMS_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_FMS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/
-- *************************************************************************
-- configuration_fts_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_FTS_LOCALBA_REMOTEBA start with 1 increment by  1
/
create table CONFIGURATION_FTS_LOCALBA_REMOTEBA (
    id number(19,0) not null,
	LOCALBAID VARCHAR2(12),
	REMOTEBAID VARCHAR2(12),
    INTERFACE_TYPE VARCHAR2(255) not null,
    LAU_ENABLED NUMBER(1) DEFAULT 0,
    RCV_CODE_PAGE VARCHAR2(255),
    RCV_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    RCV_DIGEST_FILE_ALG NUMBER(10),
    RCV_DSN_CREATION_ALGO NUMBER(10),
    RCV_DSN_PREFIX VARCHAR2(44),
    RCV_LINE_SEPARATOR VARCHAR2(255),
    RCV_RECORD_FORMAT VARCHAR2(255),
    RCV_MAX_REC_LENGHT NUMBER(10),
    RCV_PATH VARCHAR2(255),
    SND_CODE_PAGE VARCHAR2(255),
    SND_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    SND_LINE_SEPARATOR VARCHAR2(255),
    SND_MAX_REC_LENGHT NUMBER(10),
    SND_PATH VARCHAR2(255),
    SND_RECORD_FORMAT VARCHAR2(255),
    LAU_KEY VARCHAR2(255),
    HUB_CODE_PAGE VARCHAR2(255),
  	HUB_LINE_SEPARATOR VARCHAR2(255)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_FTS_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_FTS_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_FTS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FTS_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/
-- *************************************************************************
-- configuration_mss_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA start with 1 increment by  1
/
create table CONFIGURATION_MSS_LOCALBA_REMOTEBA (
    id number(19,0) not null,
    INTERFACE_TYPE VARCHAR2(255) not null,
    LOCALBAID VARCHAR2(12),
	REMOTEBAID VARCHAR2(12),
    SND_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    RCV_COMPLETION_ALGO NUMBER(1) DEFAULT 1,
    LAU_ENABLED NUMBER(1) DEFAULT 0,
    LAU_KEY VARCHAR2(255)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_MSS_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_MSS_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_MSS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/
create table csw_service_registry (
   id varchar2(60) not null,
    group_id varchar2(60),
    host_name varchar2(80),
    last_update timestamp,
    port number(10,0),
    roles varchar2(255),
    service_status varchar2(30)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table csw_service_registry add constraint PK_csw_service_registry 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
create table csw_service_registry_log (
   id varchar2(50) not null,
    insert_date timestamp,
    log varchar2(1024),
    log_type varchar2(30),
    service_registry_id varchar2(50)
)
   TABLESPACE ${DB_USER}_TABLE
/
alter table csw_service_registry_log add constraint PK_csw_service_registry_log 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/

-- *************************************************************************
-- USER TABLE (Password is admin)
-- *************************************************************************

create sequence SEQ_USERS start with 1 increment by  1
/
create table USERS ( 
    id number(19,0) not null, 
    username varchar2(255 char) not null, 
    full_Name varchar2(255 char), 
    roles varchar2(300 char) not null,  
    password varchar2(255 char) not null, 
    SECRET_ANSWER_ONE VARCHAR2(255),
    SECRET_ANSWER_TWO VARCHAR2(255),
    SECRET_RESPONSE_ONE VARCHAR2(255),
    SECRET_RESPONSE_TWO VARCHAR2(255)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table USERS add constraint PK_USERS 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table USERS add constraint UNIQUE_USER 
 unique (USERNAME)
 using index tablespace ${DB_USER}_INDEX
/
 create table FMS_RECV_MQI (
   ID number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   CORRELATION_ID varchar2(30),
   VFN varchar2(32) not null,
   FSIZE number(19,0) not null,
   QUEUE_FILENAME varchar2(48),
   GROUP_ID varchar2(48),
   LINE_SEPARATOR varchar2(255),
   RECORD_FORMAT varchar2(255),
   MAX_REC_LEN number(10,0),
   SND_CHAR_TYPE varchar2(255) not null,
   RCV_CHAR_TYPE varchar2(255),
   ADF_LEN number(10,0),
   ADF varchar2(80),
   UDR_LEN number(10,0) not null,
   UDR varchar2(80) not null,
   TUR varchar2(16),
   MESSAGETYPE varchar2(3) not null,
   CATAPPL varchar2(4),
   LOCAL_BA_DATA varchar2(80),
   NET_FILE_SIZE number(19,0),
   TRANSFER_ID varchar2(16) not null,
   FNAME varchar2(1024),
   FILE_DIGEST_ALG varchar2(8),
   FILE_DIGEST_LEN number(19,0),
   FILE_DIGEST varchar2(128),
   MSG_DIGEST_ALG varchar2(8),
   MSG_DIGEST_LEN number(19,0),
   MSG_DIGEST varchar2(128),
   MESSAGELEN number(10,0) not null,
   MESSAGE blob,
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   ACCEPT_TMS DATE,
   START_READ_TMS DATE,
   END_READ_TMS DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   HOST_FIRST_SUB_TMS DATE,
   FER_SUB_TIME DATE,
   FEN_SUB_TMS DATE,
   HOST_FIRST_DEL_TMS DATE,
   FIRST_BA_DLV_TMS DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0)
)
   TABLESPACE ${DB_USER}_TABLE 
   LOB(MESSAGE) 
   STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table FMS_RECV_MQI add constraint PK_FMS_RECV_MQI 
 primary key (ID) 
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_FMS_SEND_MQI start with 1 increment by  1
/
 create table FMS_SEND_MQI (
   ID_SYNC_SEND number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   SEND_TYPE number(10,0),
   CORRELATION_ID varchar2(30),
   VFN varchar2(32) not null,
   FSIZE number(19,0) not null,
   QUEUE_FILENAME varchar2(48) not null,
   GROUP_ID varchar2(48) not null,
   LINE_SEPARATOR varchar2(255) not null,
   RECORD_FORMAT varchar2(255),
   MAX_REC_LEN number(10,0),
   CHAR_TYPE varchar2(255) not null,
   ADF_LEN number(10,0),
   ADF varchar2(80),
   UDR_LEN number(10,0) not null,
   UDR varchar2(80) not null,
   TUR varchar2(16),
   MESSAGETYPE varchar2(3) not null,
   CATAPPL varchar2(4),
   LOCAL_BA_DATA varchar2(80),
   NET_FILE_SIZE number(19,0),
   TRANSFER_ID varchar2(16),
   FNAME varchar2(1024),
   FILE_DIGEST_ALG varchar2(8),
   FILE_DIGEST_LEN number(19,0),
   FILE_DIGEST varchar2(128),
   MSG_DIGEST_ALG varchar2(8),
   MSG_DIGEST_LEN number(19,0),
   MSG_DIGEST varchar2(128),
   MESSAGELEN number(10,0) not null,
   MESSAGE blob,
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   ACCEPTTIME DATE,
   START_CREATE_TIMESTAMP DATE,
   END_CREATE_TIMESTAMP DATE,
   ERROR_TIMESTAMP DATE,
   SENDTIME DATE,
   COMPLETETIME DATE,
   NOTIFYTIME DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   SEND_ERROR_TIMESTAMP DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   NEG_DETAIL_OPER varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0),
   CSW_PRIMITIVE VARCHAR2(20),
   ORIGINAL_PRIMITIVE BLOB
)
   TABLESPACE ${DB_USER}_TABLE 
   LOB(MESSAGE) 
   STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table FMS_SEND_MQI add constraint PK_FMS_SEND_MQI 
 primary key (ID_SYNC_SEND) 
 using index tablespace ${DB_USER}_INDEX
/
alter table FMS_SEND_MQI add constraint FMS_SEND_MQI_VFN_LBA_RBA
 unique (VFN, UDR, LOCALBA_ID, REMOTEBA_ID)
 using index tablespace ${DB_USER}_INDEX
/

create table FTS_RECV_MQI (
   ID number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   CORRELATION_ID varchar2(30),
   VFN varchar2(32) not null,
   FSIZE number(19,0),
   QUEUE_FILENAME varchar2(48),
   GROUP_ID varchar2(48),
   LINE_SEPARATOR varchar2(255),
   RECORD_FORMAT varchar2(255),
   MAX_REC_LEN number(10,0),
   SND_CHAR_TYPE varchar2(255) not null,
   RCV_CHAR_TYPE varchar2(255),
   ADF_LEN number(10,0),
   ADF varchar2(80),
   TUR varchar2(16),
   LOCAL_BA_DATA varchar2(80),
   NET_FILE_SIZE number(19,0) not null,
   TRANSFER_ID varchar2(16) not null,
   FNAME varchar2(1024),
   FILE_DIGEST_ALG varchar2(8),
   FILE_DIGEST_LEN number(19,0),
   FILE_DIGEST varchar2(128),
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   ACCEPT_TMS DATE,
   START_READ_TMS DATE,
   END_READ_TMS DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   HOST_FIRST_SUB_TMS DATE,
   FER_SUB_TIME DATE,
   FEN_SUB_TMS DATE,
   HOST_FIRST_DEL_TMS DATE,
   FIRST_BA_DLV_TMS DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0)
)
   TABLESPACE ${DB_USER}_TABLE
/
alter table FTS_RECV_MQI add constraint PK_FTS_RECV_MQI 
 primary key (ID) 
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_FTS_SEND_MQI start with 1 increment by  1
/
create table FTS_SEND_MQI (
   ID_SEND_FILE number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   SEND_TYPE number(10,0),
   CORRELATION_ID varchar2(30),
   VFN varchar2(32) not null,
   FSIZE number(19,0) not null,
   QUEUE_FILENAME varchar2(48) not null,
   GROUP_ID varchar2(48) not null,
   LINE_SEPARATOR varchar2(255) not null,
   RECORD_FORMAT varchar2(255),
   MAX_REC_LEN number(10,0),
   CHAR_TYPE varchar2(255) not null,
   ADF_LEN number(10,0),
   ADF varchar2(80),
   MESSAGETYPE varchar2(3),
   LOCAL_BA_DATA varchar2(80),
   NET_FILE_SIZE number(19,0),
   TRANSFER_ID varchar2(16),
   FNAME varchar2(1024),
   FILE_DIGEST_ALG varchar2(8),
   FILE_DIGEST_LEN number(19,0),
   FILE_DIGEST varchar2(128),
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   ACCEPTTIME DATE,
   START_CREATE_TIMESTAMP DATE,
   END_CREATE_TIMESTAMP DATE,
   ERROR_TIMESTAMP DATE,
   SENDTIME DATE,
   COMPLETETIME DATE,
   NOTIFYTIME DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   SEND_ERROR_TIMESTAMP DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   NEG_DETAIL_OPER varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0),
   CSW_PRIMITIVE VARCHAR2(20),
   ORIGINAL_PRIMITIVE BLOB
)
   TABLESPACE ${DB_USER}_TABLE
/
alter table FTS_SEND_MQI add constraint PK_FTS_SEND_MQI 
 primary key (ID_SEND_FILE) 
 using index tablespace ${DB_USER}_INDEX
/
alter table FTS_SEND_MQI add constraint FTS_SEND_MQI_VFN_LBA_RBA
 unique (VFN,LOCALBA_ID,REMOTEBA_ID)
 using index tablespace ${DB_USER}_INDEX
/
create table MSS_RECV_MQI (
   ID number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   MSGID varchar2(30) not null,
--       MSGIDR varchar2(30) not null,
   PRIORITY number(10,0) not null,
   UDR_LEN number(10,0),
   UDR varchar2(80),
   TUR varchar2(16),
   MESSAGETYPE varchar2(3) not null,
   CATAPPL varchar2(4),
   MSG_DIGEST_ALG varchar2(8),
   MSG_DIGEST_LEN number(19,0),
   MSG_DIGEST varchar2(128),
   MESSAGE blob,
   MESSAGELEN number(10,0),
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   HOST_FIRST_SUB_TMS DATE,
   FER_SUB_TIME DATE,
   FEN_SUB_TMS DATE,
   HOST_FIRST_DEL_TMS DATE,
   FIRST_BA_DLV_TMS DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0)
)
	TABLESPACE ${DB_USER}_TABLE 
	LOB(MESSAGE) 
	STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table MSS_RECV_MQI add constraint PK_MSS_RECV_MQI 
 primary key (ID) 
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_MSS_SEND_MQI start with 1 increment by  1
/
create table MSS_SEND_MQI (
   FAS_SEQID number(19,0) not null,
   LOCALBA_ID varchar2(12) not null,
   REMOTEBA_ID varchar2(12) not null,
   STATUS varchar2(255),
   CSW_STATUS varchar2(255),
   LOCAL_BA_DATA varchar2(100),
--       MSGID varchar2(30) not null,
   PRIORITY number(10,0) not null,
   TUR varchar2(16),
   MSG_TYPE varchar2(3) not null,
   CAT_APPL varchar2(4),
   CORRELATION_ID varchar2(30),
   UDR_LEN number(10,0),
   UDR varchar2(80),
   MSG_DIGEST_ALG varchar2(8),
   MSG_DIGEST_LEN number(19,0),
   MSG_DIGEST varchar2(128),
   MESSAGELEN number(10,0) not null,
   MESSAGE blob,
   LOCAL_AUTH_INFO_ALG varchar2(8),
   LOCAL_AUTH_INFO_LEN number(19,0),
   LOCAL_AUTH_INFO varchar2(128),
   CSW_INSERT_TIMESTAMP DATE,
   BA_REQ_TMS DATE,
   ACCEPTTIME DATE,
   SENDTIME DATE,
   COMPLETETIME DATE,
   NOTIFYTIME DATE,
   BA_PROCESS_TMS DATE,
   CSW_PROCESS_TMS DATE,
   SEND_ERROR_TIMESTAMP DATE,
   COMPLETE number(10,0),
   PRIMITIVE_ERROR varchar2(50),
   REJECT_REASON number(10,0),
   STATUS_INFO varchar2(1024),
   CSW_RETRY_CNT number(10,0),
   CSW_VERSION number(19,0),
   CSW_PRIMITIVE VARCHAR2(20),
   ORIGINAL_PRIMITIVE BLOB
)
   TABLESPACE ${DB_USER}_TABLE
   LOB(MESSAGE) 
   STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table MSS_SEND_MQI add constraint PK_MSS_SEND_MQI 
 primary key (FAS_SEQID) 
 using index tablespace ${DB_USER}_INDEX
/
alter table MSS_SEND_MQI add constraint MSS_SEND_MQI_UDR_LBA_RBA
 unique (UDR,LOCALBA_ID,REMOTEBA_ID)
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
/
create table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA (
   id number(19,0) not null,
    LOCALBAID varchar2(12),
    REMOTEBAID varchar2(12),
    INTERFACE_TYPE varchar2(255) not null,
    LAU_ENABLED number(1,0) DEFAULT 0,
    LAU_KEY varchar2(255),
    RCV_COMPLETION_ALGO number(1,0) DEFAULT 1,
    SND_COMPLETION_ALGO number(1,0) DEFAULT 1,
    RCV_CODE_PAGE varchar2(255),
    RCV_LINE_SEPARATOR varchar2(255),
    RCV_PATH varchar2(255),
    SND_CODE_PAGE varchar2(255),
    SND_LINE_SEPARATOR varchar2(255),
    SND_MAX_REC_LENGHT number(10,0),
    SND_RECORD_FORMAT varchar2(255),
    MQI_POS_CREATE_IND number(1,0) not null,
    RCV_AUTO_READ number(1,0) not null,
    RCV_PRIM_CONV_FORMAT varchar2(255),
    UPLOAD_Q_NAME varchar2(255),
    HUB_CODE_PAGE VARCHAR2(255),
    HUB_LINE_SEPARATOR VARCHAR2(255),
    RCV_RECORD_FORMAT VARCHAR2(255),
    RCV_MAX_REC_LENGHT NUMBER(10)
)
	TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/


create sequence SEQ_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
/  
create table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA (
   id number(19,0) not null,
    LOCALBAID varchar2(12),
    REMOTEBAID varchar2(12),
    INTERFACE_TYPE varchar2(255) not null,
    LAU_ENABLED number(1,0) DEFAULT 0,
    LAU_KEY varchar2(255),
    RCV_COMPLETION_ALGO number(1,0) DEFAULT 1,
    SND_COMPLETION_ALGO number(1,0) DEFAULT 1,
    RCV_CODE_PAGE varchar2(255),
    RCV_LINE_SEPARATOR varchar2(255),
    RCV_PATH varchar2(255),
    SND_CODE_PAGE varchar2(255),
    SND_LINE_SEPARATOR varchar2(255),
    SND_MAX_REC_LENGHT number(10,0),
    SND_RECORD_FORMAT varchar2(255),
    MQI_POS_CREATE_IND number(1,0) not null,
    RCV_AUTO_READ number(1,0) not null,
    RCV_PRIM_CONV_FORMAT varchar2(255),
    UPLOAD_Q_NAME varchar2(255),
    HUB_CODE_PAGE VARCHAR2(255),
    HUB_LINE_SEPARATOR VARCHAR2(255),
    RCV_RECORD_FORMAT VARCHAR2(255),
    RCV_MAX_REC_LENGHT NUMBER(10)
)
    TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
/  
create table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA (
   id number(19,0) not null,
    LOCALBAID varchar2(12),
    REMOTEBAID varchar2(12),
    INTERFACE_TYPE varchar2(255) not null,
    LAU_ENABLED number(1,0) DEFAULT 0,
    LAU_KEY varchar2(255),
    RCV_COMPLETION_ALGO number(1,0) DEFAULT 1,
    SND_COMPLETION_ALGO number(1,0) DEFAULT 1,
    RCV_PRIM_CONV_FORMAT varchar2(255)
)
	TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA add constraint PK_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA 
 unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
 using index tablespace ${DB_USER}_INDEX
/

create sequence SEQ_GLOBAL_CONFIGURATION start with 10 increment by  1
/
create table GLOBAL_CONFIGURATION (
   id number(19,0) not null,
    PROPERTY varchar2(255) not null,
    VALUE varchar2(3000) not null,
    DISPLAY_NAME varchar2(255),
    MANDATORY number(1,0),
    MAX_LENGTH number(10,0),
    MIN_LENGTH number(10,0),
    REQUIRED_MSG varchar2(255),
    TYPE varchar2(255)
)
	TABLESPACE ${DB_USER}_TABLE
/
alter table GLOBAL_CONFIGURATION add constraint PK_GLOBAL_CONFIGURATION 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/


create sequence SEQ_CONFIGURATION_MQ_PRIMITIVE start with 1 increment by  1
/
create table CONFIGURATION_MQ_PRIMITIVE(
   id number(19,0) not null,
   MQ_CHANNEL varchar2(255) not null,
   PRIMITIVE varchar2(255) not null,
   QUEUE_NAME varchar2(255) not null,
   TO_LOAD number(1,0)
)
   TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_MQ_PRIMITIVE add constraint PK_CONFIGURATION_MQ_PRIMITIVE 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/

create sequence seq_configuration_fems_ws start with 1 increment by  1
/
create table CONFIGURATION_FEM_WS (
   id number(19,0) not null,
   BAID varchar2(12) not null,
   WEB_SERVER_URL varchar2(255),
   WS_SOAP_ACTION varchar2(50)
)
   TABLESPACE ${DB_USER}_TABLE
/
alter table CONFIGURATION_FEM_WS add constraint PK_CONFIGURATION_FEM_WS 
 primary key (id) 
 using index tablespace ${DB_USER}_INDEX
/
alter table CONFIGURATION_FEM_WS add constraint UNIQUE_CONFIGURATION_FEMS_WS 
 unique (BAID)
 using index tablespace ${DB_USER}_INDEX
/


CREATE OR REPLACE TRIGGER TRG_SYNC_SEND_INS_UPD
before insert or update of STCODE_BA,STCODE_SYNC on SYNC_SEND
for each row
begin
	begin if :new.ID_SYNC_SEND is null then SELECT SYNC_SEND_SEQ.NEXTVAL INTO :NEW.ID_SYNC_SEND FROM dual; end if; end;
	begin if((:new.complete is NULL or :new.complete=0) and (:old.STCODE_BA=4 and :old.STCODE_SYNC=15) and (:new.STCODE_BA=7 and :new.STCODE_SYNC=23))
  then
    :new.complete := 1;
  end if;
  end;
end TRG_SYNC_SEND_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_SYNC_RECV_INS_UPD
before insert or update of STCODE_BA,STCODE_SYNC on SYNC_RECV
for each row
begin
	if((:new.complete is NULL or :new.complete=0) and (:old.STCODE_BA=1 and :old.STCODE_SYNC=6) and (:new.STCODE_BA=2 and :new.STCODE_SYNC=15))
	then
		:new.complete := 1;
	end if;
end TRG_SYNC_RECV_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_SEND_FILE_INS_UPD
before insert or update of APPL_CHECK,STSCODE on SEND_FILE
for each row
begin 
        begin if :new.ID_SEND_FILE is null then SELECT SEND_FILE_SEQ.NEXTVAL INTO :NEW.ID_SEND_FILE FROM dual; end if; end;
        begin if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=305)
	then
		:new.complete := 1;
	end if;
	end;
end TRG_SEND_FILE_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_RECV_FILE_INS_UPD
before insert or update of APPL_CHECK,STSCODE on RECV_FILE
for each row
begin
	if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=511)
	then
		:new.complete := 1;
	end if;
end TRG_RECV_FILE_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_FAS_MSG_SEND_INS_UPD
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_SEND
for each row
begin
        begin if :new.FAS_SEQID is null then SELECT FAS_MSG_SEND_SEQ.NEXTVAL INTO :NEW.FAS_SEQID FROM dual; end if; end;
	begin if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=1140)
	then
		:new.complete := 1;
	end if;
	end;
end TRG_FAS_MSG_SEND_UPD;
/
CREATE OR REPLACE TRIGGER TRG_FAS_MSG_RECV_INS_UPD
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_RECV
for each row
begin
	if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=1220)
	then
		:new.complete := 1;
	end if;
end TRG_FAS_MSG_RECV_INS_UPD;
/

