create sequence FAS_MSG_SEND_SEQ start 1 increment 1;
create sequence PRIMITIVE_POOL_SEQ start 1 increment 1;
create sequence SEND_FILE_SEQ start 1 increment 1;
create sequence SEQ_ADD_ON_CONFIGURATION_FTS start 1 increment 1;
create sequence SEQ_CONF_ROUTE_INTERFACE start 1 increment 1;
create sequence SEQ_CONFIGURATION_FEMS_WS start 1 increment 1;
create sequence SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA start 1 increment 1;
create sequence SEQ_CONFIGURATION_FTS_LOCALBA_REMOTEBA start 1 increment 1;
create sequence SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA start 1 increment 1;
create sequence SEQ_CONFIGURATION_RESTFUL start 1 increment 1;
create sequence SEQ_GLOBAL_CONFIGURATION start 1 increment 1;
create sequence SEQ_USERS start 1 increment 1;
create sequence SYNC_SEND_SEQ start 1 increment 1;

    create table ADD_ON_CONFIGURATION_FTS (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        ERRORDELIVER_PREFIX varchar(255),
        ERROR_PREFIX varchar(255),
        RCV_PATH varchar(255),
        SENDING_PREFIX varchar(255),
        SENT_PREFIX varchar(255),
        SND_PATH varchar(255),
        primary key (id)
    );

    create table CONF_ROUTE_INTERFACE (
       id int8 not null,
        INTERFACE varchar(255) not null,
        LOCALBA_ID varchar(12) not null,
        REMOTEBA_ID varchar(12) not null,
        SERVICE varchar(255) not null,
        primary key (id)
    );

    create table CONFIGURATION_FEM_WS (
       id int8 not null,
        BAID varchar(12) not null,
        WEB_SERVER_URL varchar(255),
        WS_SOAP_ACTION varchar(50),
        primary key (id)
    );

    create table CONFIGURATION_FMS_LOCALBA_REMOTEBA (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean,
        SND_COMPLETION_ALGO boolean,
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
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(255),
        primary key (id)
    );

    create table CONFIGURATION_FTS_LOCALBA_REMOTEBA (
       id int8 not null,
        LOCALBAID varchar(12),
        REMOTEBAID varchar(12),
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean,
        SND_COMPLETION_ALGO boolean,
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
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(255),
        primary key (id)
    );

    create table CONFIGURATION_MSS_LOCALBA_REMOTEBA (
       id int8 not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean,
        LAU_KEY varchar(255),
        LOCALBAID varchar(12),
        RCV_COMPLETION_ALGO boolean,
        REMOTEBAID varchar(12),
        SND_COMPLETION_ALGO boolean,
        primary key (id)
    );

    create table CONFIGURATION_RESTFUL (
       id int8 not null,
        BAID varchar(12) not null,
        WEB_SERVER_URL varchar(255),
        primary key (id)
    );

    create table csw_service_registry (
       id varchar(60) not null,
        last_update timestamp,
        group_id varchar(60),
        host_name varchar(80),
        port int4,
        roles varchar(255),
        service_status varchar(30),
        primary key (id)
    );

    create table csw_service_registry_log (
       id varchar(50) not null,
        insert_date timestamp,
        log varchar(1024),
        log_type varchar(30),
        service_registry_id varchar(50),
        primary key (id)
    );

    create table FAS_MSG_RECV (
       SEQID int8 not null,
        APPL_CHECK int4,
        BA_REQ_TIME varchar(255),
        BAR_ACQ_TIME varchar(255),
        CAT_APPL varchar(255),
        CERTF_REQ int4,
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE varchar(255),
        COMPLETE int4,
        CREATEDATE varchar(255),
        CSW_STATUS varchar(60),
        EASSTATUS varchar(255),
        EAS_SUB_TIME varchar(255),
        FEN_DEL_TIME varchar(255),
        FER_DEL_TIME varchar(255),
        FER_SUB_TIME varchar(255),
        FIRST_BAR_SUB_TIME varchar(255),
        LAST_BAR_SUB_TIME varchar(255),
        LASTUPDATE varchar(255),
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCAL_BA varchar(255),
        MSGSIZE int4,
        MSG_TYPE varchar(255),
        MSG_DIGEST varchar(255),
        MSG_DIGEST_ALG varchar(255),
        MSGID varchar(255),
        NET_MSGID varchar(255),
        PRIORITY int4,
        RECV_RNC_TIMESTAMP timestamp,
        RECV_RC_TIMESTAMP timestamp,
        REMOTE_BA varchar(255),
        REMOTE_REF varchar(255),
        STATUS int4,
        STATUS_INFO varchar(255),
        STSCODE int4,
        TUR varchar(255),
        UPDATE_MARK int4,
        MAB oid,
        primary key (SEQID)
    );

    create table FAS_MSG_SEND (
       FAS_SEQID int8 not null,
        APPL_CHECK int4,
        BA_INSERT_TIMESTAMP timestamp,
        BA_REQ_TIME varchar(255),
        BAR_ACQ_TIME varchar(255),
        CAT_APPL varchar(255),
        CERTF_REQ int4,
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE int4,
        COMPLETE int4,
        CREATEDATE varchar(255),
        CSW_STATUS varchar(60),
        EASSTATUS varchar(255),
        FEMSI_RETRY_NUMBER int4,
        FEN_DEL_TIME varchar(255),
        FER_DEL_TIME varchar(255),
        FER_SUB_TIME varchar(255),
        FIRST_EAS_SUB_TIME varchar(255),
        LAST_EAS_SUB_TIME varchar(255),
        LASTUPDATE varchar(255),
        LOAD_TIMESTAMP timestamp,
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCAL_BA varchar(255),
        MSGSIZE int4,
        MSG_TYPE varchar(255),
        MSG_DIGEST varchar(255),
        MSG_DIGEST_ALG varchar(255),
        MSGID varchar(255),
        NET_MSGID varchar(255),
        PRIORITY int4,
        REMOTE_BA varchar(255),
        REMOTE_REF varchar(255),
        CSW_RETRY_CNT int4,
        SEND_CNF_TIMESTAMP timestamp,
        SEND_ERR_TIMESTAMP timestamp,
        SEND_REQ_TIMESTAMP timestamp,
        SEND_SC_TIMESTAMP timestamp,
        SEQID int4,
        STATUS int4,
        STATUS_INFO varchar(255),
        STSCODE int4,
        TUR varchar(255),
        UPDATE_MARK int4,
        MAB oid,
        primary key (FAS_SEQID)
    );

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
    );

    create table PRIMITIVE_POOL (
       ID int8 not null,
        MESSAGE oid,
        PRIMITIVE_ID varchar(255),
        SERVICE_TYPE varchar(255),
        primary key (ID)
    );

    create table RECV_FILE (
       FAS_SEQID int8 not null,
        ACT_REQ_TIME varchar(255),
        APPL_CHECK int4,
        APPLICATIVE_DATA_FIELD varchar(255),
        BA_PROC_TIME varchar(255),
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE int4,
        COMPLETE int4,
        CREATEDATE varchar(255),
        CSW_STATUS varchar(60),
        EAS_COMPL_TIME varchar(255),
        EAS_ELAB_TIME varchar(255),
        EAS_ACQ_TIME varchar(255),
        EASSTATUS varchar(255),
        FBLKMOVED int4,
        FILE_DIGEST varchar(255),
        FILE_DIGEST_ALG varchar(255),
        FMD5 varchar(255),
        FILEMAP varchar(255),
        FNAME varchar(255),
        FSIZE int8,
        FIRST_DEL_TIME varchar(255),
        FIRST_READ_TIME varchar(255),
        FTS_INTERFACE varchar(255),
        LAST_DEL_TIME varchar(255),
        LAST_READ_TIME varchar(255),
        LASTUPDATE varchar(255),
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCAL_AUTH_INFO_FS varchar(255),
        LOCAL_BA varchar(255),
        ORIGINAL_FNAME varchar(255),
        QUEUE_INS_TIME varchar(255),
        RECV_REQUEST_TIMESTAMP timestamp,
        RECV_COMPLETE_TIMESTAMP timestamp,
        RECV_DELIVERED_TIMESTAMP timestamp,
        RECV_ERROR_TIMESTAMP timestamp,
        REF_DATE varchar(255),
        REMOTE_BA varchar(255),
        RETRY_CNT int4,
        START_TIME varchar(255),
        STATUS int4,
        STATUS_INFO varchar(255),
        STSCODE int4,
        TRANSP_TYPE varchar(255),
        UPDATE_MARK int4,
        VFN varchar(255),
        primary key (FAS_SEQID)
    );

    create table SEND_FILE (
       ID_SEND_FILE int8 not null,
        ACT_REQ_TIME varchar(255),
        APPL_CHECK int4,
        APPLICATIVE_DATA_FIELD varchar(255),
        APPLICATIVE_DATA_FIELD_LENGTH int4,
        BA_INSERT_TIMESTAMP timestamp,
        BA_PROC_TIME varchar(255),
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE int4,
        COMPLETE int4,
        CREATEDATE varchar(255),
        CSW_STATUS varchar(60),
        EAS_COMPL_TIME varchar(255),
        EAS_ELAB_TIME varchar(255),
        EAS_ACQ_TIME varchar(255),
        EASSTATUS varchar(255),
        FBLKMOVED int4,
        FILE_DIGEST varchar(255),
        FILE_DIGEST_ALG varchar(255),
        FMD5 varchar(255),
        FILEMAP varchar(255),
        FNAME varchar(255),
        FSIZE int8,
        FTS_INTERFACE varchar(255),
        LASTUPDATE varchar(255),
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCAL_AUTH_INFO_FS varchar(255),
        LOCAL_BA varchar(255),
        OPERATION varchar(255),
        OPERATION_TIMESTAMP timestamp,
        ORIGINAL_FNAME varchar(255),
        QUEUE_INS_TIME varchar(255),
        REACTIVATE varchar(255),
        REF_DATE varchar(255),
        REMOTE_BA varchar(255),
        RETRY_CNT int4,
        SEND_ACCEPTED_TIMESTAMP timestamp,
        SEND_COMPLETED_TIMESTAMP timestamp,
        SEND_CONFIRMED_TIMESTAMP timestamp,
        SEND_ERROR_TIMESTAMP timestamp,
        SEND_GFT_REQUEST_TIMESTAMP timestamp,
        SEND_REQUEST_TIMESTAMP timestamp,
        START_TIME varchar(255),
        STATUS int4,
        STATUS_INFO varchar(255),
        STSCODE int4,
        TRANSP_TYPE varchar(255),
        UPDATE_MARK int4,
        VFN varchar(255),
        primary key (ID_SEND_FILE)
    );

    create table SYNC_RECV (
       FAS_SEQID int8 not null,
        BAMSGID varchar(255),
        BA_PROCESSED_TMP timestamp,
        CAT_APPLY varchar(255),
        CERTF_REQ int4,
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE int4,
        COMPLETE int4,
        COMPRESSION_ALGO varchar(255),
        CRT_DLV_MSS_TMP timestamp,
        CRT_SUB_MSS_TMP timestamp,
        CSW_STATUS varchar(60),
        DATASETNAME varchar(255),
        FBLOCKMOVED int4,
        FMAP varchar(255),
        FEN_DLV_MSS_TMP timestamp,
        FEN_SUB_MSS_TMP timestamp,
        FER_DLV_FST_FTS_TMP timestamp,
        FER_DLV_LST_FTS_TMP timestamp,
        FER_SUB_FST_FTS_TMP timestamp,
        FER_SUB_LST_FTS_TMP timestamp,
        FILE_DIGEST varchar(255),
        FILE_DIGEST_ALG varchar(255),
        FILE_FORMAT varchar(255),
        FHASH varchar(255),
        FNAME varchar(255),
        FSIZE int8,
        FTSDELIVTIME timestamp,
        FTSINSERTTIME timestamp,
        LAST_UPDATE int4,
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCALBA_ID varchar(255),
        LOCAL_DATA varchar(255),
        MESSAGELENGTH int4,
        MESSAGETYPE varchar(255),
        MODTIME timestamp,
        MSCONFTIME timestamp,
        MSRECVTIME timestamp,
        MSG_DIGEST varchar(255),
        MSG_DIGEST_ALG varchar(255),
        PRIORITY int4,
        READ_COMPLETED_TIMESTAMP timestamp,
        READ_CONFIRMED_TIMESTAMP timestamp,
        READ_END_FTS_TMP timestamp,
        READ_ERROR_TIMESTAMP timestamp,
        READ_NOTIFIED_TIMESTAMP timestamp,
        READ_REQUEST_TIMESTAMP timestamp,
        READ_STR_FTS_TMP timestamp,
        RECEIVETIME timestamp,
        FTSRECVTIME timestamp,
        REMOTEBA_ID varchar(255),
        START_RECV_GMT timestamp,
        STATUS_BA int4,
        STCODE_BA int4,
        STCODE_SYNC int4,
        STATUS_INFO varchar(255),
        STATUS_SYNC varchar(255),
        TIMEOUT int4,
        TUR varchar(255),
        USERDATAREMOTE varchar(255),
        VFN varchar(255),
        MESSAGE oid,
        primary key (FAS_SEQID)
    );

    create table SYNC_SEND (
       ID_SYNC_SEND int8 not null,
        ACCEPTTIME timestamp,
        BA_INSERT_TIMESTAMP timestamp,
        BAMSG_ID varchar(255),
        CATAPPL varchar(255),
        CERTF_REQ int4,
        CLEANUP_LOT int4,
        CLEANUP_STATUS varchar(255),
        CLEANUP_TYPE int4,
        COMPLETE int4,
        CRT_DLV_MSS_TMP timestamp,
        CRT_SUB_MSS_TMP timestamp,
        CSW_STATUS varchar(60),
        DATASETNAME varchar(255),
        ERRORTIME timestamp,
        FBLOCKMOVED int4,
        FMAP varchar(255),
        FEN_DLV_MSS_TMP timestamp,
        FEN_SUB_MSS_TMP timestamp,
        FER_DLV_FST_FTS_TMP timestamp,
        FER_DLV_LST_FTS_TMP timestamp,
        FER_SUB_FST_FTS_TMP timestamp,
        FER_SUB_LST_FTS_TMP timestamp,
        FILE_DIGEST varchar(255),
        FILE_DIGEST_ALG varchar(255),
        FHASH varchar(255),
        FNAME varchar(255),
        FSIZE int8,
        FTSCOMPLETETIME timestamp,
        FTSSENDTIME timestamp,
        LAST_UPDATE int4,
        LOCAL_AUTH_INFO varchar(255),
        LOCAL_AUTH_INFO_ALG varchar(255),
        LOCALBA_ID varchar(255),
        LOCAL_DATA varchar(255),
        MESSAGELEN int4,
        MESSAGETYPE varchar(255),
        MODTIME timestamp,
        MSCOMPLETETIME timestamp,
        MSSENDTIME timestamp,
        MSG_DIGEST varchar(255),
        MSG_DIGEST_ALG varchar(255),
        NOTIFYTIME timestamp,
        OPERATION varchar(255),
        OPERATION_TIMESTAMP timestamp,
        PRIORITY int4,
        REACTIVATE varchar(255),
        REMOTEBA_ID varchar(255),
        RETRY_CNT int4,
        SEND_ACCEPTED_TIMESTAMP timestamp,
        SEND_COMPLETED_TIMESTAMP timestamp,
        SEND_CONFIRMED_TIMESTAMP timestamp,
        SEND_ERROR_TIMESTAMP timestamp,
        SEND_REQUEST_TIMESTAMP timestamp,
        STATUS_BA int4,
        STCODE_BA int4,
        STCODE_SYNC int4,
        STATUS_INFO varchar(255),
        STATUS_SYNC varchar(255),
        TUR varchar(255),
        USERDATAREMOTE varchar(255),
        USERDATAREMOTELEN int4,
        VFN varchar(255),
        MESSAGE oid,
        primary key (ID_SYNC_SEND)
    );

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
    );

    alter table if exists ADD_ON_CONFIGURATION_FTS 
       add constraint UNIQUE_ADD_ON_CONFIGURATION_FTS unique (LOCALBAID, REMOTEBAID);

    alter table if exists CONF_ROUTE_INTERFACE 
       add constraint UNIQUE_CONF_ROUTE_INTERFACE unique (LOCALBA_ID, REMOTEBA_ID, INTERFACE, SERVICE);

    alter table if exists CONFIGURATION_FEM_WS 
       add constraint UNIQUE_CONFIGURATION_FEMWS unique (BAID);

    alter table if exists CONFIGURATION_FMS_LOCALBA_REMOTEBA 
       add constraint UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID);

    alter table if exists CONFIGURATION_FTS_LOCALBA_REMOTEBA 
       add constraint UNIQUE_CONFIGURATION_FTS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID);

    alter table if exists CONFIGURATION_MSS_LOCALBA_REMOTEBA 
       add constraint UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID);

    alter table if exists CONFIGURATION_RESTFUL 
       add constraint UNIQUE_CONFIGURATION_RESTFUL unique (BAID);

    alter table if exists USERS 
       add constraint UNIQUE_USER unique (username);
