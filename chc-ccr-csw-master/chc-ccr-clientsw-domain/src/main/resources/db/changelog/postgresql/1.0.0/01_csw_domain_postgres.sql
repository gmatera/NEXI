-- Variazioni alla tebelle di retrocompatibilita FTS_INTERFACE


ALTER TABLE SEND_FILE ADD CSW_STATUS VARCHAR(60);
ALTER TABLE SEND_FILE ADD CSW_VERSION int8 DEFAULT 0;    
ALTER TABLE SEND_FILE ADD FTS_INTERFACE VARCHAR(2) DEFAULT 'DB';
create sequence SEND_FILE_SEQ start with 1 increment by  1;

ALTER TABLE RECV_FILE ADD FTS_INTERFACE VARCHAR(2) DEFAULT 'DB';
ALTER TABLE RECV_FILE ADD CSW_STATUS VARCHAR(60);

ALTER TABLE SYNC_SEND ADD CSW_STATUS VARCHAR(60);
ALTER TABLE SYNC_SEND ADD CSW_VERSION int8 DEFAULT 0;
create sequence SYNC_SEND_SEQ start with 1 increment by  1;


ALTER TABLE SYNC_RECV ADD CSW_STATUS VARCHAR(60);

ALTER TABLE FAS_MSG_SEND ADD CSW_STATUS VARCHAR(60);
ALTER TABLE FAS_MSG_SEND ADD CSW_VERSION int8 DEFAULT 0;
ALTER TABLE FAS_MSG_SEND ADD CSW_RETRY_CNT int4 DEFAULT 0;
create sequence FAS_MSG_SEND_SEQ start with 1 increment by  1;

ALTER TABLE FAS_MSG_RECV ADD CSW_STATUS VARCHAR(60);

-- *************************************************************************
-- conf_route_interface
-- *************************************************************************
create sequence SEQ_CONF_ROUTE_INTERFACE start with 1 increment by  1
;
create table CONF_ROUTE_INTERFACE (
       id int8 not null,
        INTERFACE varchar(255) not null,
        LOCALBA_ID varchar(12) not null,
        REMOTEBA_ID varchar(12) not null,
        SERVICE varchar(255) not null,
        primary key (id)
    )
;
alter table CONF_ROUTE_INTERFACE add constraint UNIQUE_CONF_ROUTE_INTERFACE unique (LOCALBA_ID, REMOTEBA_ID, INTERFACE, SERVICE)
;
insert into conf_route_interface (id, interface, localba_id, remoteba_id, service) values (nextval('SEQ_CONF_ROUTE_INTERFACE'), 'DB', '88507NCB1300', '88508NCB1300', 'FMS')
;
insert into conf_route_interface (id, interface, localba_id, remoteba_id, service) values (nextval('SEQ_CONF_ROUTE_INTERFACE'), 'DB', '88508NCB1300', '88507NCB1300', 'FMS')
;
-- *************************************************************************
-- add_on_configuration_fts
-- *************************************************************************
create sequence SEQ_ADD_ON_CONFIGURATION_FTS start with 1 increment by  1
;
create table ADD_ON_CONFIGURATION_FTS (
       id int8 not null,
        LOCALBAID varchar(12) not null,
        REMOTEBAID varchar(12) not null,
        ERRORDELIVER_PREFIX varchar(255),
        ERROR_PREFIX varchar(255),
        RCV_PATH varchar(255),
        SENDING_PREFIX varchar(255),
        SENT_PREFIX varchar(255),
        SND_PATH varchar(255),
        primary key (id)
    )
;
alter table ADD_ON_CONFIGURATION_FTS add constraint UNIQUE_ADD_ON_CONFIGURATION_FTS unique (LOCALBAID, REMOTEBAID)
;
alter table ADD_ON_CONFIGURATION_FTS add constraint SND_PATH_CONSTRAIN unique (SND_PATH)
;
----------------- TODODODODOD alter table ADD_ON_CONFIGURATION_FTS add constraint RCV_PATH_CONSTRAIN unique (RCV_PATH)


-- *************************************************************************
-- configuration_fms_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA start with 1 increment by  1
;
 create table CONFIGURATION_FMS_LOCALBA_REMOTEBA (
       id int8 not null,

       	LOCALBAID varchar(12),
		REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        RCV_CODE_PAGE varchar(255),
        RCV_COMPLETION_ALGO boolean default true,
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT int4,
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO boolean default true,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        LAU_KEY varchar(255),
        HUB_CODE_PAGE varchar(255),
 		HUB_LINE_SEPARATOR varchar(255),
        primary key (id)
    )
;
alter table CONFIGURATION_FMS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;
-- *************************************************************************
-- configuration_fts_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_FTS_LOCALBA_REMOTEBA start with 1 increment by  1
;
create table CONFIGURATION_FTS_LOCALBA_REMOTEBA (
       id int8 not null,

       	LOCALBAID varchar(12),
		REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        RCV_CODE_PAGE varchar(255),
        RCV_COMPLETION_ALGO boolean default true,
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT int4,
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO boolean default true,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        LAU_KEY varchar(255),
        HUB_CODE_PAGE varchar(255),
  		HUB_LINE_SEPARATOR varchar(255),
        primary key (id)
    )
;
alter table CONFIGURATION_FTS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FTS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;
-- *************************************************************************
-- configuration_mss_localba_remoteba
-- *************************************************************************
create sequence SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA start with 1 increment by  1
;
 create table CONFIGURATION_MSS_LOCALBA_REMOTEBA (
       id int8 not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        LAU_KEY varchar(255),
        LOCALBAID varchar(12),
        RCV_COMPLETION_ALGO boolean default true,
        REMOTEBAID varchar(12),
        SND_COMPLETION_ALGO boolean default true,
        primary key (id)
    )
;
alter table CONFIGURATION_MSS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;
create table csw_service_registry (
   id varchar(60) not null,
    last_update timestamp,
    group_id varchar(60),
    host_name varchar(80),
    port int4,
    roles varchar(255),
    service_status varchar(30),
    primary key (id)
)
;
create table csw_service_registry_log (
   id varchar(50) not null,
    insert_date timestamp,
    log varchar(1024),
    log_type varchar(30),
    service_registry_id varchar(50),
    primary key (id)
)
;
-- *************************************************************************
-- USER TABLE (Password is admin)
-- *************************************************************************

create sequence SEQ_USERS start with 2 increment by  1
;
create table USERS (
       id int8 not null,
        full_Name varchar(255),
        password varchar(255),
        roles varchar(300),
        SECRET_ANSWER_ONE varchar(255),
        SECRET_ANSWER_TWO varchar(255),
        SECRET_RESPONSE_ONE varchar(255),
        SECRET_RESPONSE_TWO varchar(255),
        username varchar(255),
        primary key (id)
    )
;
alter table USERS add constraint UNIQUE_USER unique (USERNAME)
;
create table FMS_RECV_MQI (
       ID int8 not null,
        MESSAGE bytea,
        ACCEPT_TMS timestamp,
        ADF varchar(80),
        ADF_LEN int4,
        BA_PROCESS_TMS timestamp,
        CATAPPL varchar(4),
        COMPLETE int4,
        CORRELATION_ID varchar(30),
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        END_READ_TMS timestamp,
        FEN_SUB_TMS timestamp,
        FER_SUB_TIME timestamp,
        FILE_DIGEST varchar(128),
        FILE_DIGEST_ALG varchar(8),
        FILE_DIGEST_LEN int8,
        FNAME varchar(1024),
        FSIZE int8 not null,
        FIRST_BA_DLV_TMS timestamp,
        GROUP_ID varchar(48),
        HOST_FIRST_DEL_TMS timestamp,
        HOST_FIRST_SUB_TMS timestamp,
        LINE_SEPARATOR varchar(255),
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCAL_BA_DATA varchar(80),
        LOCALBA_ID varchar(12) not null,
        MAX_REC_LEN int4,
        MESSAGELEN int4 not null,
        MESSAGETYPE varchar(3) not null,
        MSG_DIGEST varchar(128),
        MSG_DIGEST_ALG varchar(8),
        MSG_DIGEST_LEN int8,
        NET_FILE_SIZE int8,
        PRIMITIVE_ERROR varchar(50),
        QUEUE_FILENAME varchar(48),
        RCV_CHAR_TYPE varchar(255),
        RECORD_FORMAT varchar(255),
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4,
        SND_CHAR_TYPE varchar(255) not null,
        START_READ_TMS timestamp,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TRANSFER_ID varchar(16) not null,
        TUR varchar(16),
        UDR varchar(80) not null,
        UDR_LEN int4 not null,
        CSW_VERSION int8,
        VFN varchar(32) not null,
        primary key (ID)
    )
;
create sequence SEQ_FMS_SEND_MQI start with 1 increment by  1
;
  create table FMS_SEND_MQI (
       ID_SYNC_SEND int8 not null,
        MESSAGE bytea,
        ACCEPTTIME timestamp,
        ADF varchar(80),
        ADF_LEN int4,
        BA_PROCESS_TMS timestamp,
        CATAPPL varchar(4),
        CHAR_TYPE varchar(255) not null,
        COMPLETE int4,
        COMPLETETIME timestamp,
        CORRELATION_ID varchar(30),
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        END_CREATE_TIMESTAMP timestamp,
        ERROR_TIMESTAMP timestamp,
        FILE_DIGEST varchar(128),
        FILE_DIGEST_ALG varchar(8),
        FILE_DIGEST_LEN int8,
        FNAME varchar(1024),
        FSIZE int8 not null,
        GROUP_ID varchar(48) not null,
        LINE_SEPARATOR varchar(255) not null,
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCAL_BA_DATA varchar(80),
        LOCALBA_ID varchar(12) not null,
        MAX_REC_LEN int4,
        MESSAGELEN int4 not null,
        MESSAGETYPE varchar(3) not null,
        MSG_DIGEST varchar(128),
        MSG_DIGEST_ALG varchar(8),
        MSG_DIGEST_LEN int8,
        NEG_DETAIL_OPER varchar(1024),
        NET_FILE_SIZE int8,
        NOTIFYTIME timestamp,
        ORIGINAL_PRIMITIVE bytea,
        CSW_PRIMITIVE VARCHAR(20),
        PRIMITIVE_ERROR varchar(50),
        QUEUE_FILENAME varchar(48) not null,
        RECORD_FORMAT varchar(255),
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4 default 0,
        SEND_ERROR_TIMESTAMP timestamp,
        SENDTIME timestamp,
        SEND_TYPE int4,
        START_CREATE_TIMESTAMP timestamp,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TRANSFER_ID varchar(16),
        TUR varchar(16),
        UDR varchar(80) not null,
        UDR_LEN int4 not null,
        CSW_VERSION int8 default 0,
        VFN varchar(32) not null,
        primary key (ID_SYNC_SEND)
    )
;
alter table FMS_SEND_MQI add constraint FMS_SEND_MQI_VFN_LBA_RBA unique (VFN, UDR, LOCALBA_ID, REMOTEBA_ID)
;
    create table FTS_RECV_MQI (
       ID int8 not null,
        ACCEPT_TMS timestamp,
        ADF varchar(80),
        ADF_LEN int4,
        BA_PROCESS_TMS timestamp,
        COMPLETE int4,
        CORRELATION_ID varchar(30),
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        END_READ_TMS timestamp,
        FEN_SUB_TMS timestamp,
        FER_SUB_TIME timestamp,
        FILE_DIGEST varchar(128),
        FILE_DIGEST_ALG varchar(8),
        FILE_DIGEST_LEN int8,
        FNAME varchar(1024),
        FSIZE int8,
        FIRST_BA_DLV_TMS timestamp,
        GROUP_ID varchar(48),
        HOST_FIRST_DEL_TMS timestamp,
        HOST_FIRST_SUB_TMS timestamp,
        LINE_SEPARATOR varchar(255),
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCAL_BA_DATA varchar(80),
        LOCALBA_ID varchar(12) not null,
        MAX_REC_LEN int4,
        NET_FILE_SIZE int8 not null,
        PRIMITIVE_ERROR varchar(50),
        QUEUE_FILENAME varchar(48),
        RCV_CHAR_TYPE varchar(255),
        RECORD_FORMAT varchar(255),
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4 default 0,
        SND_CHAR_TYPE varchar(255) not null,
        START_READ_TMS timestamp,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TRANSFER_ID varchar(16) not null,
        TUR varchar(16),
        CSW_VERSION int8 default 0,
        VFN varchar(32) not null,
        primary key (ID)
    )
;
create sequence SEQ_FTS_SEND_MQI start with 1 increment by  1
;
   create table FTS_SEND_MQI (
       ID_SEND_FILE int8 not null,
        ACCEPTTIME timestamp,
        ADF varchar(80),
        ADF_LEN int4,
        BA_PROCESS_TMS timestamp,
        CHAR_TYPE varchar(255) not null,
        COMPLETE int4,
        COMPLETETIME timestamp,
        CORRELATION_ID varchar(30),
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        END_CREATE_TIMESTAMP timestamp,
        ERROR_TIMESTAMP timestamp,
        FILE_DIGEST varchar(128),
        FILE_DIGEST_ALG varchar(8),
        FILE_DIGEST_LEN int8,
        FNAME varchar(1024),
        FSIZE int8 not null,
        GROUP_ID varchar(48) not null,
        LINE_SEPARATOR varchar(255) not null,
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCAL_BA_DATA varchar(80),
        LOCALBA_ID varchar(12) not null,
        MAX_REC_LEN int4,
        MESSAGETYPE varchar(3),
        NEG_DETAIL_OPER varchar(1024),
        NET_FILE_SIZE int8,
        NOTIFYTIME timestamp,
        ORIGINAL_PRIMITIVE bytea,
        CSW_PRIMITIVE VARCHAR(20),
        PRIMITIVE_ERROR varchar(50),
        QUEUE_FILENAME varchar(48) not null,
        RECORD_FORMAT varchar(255),
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4 default 0,
        SEND_ERROR_TIMESTAMP timestamp,
        SENDTIME timestamp,
        SEND_TYPE int4,
        START_CREATE_TIMESTAMP timestamp,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TRANSFER_ID varchar(16),
        CSW_VERSION int8 default 0,
        VFN varchar(32) not null,
        primary key (ID_SEND_FILE)
    )
;
alter table FTS_SEND_MQI add constraint FTS_SEND_MQI_VFN_LBA_RBA unique (VFN,LOCALBA_ID,REMOTEBA_ID)
;
     create table MSS_RECV_MQI (
       ID int8 not null,
        MESSAGE bytea,
        BA_PROCESS_TMS timestamp,
        CATAPPL varchar(4),
        COMPLETE int4,
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        FEN_SUB_TMS timestamp,
        FER_SUB_TIME timestamp,
        FIRST_BA_DLV_TMS timestamp,
        HOST_FIRST_DEL_TMS timestamp,
        HOST_FIRST_SUB_TMS timestamp,
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCALBA_ID varchar(12) not null,
        MESSAGELEN int4 not null,
        MESSAGETYPE varchar(3) not null,
        MSG_DIGEST varchar(128),
        MSG_DIGEST_ALG varchar(8),
        MSG_DIGEST_LEN int8,
        MSGID varchar(30) not null,
        PRIMITIVE_ERROR varchar(50),
        PRIORITY int4 not null,
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4 default 0,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TUR varchar(16),
        UDR varchar(80),
        UDR_LEN int4,
        CSW_VERSION int8 default 0,
        primary key (ID)
    )
;
create sequence SEQ_MSS_SEND_MQI start with 1 increment by  1
;
    create table MSS_SEND_MQI (
       FAS_SEQID int8 not null,
        MESSAGE bytea,
        ACCEPTTIME timestamp,
        BA_PROCESS_TMS timestamp,
        BA_REQ_TMS timestamp,
        CAT_APPL varchar(4),
        COMPLETE int4,
        COMPLETETIME timestamp,
        CORRELATION_ID varchar(30),
        CSW_INSERT_TIMESTAMP timestamp,
        CSW_PROCESS_TMS timestamp,
        CSW_STATUS varchar(60),
        SEND_ERROR_TIMESTAMP timestamp,
        LOCAL_AUTH_INFO varchar(128),
        LOCAL_AUTH_INFO_ALG varchar(8),
        LOCAL_AUTH_INFO_LEN int8,
        LOCAL_BA_DATA varchar(100),
        LOCALBA_ID varchar(12) not null,
        MESSAGELEN int4 not null,
        MSG_TYPE varchar(3) not null,
        MSG_DIGEST varchar(128),
        MSG_DIGEST_ALG varchar(8),
        MSG_DIGEST_LEN int8,
        NOTIFYTIME timestamp,
        ORIGINAL_PRIMITIVE bytea,
        CSW_PRIMITIVE VARCHAR(20),
        PRIMITIVE_ERROR varchar(50),
        PRIORITY int4 not null,
        REJECT_REASON int4,
        REMOTEBA_ID varchar(12) not null,
        CSW_RETRY_CNT int4 default 0,
        SENDTIME timestamp,
        STATUS varchar(255),
        STATUS_INFO varchar(1024),
        TUR varchar(16),
        UDR varchar(80),
        UDR_LEN int4,
        CSW_VERSION int8 default 0,
        primary key (FAS_SEQID)
    )
;
alter table MSS_SEND_MQI add constraint MSS_SEND_MQI_UDR_LBA_RBA unique (UDR,LOCALBA_ID,REMOTEBA_ID)
;

create sequence SEQ_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
;

 create table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean default true,
        SND_COMPLETION_ALGO boolean default true,
        HUB_CODE_PAGE varchar(255),
        HUB_LINE_SEPARATOR varchar(255),
        RCV_CODE_PAGE varchar(255),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_MAX_REC_LENGHT int4,
        RCV_PATH varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_RECORD_FORMAT varchar(255),
        MQI_POS_CREATE_IND boolean,
        RCV_AUTO_READ boolean,
        RCV_PRIM_CONV_FORMAT varchar(255),
        UPLOAD_Q_NAME varchar(255),
        primary key (id)
    )
;
alter table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;
create sequence SEQ_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
;
  create table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean,
        SND_COMPLETION_ALGO boolean default true,
        HUB_CODE_PAGE varchar(255),
        HUB_LINE_SEPARATOR varchar(255),
        RCV_CODE_PAGE varchar(255),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_MAX_REC_LENGHT int4,
        RCV_PATH varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_RECORD_FORMAT varchar(255),
        MQI_POS_CREATE_IND boolean,
        RCV_AUTO_READ boolean,
        RCV_PRIM_CONV_FORMAT varchar(255),
        UPLOAD_Q_NAME varchar(255),
        primary key (id)
    )
;
alter table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;
create sequence SEQ_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA start with 1 increment by  1
;
create table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean default false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean default true,
        SND_COMPLETION_ALGO boolean default true,
        RCV_PRIM_CONV_FORMAT varchar(255),
        primary key (id)
    )
;
alter table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
;

create sequence SEQ_GLOBAL_CONFIGURATION start with 1 increment by  1
;
create table GLOBAL_CONFIGURATION (
       id int8 not null,
        DISPLAY_NAME varchar(255),
        MANDATORY boolean,
        MAX_LENGTH int4,
        MIN_LENGTH int4,
        PROPERTY varchar(255),
        REQUIRED_MSG varchar(255),
        TYPE varchar(255),
        VALUE varchar(3000),
        primary key (id)
    )
;
create sequence SEQ_CONFIGURATION_MQ_PRIMITIVE start with 10 increment by  1
;
 create table CONFIGURATION_MQ_PRIMITIVE (
       id int8 not null,
        MQ_CHANNEL varchar(255),
        PRIMITIVE varchar(255),
        QUEUE_NAME varchar(255),
        TO_LOAD boolean,
        primary key (id)
    )
;
create sequence SEQ_CONFIGURATION_FEMS_WS start with 1 increment by  1
;
create table CONFIGURATION_FEM_WS (
       id int8 not null,
        BAID varchar(12) not null,
        WEB_SERVER_URL varchar(255),
        WS_SOAP_ACTION varchar(50),
        primary key (id)
    )
;

alter table CONFIGURATION_FEM_WS add constraint UNIQUE_CONFIGURATION_FEMS_WS unique (BAID)
;
