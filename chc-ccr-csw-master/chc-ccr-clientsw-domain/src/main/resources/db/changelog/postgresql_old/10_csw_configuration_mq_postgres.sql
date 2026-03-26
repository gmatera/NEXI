create table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA (
       id bigint not null,
        LOCALBAID varchar(12) not null,
        REMOTEBAID varchar(12) not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean DEFAULT false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean DEFAULT true,
        SND_COMPLETION_ALGO boolean DEFAULT true,
        RCV_CODE_PAGE varchar(255),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_RECORD_FORMAT varchar(255),
        MQI_POS_CREATE_IND boolean not null,
        RCV_AUTO_READ boolean not null,
        RCV_PRIM_CONV_FORMAT varchar(255),
        UPLOAD_Q_NAME varchar(255),
        primary key (id)
    )
/
 create table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA (
       id bigint not null,
        LOCALBAID varchar(12) not null,
        REMOTEBAID varchar(12) not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean DEFAULT false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean DEFAULT true,
        SND_COMPLETION_ALGO boolean DEFAULT true,
        RCV_CODE_PAGE varchar(255),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_PATH varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_RECORD_FORMAT varchar(255),
        MQI_POS_CREATE_IND boolean not null,
        RCV_AUTO_READ boolean not null,
        RCV_PRIM_CONV_FORMAT varchar(255),
        UPLOAD_Q_NAME varchar(255),
        primary key (id)
    )
/
create table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA (
       id bigint not null,
        LOCALBAID varchar(12) not null,
        REMOTEBAID varchar(12) not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean DEFAULT false,
        LAU_KEY varchar(255),
        RCV_COMPLETION_ALGO boolean DEFAULT true,
        SND_COMPLETION_ALGO boolean DEFAULT true,
        RCV_PRIM_CONV_FORMAT varchar(255),
        primary key (id)
    )
/
