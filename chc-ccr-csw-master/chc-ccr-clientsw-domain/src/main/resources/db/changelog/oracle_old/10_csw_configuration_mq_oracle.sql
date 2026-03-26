create table CONFIGURATION_FMS_MQ_LOCALBA_REMOTEBA (
       id number(19,0) not null,
        LOCALBAID varchar2(12) not null,
        REMOTEBAID varchar2(12) not null,
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
        primary key (id)
    )
/
 create table CONFIGURATION_FTS_MQ_LOCALBA_REMOTEBA (
       id number(19,0) not null,
        LOCALBAID varchar2(12) not null,
        REMOTEBAID varchar2(12) not null,
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
        primary key (id)
    )
/
create table CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA (
       id number(19,0) not null,
        LOCALBAID varchar2(12) not null,
        REMOTEBAID varchar2(12) not null,
        INTERFACE_TYPE varchar2(255) not null,
        LAU_ENABLED number(1,0) DEFAULT 0,
        LAU_KEY varchar2(255),
        RCV_COMPLETION_ALGO number(1,0) DEFAULT 1,
        SND_COMPLETION_ALGO number(1,0) DEFAULT 1,
        RCV_PRIM_CONV_FORMAT varchar2(255),
        primary key (id)
    )
/
