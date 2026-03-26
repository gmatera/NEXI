-- *************************************************************************
-- Sync_send
-- *************************************************************************
CREATE TABLE SYNC_SEND
(
	LOCALBA_ID			VARCHAR2(12) NOT NULL,
	REMOTEBA_ID			VARCHAR2(12) NOT NULL,
	VFN					VARCHAR2(32),
	STATUS_BA			VARCHAR2(60) DEFAULT 'SUBMITTED',
	STATUS_SYNC			VARCHAR2(60),
	STCODE_BA			NUMBER(10) DEFAULT 0  NOT NULL,
	STCODE_SYNC			NUMBER(10) DEFAULT 0  NOT NULL,
	FNAME			VARCHAR2(1024)  NOT NULL,
	RETRY_CNT			NUMBER(10) DEFAULT 0,
	USERDATAREMOTE			VARCHAR2(80) NOT NULL,
	PRIORITY			NUMBER(10) DEFAULT 0,
	BAMSG_ID			VARCHAR2(30),
	MESSAGE			BLOB,
	ACCEPTTIME			DATE,
	FTSSENDTIME			DATE,
	FTSCOMPLETETIME			DATE,
	MSSENDTIME			DATE,
	MSCOMPLETETIME			DATE,
	NOTIFYTIME			DATE,
	MODTIME			DATE DEFAULT sys_extract_utc(systimestamp) NOT NULL,
	USERDATAREMOTELEN			NUMBER(10),
	TUR			VARCHAR2(16),
	MESSAGELEN			NUMBER(10),
	MESSAGETYPE			VARCHAR2(3) NOT NULL,
	CATAPPL			VARCHAR2(4),
	ERRORTIME			DATE,
	FER_SUB_FST_FTS_TMP			DATE,
	FER_SUB_LST_FTS_TMP			DATE,
	FER_DLV_FST_FTS_TMP			DATE,
	FER_DLV_LST_FTS_TMP			DATE,
	FEN_SUB_MSS_TMP			DATE,
	FEN_DLV_MSS_TMP			DATE,
	CRT_SUB_MSS_TMP			DATE,
	CRT_DLV_MSS_TMP			DATE,
	DATASETNAME			VARCHAR2(255),
	LOCAL_DATA			VARCHAR2(255),
	CERTF_REQ			NUMBER(10),
	FSIZE			NUMBER(19),
	FHASH			VARCHAR2(32),
	FBLOCKMOVED			NUMBER(10),
	FMAP			VARCHAR2(256),
	COMPLETE			NUMBER(10) DEFAULT 0,
	LAST_UPDATE			NUMBER(10),
	BA_INSERT_TIMESTAMP			DATE DEFAULT sys_extract_utc(systimestamp),
	SEND_ACCEPTED_TIMESTAMP			DATE,
	SEND_REQUEST_TIMESTAMP			DATE,
	SEND_CONFIRMED_TIMESTAMP			DATE,
	SEND_COMPLETED_TIMESTAMP			DATE,
	SEND_ERROR_TIMESTAMP			DATE,
	STATUS_INFO			VARCHAR2(4000),
	ID_SYNC_SEND			NUMBER(19),
	OPERATION_TIMESTAMP			DATE,
	OPERATION			VARCHAR2(10),
	REACTIVATE			VARCHAR2(10),
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	FILE_DIGEST_ALG			VARCHAR2(8 CHAR),
	FILE_DIGEST			VARCHAR2(128 CHAR),
	MSG_DIGEST_ALG			VARCHAR2(8 CHAR),
	MSG_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60),
    CSW_VERSION NUMBER(19) DEFAULT 0
)
	TABLESPACE ${DB_USER}_TABLE 
	LOB(MESSAGE) 
	STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/

alter table SYNC_SEND add constraint PK_SYNC_SEND
primary key (ID_SYNC_SEND)
using index tablespace ${DB_USER}_INDEX
/
alter table SYNC_SEND add constraint UK_SYNC_SEND_VFN
unique (VFN)
using index tablespace ${DB_USER}_INDEX
/
alter table SYNC_SEND add constraint SYNC_SEND_UDR_LBA_RBA
unique (USERDATAREMOTE,LOCALBA_ID,REMOTEBA_ID)
/
alter table SYNC_SEND add constraint CK_SYNC_SEND_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table SYNC_SEND add constraint CK_SYNC_SEND_CLEANUP_TYPE
check (cleanup_type in('IRL_FMS_SND'))
/
alter table SYNC_SEND add constraint CK_SYNC_SEND_FDIGEST_ALG
check ((FILE_DIGEST_ALG IS NULL AND FILE_DIGEST IS NULL) OR (FILE_DIGEST_ALG IS NOT NULL AND FILE_DIGEST IS NOT NULL AND FILE_DIGEST_ALG in('SHA-256')))
/
alter table SYNC_SEND add constraint CK_SYNC_SEND_MDIGEST_ALG
check ((MSG_DIGEST_ALG IS NULL AND MSG_DIGEST IS NULL) OR (MSG_DIGEST_ALG IS NOT NULL AND MSG_DIGEST IS NOT NULL AND MSG_DIGEST_ALG in('SHA-256')))
/
alter table SYNC_SEND add constraint CK_SYNC_SEND_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL) OR (LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_SYNC_SEND_1 on  SYNC_SEND(STCODE_BA,STCODE_SYNC,ID_SYNC_SEND)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SYNC_SEND_2 on  SYNC_SEND(COMPLETE,STCODE_BA,STCODE_SYNC,LAST_UPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SYNC_SEND_3 on  SYNC_SEND(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
CREATE SEQUENCE SYNC_SEND_SEQ
 START WITH     1
 INCREMENT BY   1
 NOCACHE
 NOCYCLE
/
--CREATE OR REPLACE TRIGGER TRG_SYNC_SEND_INS_UPD
--before insert or update of STCODE_BA,STCODE_SYNC on SYNC_SEND
--for each row
--declare
--	audit_enabled varchar2(100);
--	oldStcode     varchar2(30);
--begin
	-- set identity
	--begin if :new.ID_SYNC_SEND is null then SELECT SYNC_SEND_SEQ.NEXTVAL INTO :NEW.ID_SYNC_SEND FROM dual; end if; end;
--end TRG_SYNC_SEND_INS_UPD;
-- TRIGGER --
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
-- *************************************************************************
-- Sync_recv
-- *************************************************************************
CREATE TABLE SYNC_RECV
(
	LOCALBA_ID			VARCHAR2(12) NOT NULL,
	REMOTEBA_ID			VARCHAR2(12) NOT NULL,
	VFN			VARCHAR2(32) NOT NULL,
	STATUS_BA			VARCHAR2(60),
	STATUS_SYNC			VARCHAR2(60),
	STCODE_BA			NUMBER(10) DEFAULT 0,
	STCODE_SYNC			NUMBER(10) DEFAULT 0,
	TIMEOUT			NUMBER(10),
	FTSRECVTIME			DATE,
	MSRECVTIME			DATE,
	FNAME			VARCHAR2(1024) NOT NULL,
	USERDATAREMOTE			VARCHAR2(80),
	PRIORITY			NUMBER(10),
	MESSAGETYPE			VARCHAR2(3),
	MODTIME			DATE,
	MESSAGE			BLOB,
	MESSAGELENGTH			NUMBER(10),
	CAT_APPLY			VARCHAR2(4),
	TUR			VARCHAR2(16),
	BAMSGID			VARCHAR2(30),
	RECEIVETIME			DATE,
	FTSINSERTTIME			DATE,
	MSCONFTIME			DATE,
	FTSDELIVTIME			DATE,
	BA_PROCESSED_TMP			DATE,
	DATASETNAME			VARCHAR2(255),
	FER_SUB_FST_FTS_TMP			DATE,
	FER_SUB_LST_FTS_TMP			DATE,
	FER_DLV_FST_FTS_TMP			DATE,
	FER_DLV_LST_FTS_TMP			DATE,
	FEN_SUB_MSS_TMP			DATE,
	FEN_DLV_MSS_TMP			DATE,
	CRT_SUB_MSS_TMP			DATE,
	CRT_DLV_MSS_TMP			DATE,
	READ_STR_FTS_TMP			DATE,
	READ_END_FTS_TMP			DATE,
	LOCAL_DATA			VARCHAR2(255),
	CERTF_REQ			NUMBER(10),
	START_RECV_GMT			DATE,
	FSIZE			INTEGER,
	FHASH			VARCHAR2(32),
	FBLOCKMOVED			NUMBER(10),
	FMAP			VARCHAR2(256),
	COMPLETE			NUMBER(10),
	LAST_UPDATE			NUMBER(10),
	READ_REQUEST_TIMESTAMP			DATE,
	READ_CONFIRMED_TIMESTAMP			DATE,
	READ_COMPLETED_TIMESTAMP			DATE,
	READ_NOTIFIED_TIMESTAMP			DATE,
	READ_ERROR_TIMESTAMP			DATE,
	STATUS_INFO			VARCHAR2(4000),
	FILE_FORMAT			VARCHAR2(32),
	COMPRESSION_ALGO			VARCHAR2(32),
	FAS_SEQID			NUMBER(19) NOT NULL,
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	FILE_DIGEST_ALG			VARCHAR2(8 CHAR),
	FILE_DIGEST			VARCHAR2(128 CHAR),
	MSG_DIGEST_ALG			VARCHAR2(8 CHAR),
	MSG_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60)
)
	TABLESPACE ${DB_USER}_TABLE
	LOB(MESSAGE)
	STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table SYNC_RECV add constraint PK_SYNC_RECV
primary key (FAS_SEQID)
using index tablespace ${DB_USER}_INDEX
/
alter table SYNC_RECV add constraint UK_SYNCRECVLOCALBAIDREMOTEB_1
unique (VFN,LOCALBA_ID,REMOTEBA_ID)
using index tablespace ${DB_USER}_INDEX
/
alter table SYNC_RECV add constraint CK_SYNC_RECV_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table SYNC_RECV add constraint CK_SYNC_RECV_CLEANUP_TYPE
check (cleanup_type in('IRL_FMS_RCV'))
/
alter table SYNC_RECV add constraint CK_SYNC_RECV_FDIGEST_ALG
check ((FILE_DIGEST_ALG IS NULL AND FILE_DIGEST IS NULL) OR (FILE_DIGEST_ALG IS NOT NULL AND FILE_DIGEST IS NOT NULL AND FILE_DIGEST_ALG in('SHA-256')))
/
alter table SYNC_RECV add constraint CK_SYNC_RECV_MDIGEST_ALG
check ((MSG_DIGEST_ALG IS NULL AND MSG_DIGEST IS NULL) OR (MSG_DIGEST_ALG IS NOT NULL AND MSG_DIGEST IS NOT NULL AND MSG_DIGEST_ALG in('SHA-256')))
/
alter table SYNC_RECV add constraint CK_SYNC_RECV_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL) OR (LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_SYNC_RECV_1 on  SYNC_RECV(STCODE_BA,STCODE_SYNC)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SYNC_RECV_2 on  SYNC_RECV(COMPLETE,STCODE_BA,STCODE_SYNC,LAST_UPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SYNC_RECV_3 on  SYNC_RECV(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
create sequence SYNC_RECV_SEQ start with 1 increment by  1
/
-- TRIGGER --
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
-- *************************************************************************
-- Send_file
-- *************************************************************************
CREATE TABLE SEND_FILE
(
	VFN			VARCHAR2(32) NOT NULL,
	FNAME			VARCHAR2(1024),
	LOCAL_BA			VARCHAR2(12) NOT NULL,
	REMOTE_BA			VARCHAR2(12) NOT NULL,
	STATUS			VARCHAR2(60) DEFAULT 'FILE TO BE PROCESSED' NOT NULL,
	EASSTATUS			VARCHAR2(3),
	STSCODE			NUMBER(10) DEFAULT 90 NOT NULL,
	COMPLETE			NUMBER(10) DEFAULT 0 NOT NULL,
	TRANSP_TYPE			VARCHAR2(8) DEFAULT 'EAS' NOT NULL,
	CREATEDATE			VARCHAR2(24),
	LASTUPDATE			VARCHAR2(24),
	APPL_CHECK			NUMBER(10) DEFAULT 0 NOT NULL,
	ORIGINAL_FNAME			VARCHAR2(1024),
	RETRY_CNT			NUMBER(10) DEFAULT 0,
	UPDATE_MARK			NUMBER(10),
	FSIZE			NUMBER(10),
	FMD5			VARCHAR2(32),
	FBLKMOVED			NUMBER(10),
	FILEMAP			VARCHAR2(255),
	OPERATION_TIMESTAMP			DATE,
	OPERATION			VARCHAR2(10),
	REACTIVATE			VARCHAR2(10),
	REF_DATE			VARCHAR2(6),
	ACT_REQ_TIME			VARCHAR2(12),
	EAS_ACQ_TIME			VARCHAR2(17),
	QUEUE_INS_TIME			VARCHAR2(17),
	START_TIME			VARCHAR2(17),
	EAS_COMPL_TIME			VARCHAR2(17),
	BA_PROC_TIME			VARCHAR2(12),
	EAS_ELAB_TIME			VARCHAR2(17),
	BA_INSERT_TIMESTAMP			DATE DEFAULT sys_extract_utc(systimestamp),
	SEND_ACCEPTED_TIMESTAMP			DATE,
	SEND_GFT_REQUEST_TIMESTAMP			DATE,
	SEND_REQUEST_TIMESTAMP			DATE,
	SEND_CONFIRMED_TIMESTAMP			DATE,
	SEND_COMPLETED_TIMESTAMP			DATE,
	SEND_ERROR_TIMESTAMP			DATE,
	STATUS_INFO			VARCHAR2(4000),
	ID_SEND_FILE			NUMBER(19) NOT NULL,
	APPLICATIVE_DATA_FIELD			VARCHAR2(80),
	APPLICATIVE_DATA_FIELD_LENGTH			NUMBER(10),
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	FILE_DIGEST_ALG			VARCHAR2(8 CHAR),
	FILE_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_FS			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60),
    CSW_VERSION NUMBER(19) DEFAULT 0,
    FTS_INTERFACE VARCHAR2(2) DEFAULT 'DB'
)
	TABLESPACE ${DB_USER}_TABLE
/
alter table SEND_FILE add constraint PK_SEND_FILE
primary key (ID_SEND_FILE)
using index tablespace ${DB_USER}_INDEX
/
alter table SEND_FILE add constraint UK_SEND_FILE_VFN
unique (VFN)
using index tablespace ${DB_USER}_INDEX
/
alter table SEND_FILE add constraint CK_SEND_FILE_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table SEND_FILE add constraint CK_SEND_FILE_CLEANUP_TYPE
check (cleanup_type in('IRL_FTS_SND'))
/
alter table SEND_FILE add constraint CK_SEND_FILE_FDIGEST_ALG
check ((FILE_DIGEST_ALG IS NULL AND FILE_DIGEST IS NULL) OR (FILE_DIGEST_ALG IS NOT NULL AND FILE_DIGEST IS NOT NULL AND FILE_DIGEST_ALG in('SHA-256')))
/
alter table SEND_FILE add constraint CK_SEND_FILE_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL AND LOCAL_AUTH_INFO_FS IS NULL)
	OR (LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_FS IS NULL AND LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256'))
	OR (LOCAL_AUTH_INFO IS NULL AND LOCAL_AUTH_INFO_FS IS NOT NULL AND LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_SEND_FILE_AB on  SEND_FILE(APPL_CHECK,STSCODE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SEND_FILE_1 on  SEND_FILE(STSCODE,ID_SEND_FILE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SEND_FILE_2 on  SEND_FILE(COMPLETE,STSCODE,LASTUPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_SEND_FILE_3 on  SEND_FILE(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
CREATE SEQUENCE SEND_FILE_SEQ
 START WITH     1
 INCREMENT BY   1
 NOCACHE
 NOCYCLE
/
--CREATE OR REPLACE TRIGGER TRG_SEND_FILE_INS_UPD
--before insert or update of STSCODE on SEND_FILE
--for each row
--declare
--	audit_enabled varchar2(100);
--	oldStcode     varchar2(30);
--begin
	-- set identity
	--begin if :new.ID_SEND_FILE is null then SELECT SEND_FILE_SEQ.NEXTVAL INTO :NEW.ID_SEND_FILE FROM dual; end if; end;
--end TRG_SEND_FILE_INS_UPD;
-- TRIGGER --
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
-- *************************************************************************
-- Recv_file
-- *************************************************************************
CREATE TABLE RECV_FILE
(
	VFN			VARCHAR2(32) NOT NULL,
	FNAME			VARCHAR2(1024),
	LOCAL_BA			VARCHAR2(12) NOT NULL,
	REMOTE_BA			VARCHAR2(12) NOT NULL,
	STATUS			VARCHAR2(60) NOT NULL,
	EASSTATUS			VARCHAR2(3),
	STSCODE			NUMBER(10) NOT NULL,
	COMPLETE			NUMBER(10) NOT NULL,
	TRANSP_TYPE			VARCHAR2(8) NOT NULL,
	CREATEDATE			VARCHAR2(24),
	LASTUPDATE			VARCHAR2(24),
	APPL_CHECK			NUMBER(10) NOT NULL,
	ORIGINAL_FNAME			VARCHAR2(1024),
	RETRY_CNT			NUMBER(10),
	UPDATE_MARK			NUMBER(10),
	FSIZE			NUMBER(10),
	FMD5			VARCHAR2(32),
	FBLKMOVED			NUMBER(10),
	FILEMAP			VARCHAR2(255),
	REF_DATE			VARCHAR2(6),
	ACT_REQ_TIME			VARCHAR2(12),
	EAS_ACQ_TIME			VARCHAR2(17),
	QUEUE_INS_TIME			VARCHAR2(17),
	START_TIME			VARCHAR2(17),
	EAS_COMPL_TIME			VARCHAR2(17),
	FIRST_DEL_TIME			VARCHAR2(17),
	LAST_DEL_TIME			VARCHAR2(17),
	FIRST_READ_TIME			VARCHAR2(17),
	LAST_READ_TIME			VARCHAR2(17),
	BA_PROC_TIME			VARCHAR2(12),
	EAS_ELAB_TIME			VARCHAR2(17),
	RECV_REQUEST_TIMESTAMP			DATE,
	RECV_ERROR_TIMESTAMP			DATE,
	RECV_COMPLETE_TIMESTAMP			DATE,
	RECV_DELIVERED_TIMESTAMP			DATE,
	FAS_SEQID			NUMBER(19) NOT NULL,
	STATUS_INFO			VARCHAR2(4000),
	APPLICATIVE_DATA_FIELD			VARCHAR2(80),
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	FILE_DIGEST_ALG			VARCHAR2(8 CHAR),
	FILE_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_FS			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60),
    FTS_INTERFACE VARCHAR2(2) DEFAULT 'DB'
)
	TABLESPACE ${DB_USER}_TABLE
/
alter table RECV_FILE add constraint PK_RECV_FILE
primary key (FAS_SEQID)
using index tablespace ${DB_USER}_INDEX
/
alter table RECV_FILE add constraint UK_RECVFILEVFNLOCALBAREMOTEBA
unique (VFN,LOCAL_BA,REMOTE_BA)
using index tablespace ${DB_USER}_INDEX
/
alter table RECV_FILE add constraint CK_RECV_FILE_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table RECV_FILE add constraint CK_RECV_FILE_CLEANUP_TYPE
check (cleanup_type in('IRL_FTS_RCV'))
/
alter table RECV_FILE add constraint CK_RECV_FILE_FDIGEST_ALG
check ((FILE_DIGEST_ALG IS NULL AND FILE_DIGEST IS NULL) OR (FILE_DIGEST_ALG IS NOT NULL AND FILE_DIGEST IS NOT NULL AND FILE_DIGEST_ALG in('SHA-256')))
/
alter table RECV_FILE add constraint CK_RECV_FILE_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL AND LOCAL_AUTH_INFO_FS IS NULL)
	OR (LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_FS IS NULL AND LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256'))
	OR (LOCAL_AUTH_INFO IS NULL AND LOCAL_AUTH_INFO_FS IS NOT NULL AND LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_RECV_FILE_AB on  RECV_FILE(APPL_CHECK,STSCODE,LOCAL_BA)
 tablespace ${DB_USER}_INDEX
/
create index IDX_RECV_FILE_1 on  RECV_FILE(STSCODE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_RECV_FILE_2 on  RECV_FILE(COMPLETE,STSCODE,LASTUPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_RECV_FILE_3 on  RECV_FILE(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
create sequence RECV_FILE_SEQ start with 1 increment by  1
/
-- TRIGGER --
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
-- *************************************************************************
-- Fas_msg_send
-- *************************************************************************
CREATE TABLE FAS_MSG_SEND
(
	LOCAL_BA			VARCHAR2(12) NOT NULL,
	REMOTE_BA			VARCHAR2(12) NOT NULL,
	MSG_TYPE			VARCHAR2(3),
	CAT_APPL			VARCHAR2(4),
	TUR			VARCHAR2(16),
	MSGID			VARCHAR2(30) NOT NULL,
	REMOTE_REF			VARCHAR2(80),
	PRIORITY			NUMBER(10) DEFAULT 0,
	CERTF_REQ			NUMBER(10),
	STSCODE			NUMBER(10) DEFAULT 0 NOT NULL,
	STATUS			VARCHAR2(256) DEFAULT 'NEW TRAFFIC',
	EASSTATUS			VARCHAR2(3),
	COMPLETE			NUMBER(10) DEFAULT 0,
	APPL_CHECK			NUMBER(10) DEFAULT 0 NOT NULL,
	CREATEDATE			VARCHAR2(24),
	LASTUPDATE			VARCHAR2(24),
	UPDATE_MARK			NUMBER(10),
	SEQID			NUMBER(19),
	MSGSIZE			NUMBER(10),
	MAB			BLOB,
	BA_REQ_TIME			VARCHAR2(17),
	BAR_ACQ_TIME			VARCHAR2(17),
	FEMSI_RETRY_NUMBER			NUMBER(10),
	FIRST_EAS_SUB_TIME			VARCHAR2(17),
	LAST_EAS_SUB_TIME			VARCHAR2(17),
	FER_SUB_TIME			VARCHAR2(17),
	FEN_DEL_TIME			VARCHAR2(17),
	FER_DEL_TIME			VARCHAR2(17),
	BA_INSERT_TIMESTAMP			DATE DEFAULT sys_extract_utc(systimestamp),
	LOAD_TIMESTAMP			DATE,
	SEND_REQ_TIMESTAMP			DATE,
	SEND_ERR_TIMESTAMP			DATE,
	SEND_CNF_TIMESTAMP			DATE,
	SEND_SC_TIMESTAMP			DATE,
	STATUS_INFO			VARCHAR2(4000),
	FAS_SEQID			NUMBER(19) NOT NULL,
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	NET_MSGID			VARCHAR2(16),
	MSG_DIGEST_ALG			VARCHAR2(8 CHAR),
	MSG_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60),
    CSW_VERSION NUMBER(19) DEFAULT 0,
    CSW_RETRY_CNT NUMBER(10) DEFAULT 0
)
	TABLESPACE ${DB_USER}_TABLE
	LOB(MAB)
	STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/ 
alter table FAS_MSG_SEND add constraint PK_FAS_MSG_SEND
primary key (FAS_SEQID)
using index tablespace ${DB_USER}_INDEX
/
alter table FAS_MSG_SEND add constraint UK_FASMSGSENDR6CUFASMSGSEND
unique (LOCAL_BA,MSGID)
using index tablespace ${DB_USER}_INDEX
/
alter table FAS_MSG_SEND add constraint CK_FAS_MSG_SEND_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table FAS_MSG_SEND add constraint CK_FAS_MSG_SEND_CLEANUP_TYPE
check (cleanup_type in('IRL_MSS_SND'))
/
alter table FAS_MSG_SEND add constraint CK_FAS_MSG_SEND_MDIGEST_ALG
check ((MSG_DIGEST_ALG IS NULL AND MSG_DIGEST IS NULL) OR (MSG_DIGEST_ALG IS NOT NULL AND MSG_DIGEST IS NOT NULL AND MSG_DIGEST_ALG in('SHA-256')))
/
alter table FAS_MSG_SEND add constraint CK_FAS_MSG_SEND_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL) OR (LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_FAS_MSG_SEND_AB on  FAS_MSG_SEND(APPL_CHECK,STSCODE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_SEND_1 on  FAS_MSG_SEND(STSCODE,FAS_SEQID)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_SEND_2 on  FAS_MSG_SEND(COMPLETE,STSCODE,LASTUPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_SEND_3 on  FAS_MSG_SEND(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
CREATE SEQUENCE FAS_MSG_SEND_SEQ
 START WITH     1
 INCREMENT BY   1
 NOCACHE
 NOCYCLE
/
--CREATE OR REPLACE TRIGGER TRG_FAS_MSG_SEND_INS_UPD
--before insert or update of STSCODE on FAS_MSG_SEND
--for each row
--declare
--	audit_enabled varchar2(100);
--	oldStcode     varchar2(30);
--begin
	-- set identity
	--begin if :new.FAS_SEQID is null then SELECT FAS_MSG_SEND_SEQ.NEXTVAL INTO :NEW.FAS_SEQID FROM dual; end if; end;
--end TRG_FAS_MSG_SEND_INS_UPD;
-- TRIGGER --
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
-- *************************************************************************
-- Fas_msg_recv
-- *************************************************************************
CREATE TABLE FAS_MSG_RECV
(
	LOCAL_BA			VARCHAR2(12) NOT NULL,
	REMOTE_BA			VARCHAR2(12) NOT NULL,
	MSG_TYPE			VARCHAR2(3),
	CAT_APPL			VARCHAR2(4),
	TUR			VARCHAR2(16),
	MSGID			VARCHAR2(30) NOT NULL,
	REMOTE_REF			VARCHAR2(80),
	PRIORITY			NUMBER(10),
	CERTF_REQ			NUMBER(10),
	STSCODE			NUMBER(10) NOT NULL,
	STATUS			VARCHAR2(60),
	EASSTATUS			VARCHAR2(3),
	COMPLETE			NUMBER(10),
	APPL_CHECK			NUMBER(10) NOT NULL,
	CREATEDATE			VARCHAR2(24),
	LASTUPDATE			VARCHAR2(24),
	UPDATE_MARK			NUMBER(10),
	SEQID			NUMBER(19) NOT NULL,
	MSGSIZE			NUMBER(10),
	MAB			BLOB,
	BA_REQ_TIME			VARCHAR2(17),
	BAR_ACQ_TIME			VARCHAR2(17),
	EAS_SUB_TIME			VARCHAR2(17),
	FER_SUB_TIME			VARCHAR2(17),
	FEN_DEL_TIME			VARCHAR2(17),
	FER_DEL_TIME			VARCHAR2(17),
	FIRST_BAR_SUB_TIME			VARCHAR2(17),
	LAST_BAR_SUB_TIME			VARCHAR2(17),
	RECV_RNC_TIMESTAMP			DATE,
	RECV_RC_TIMESTAMP			DATE,
	STATUS_INFO			VARCHAR2(4000),
	CLEANUP_STATUS			VARCHAR2(20) DEFAULT 'FREE' NOT NULL,
	CLEANUP_LOT			NUMBER(10),
	CLEANUP_TYPE			VARCHAR2(20),
	NET_MSGID			VARCHAR2(16),
	MSG_DIGEST_ALG			VARCHAR2(8 CHAR),
	MSG_DIGEST			VARCHAR2(128 CHAR),
	LOCAL_AUTH_INFO_ALG			VARCHAR2(8 CHAR),
	LOCAL_AUTH_INFO			VARCHAR2(128 CHAR),
    CSW_STATUS VARCHAR2(60)
)
	TABLESPACE ${DB_USER}_TABLE
	LOB(MAB)
	STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/
alter table FAS_MSG_RECV add constraint PK_FAS_MSG_RECV
primary key (SEQID)
using index tablespace ${DB_USER}_INDEX
/
alter table FAS_MSG_RECV add constraint UK_FASMSGRECVR6CUFASMSGRECV
unique (LOCAL_BA,MSGID)
using index tablespace ${DB_USER}_INDEX
/
alter table FAS_MSG_RECV add constraint CK_FAS_MSG_RECV_CLEANUP_STATUS
check (cleanup_status in('FREE','MARKED','EXPORTED','PARTIAL_DELETED','DELETABLE'))
/
alter table FAS_MSG_RECV add constraint CK_FAS_MSG_RECV_CLEANUP_TYPE
check (cleanup_type in('IRL_MSS_RCV'))
/
alter table FAS_MSG_RECV add constraint CK_FAS_MSG_RECV_MDIGEST_ALG
check ((MSG_DIGEST_ALG IS NULL AND MSG_DIGEST IS NULL) OR (MSG_DIGEST_ALG IS NOT NULL AND MSG_DIGEST IS NOT NULL AND MSG_DIGEST_ALG in('SHA-256')))
/
alter table FAS_MSG_RECV add constraint CK_FAS_MSG_RECV_LAU_ALG
check ((LOCAL_AUTH_INFO_ALG IS NULL AND LOCAL_AUTH_INFO IS NULL) OR (LOCAL_AUTH_INFO_ALG IS NOT NULL AND LOCAL_AUTH_INFO IS NOT NULL AND LOCAL_AUTH_INFO_ALG in('HS256')))
/
create index IDX_FAS_MSG_RECV_AB on  FAS_MSG_RECV(APPL_CHECK,STSCODE,LOCAL_BA)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_RECV_1 on  FAS_MSG_RECV(STSCODE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_RECV_2 on  FAS_MSG_RECV(COMPLETE,STSCODE,LASTUPDATE)
 tablespace ${DB_USER}_INDEX
/
create index IDX_FAS_MSG_RECV_3 on  FAS_MSG_RECV(CLEANUP_STATUS,CLEANUP_TYPE,CLEANUP_LOT)
 tablespace ${DB_USER}_INDEX
/
create sequence FAS_MSG_RECV_SEQ start with 1 increment by  1
/
-- TRIGGER --
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
-- *************************************************************************
-- VI_FMS_RCV
-- *************************************************************************
CREATE OR REPLACE VIEW VI_FMS_RCV
(LOCALBA_ID,REMOTEBA_ID,VFN,USERDATAREMOTE,STCODE_BA,STCODE_SYNC,STATUS_BA,STATUS_INFO,COMPLETE,FNAME,FSIZE,FHASH,TUR,MESSAGETYPE,CAT_APPLY,MESSAGELENGTH,MESSAGE,MI_BEG_SND_GW,GW_BEG_SND_GW_MS,GW_END_SND_GW_MS,GW_BEG_SND_MI_MS,GW_END_SND_MI_MS,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,MI_END_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,MSG_DIGEST_ALG,MSG_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    rf.localba_id,
    rf.remoteba_id,
    rf.vfn,
    rf.userdataremote,
    rf.stcode_ba,
    rf.stcode_sync,
    rf.status_ba,
    rf.status_info,
    rf.complete,
    rf.fname,
    rf.fsize,
    rf.fhash,
    rf.tur,
    rf.messagetype,
    rf.cat_apply,
    rf.messagelength,
    rf.message,
    cast(cast(rf.ftsdelivtime as date) as timestamp) as mi_beg_snd_gw,
    cast(cast(rf.crt_sub_mss_tmp as date) as timestamp) as gw_beg_snd_gw_ms,
    cast(cast(rf.fen_sub_mss_tmp as date) as timestamp) as gw_end_snd_gw_ms,
    cast(cast(rf.fen_dlv_mss_tmp as date) as timestamp) as gw_beg_snd_mi_ms,
    cast(cast(rf.crt_dlv_mss_tmp as date) as timestamp) as gw_end_snd_mi_ms,
    cast(cast(rf.fer_sub_fst_fts_tmp as date) as timestamp) as gw_beg_snd_gw,
    cast(cast(rf.fer_sub_lst_fts_tmp as date) as timestamp) as gw_end_snd_gw,
    cast(cast(rf.fer_dlv_fst_fts_tmp as date) as timestamp) as gw_beg_snd_mi, 
    cast(cast(rf.fer_dlv_lst_fts_tmp as date) as timestamp) as gw_end_snd_mi,
    cast(cast(rf.msrecvtime as date) as timestamp) as mi_beg_rcv_gw,
    cast(cast(rf.receivetime as date) as timestamp) as mi_end_rcv_gw,
    rf.file_digest_alg,
    rf.file_digest,
    rf.msg_digest_alg,
    rf.msg_digest,
    rf.local_auth_info_alg,
    rf.local_auth_info
from
    SYNC_RECV rf
/
-- *************************************************************************
-- VI_FMS_SND
-- *************************************************************************
--
CREATE OR REPLACE VIEW VI_FMS_SND
(LOCALBA_ID,REMOTEBA_ID,VFN,USERDATAREMOTE,STCODE_BA,STCODE_SYNC,STATUS_BA,STATUS_INFO,SEND_ERROR_TIMESTAMP,COMPLETE,FNAME,FSIZE,FHASH,TUR,MESSAGETYPE,CATAPPL,MESSAGELEN,MESSAGE,BA_INSERT_TIMESTAMP,MI_ACCEPT_BA,MI_BEG_SND_GW,GW_BEG_SND_GW_MS,GW_END_SND_GW_MS,GW_BEG_SND_MI_MS,GW_END_SND_MI_MS,MI_END_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,MSG_DIGEST_ALG,MSG_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    sf.localba_id,
    sf.remoteba_id,
    sf.vfn,
    sf.userdataremote,
    sf.stcode_ba,
    sf.stcode_sync,
    sf.status_ba,
    sf.status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(sf.send_error_timestamp as date) as timestamp) as send_error_timestamp,
    sf.complete,
    sf.fname,
    sf.fsize,
    sf.fhash,
    sf.tur,
    sf.messagetype,
    sf.catappl,
    sf.messagelen,
    sf.message,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(sf.ba_insert_timestamp as date) as timestamp) as ba_insert_timestamp,
    cast(cast(sf.accepttime as date) as timestamp) as mi_accept_ba,
    cast(cast(sf.ftssendtime as date) as timestamp) as mi_beg_snd_gw,
    cast(cast(sf.crt_sub_mss_tmp as date) as timestamp) as gw_beg_snd_gw_ms,
    cast(cast(sf.fen_sub_mss_tmp as date) as timestamp) as gw_end_snd_gw_ms,
    cast(cast(sf.fen_dlv_mss_tmp as date) as timestamp) as gw_beg_snd_mi_ms,
    cast(cast(sf.crt_dlv_mss_tmp as date) as timestamp) as gw_end_snd_mi_ms,
    cast(cast(sf.ftscompletetime as date) as timestamp) as mi_end_snd_gw,
    cast(cast(sf.fer_sub_fst_fts_tmp as date) as timestamp) as gw_beg_snd_gw,
    cast(cast(sf.fer_sub_lst_fts_tmp as date) as timestamp) as gw_end_snd_gw,
    cast(cast(sf.send_request_timestamp as date) as timestamp) as gw_beg_snd_mi, 
    cast(cast(sf.send_completed_timestamp as date) as timestamp) as gw_end_snd_mi,
    cast(cast(sf.notifytime as date) as timestamp) as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.msg_digest_alg,
    sf.msg_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SYNC_SEND sf
    --per estrarre i circuiti SIAnet
--left outer join VCC_ABAB_SIANET ab on (ab.service='FT' and ab.local_ba=sf.localba_id and ab.remote_ba=sf.remoteba_id and ab.with_msg=1)
    -- per estrarre tipo interfaccia
--left outer join VCL_FT cft on (sf.localba_id=cft.local_ba and sf.remoteba_id=cft.remote_ba and cft.with_msg=1)
/
-- *************************************************************************
-- VI_FTS_RCV
-- *************************************************************************
CREATE OR REPLACE VIEW VI_FTS_RCV
(LOCAL_BA,REMOTE_BA,VFN,STSCODE,STATUS,STATUS_INFO,APPL_CHECK,COMPLETE,FNAME,APPLICATIVE_DATA_FIELD,FSIZE,FMD5,MI_BEG_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,MI_END_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    rf.local_ba,
    rf.remote_ba,
    rf.vfn,
    rf.stscode,
    rf.status,
    rf.status_info,
    rf.appl_check,
    rf.complete,
    rf.fname,
    rf.applicative_data_field,
    rf.fsize,
    rf.fmd5,
    --start_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(rf.start_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_beg_snd_gw,
    --first_del_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(rf.first_del_time, 0,12),'YYMMDDHH24MISS') as timestamp) as gw_beg_snd_gw,
    --last_del_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(rf.last_del_time, 0,12),'YYMMDDHH24MISS') as timestamp) as gw_end_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(recv_request_timestamp as date) as timestamp) as gw_beg_snd_mi,
    cast(cast(recv_complete_timestamp as date) as timestamp) as gw_end_snd_mi,
    cast(cast(recv_delivered_timestamp as date) as timestamp) as mi_beg_rcv_gw,
    --eas_compl_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(rf.eas_compl_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_end_rcv_gw,
    rf.file_digest_alg,
    rf.file_digest,
    rf.local_auth_info_alg,
    rf.local_auth_info
from
    RECV_FILE rf
/
-- *************************************************************************
-- VI_FTS_SND
-- *************************************************************************
CREATE OR REPLACE VIEW VI_FTS_SND
(LOCAL_BA,REMOTE_BA,VFN,STSCODE,STATUS,STATUS_INFO,SEND_ERROR_TIMESTAMP,APPL_CHECK,COMPLETE,FNAME,APPLICATIVE_DATA_FIELD,FSIZE,FMD5,BA_INSERT_TIMESTAMP,MI_ACCEPT_BA,MI_BEG_SND_GW,MI_END_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    sf.local_ba,
    sf.remote_ba,
    sf.vfn,
    sf.stscode,
    sf.status,
    sf.status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(sf.send_error_timestamp as date) as timestamp) as send_error_timestamp,
    sf.appl_check,
    sf.complete,
    sf.fname,
    sf.applicative_data_field,
    sf.fsize,
    sf.fmd5,
    cast(cast(sf.ba_insert_timestamp as date) as timestamp) as ba_insert_timestamp,
    cast(to_date(sf.createdate,'YYYY/MM/DD HH24:MI:SS') as timestamp) as mi_accept_ba,
    -- start_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(sf.start_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_beg_snd_gw,
    -- eas_compl_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(sf.eas_compl_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_end_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(sf.send_accepted_timestamp as date) as timestamp) as gw_beg_snd_gw,
    cast(cast(sf.send_gft_request_timestamp as date) as timestamp)  as gw_end_snd_gw,
    cast(cast(sf.send_request_timestamp as date) as timestamp)  as gw_beg_snd_mi,
    cast(cast(sf.send_confirmed_timestamp as date) as timestamp)  as gw_end_snd_mi,
    cast(cast(sf.send_completed_timestamp as date) as timestamp) as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SEND_FILE sf
    --per estrarre i circuiti SIAnet
--left outer join VCC_ABAB_SIANET ab on (ab.service='FT' and ab.local_ba=sf.local_ba and ab.remote_ba=sf.remote_ba and ab.with_msg=0)
    -- per estrarre tipo interfaccia
--left outer join VCL_FT cft on (sf.local_ba=cft.local_ba and sf.remote_ba=cft.remote_ba and cft.with_msg=0)
/
-- *************************************************************************
-- VI_MSS_RCV
-- *************************************************************************
CREATE OR REPLACE VIEW VI_MSS_RCV
(LOCAL_BA,REMOTE_BA,PRIORITY,MSGID,NET_MSGID,TUR,REMOTE_REF,MSG_TYPE,CAT_APPL,SEQUENCEID,STSCODE,STATUS,STATUS_INFO,APPL_CHECK,COMPLETE,MSGSIZE,MAB,MI_BEG_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,MSG_DIGEST_ALG,MSG_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    trf.local_ba,
    trf.remote_ba,
    trf.priority,
    trf.msgid,
    trf.net_msgid,
    trf.tur,
    trf.remote_ref,
    trf.msg_type,
    trf.cat_appl,
    trf.seqid as sequenceid,
    trf.stscode,
    trf.status,
    trf.status_info,
    trf.appl_check,
    trf.complete,
    trf.msgsize,
    trf.mab,
    cast(to_date(substr(trf.eas_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)          as mi_beg_snd_gw,
    cast(to_date(substr(trf.fer_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)          as gw_beg_snd_gw,
    cast(to_date(substr(trf.first_bar_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)    as gw_end_snd_gw,
    cast(to_date(substr(trf.fen_del_time, 0,12),'YYMMDDHH24MISS') as timestamp)          as gw_beg_snd_mi,
    cast(to_date(substr(trf.fer_del_time, 0,12),'YYMMDDHH24MISS') as timestamp)          as gw_end_snd_mi,
    cast(to_date(substr(trf.last_bar_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)     as mi_beg_rcv_gw,
    trf.msg_digest_alg,
    trf.msg_digest,
    trf.local_auth_info_alg,
    trf.local_auth_info
from
    FAS_MSG_RECV trf
/
-- *************************************************************************
-- VI_MSS_SND
-- *************************************************************************
CREATE OR REPLACE VIEW VI_MSS_SND
(LOCAL_BA,REMOTE_BA,PRIORITY,MSGID,NET_MSGID,TUR,REMOTE_REF,MSG_TYPE,CAT_APPL,SEQUENCEID,STSCODE,STATUS,STATUS_INFO,SEND_ERR_TIMESTAMP,APPL_CHECK,COMPLETE,MSGSIZE,MAB,BA_INSERT_TIMESTAMP,MI_ACCEPT_BA,MI_BEG_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,MSG_DIGEST_ALG,MSG_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    trf.local_ba,
    trf.remote_ba,
    trf.priority,
    trf.msgid,
    trf.net_msgid,
    trf.tur,
    trf.remote_ref,
    trf.msg_type,
    trf.cat_appl,
    trf.fas_seqid as sequenceid,
    trf.stscode,
    trf.status,
    trf.status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(trf.send_err_timestamp as date) as timestamp)                                  as send_err_timestamp,
    trf.appl_check,
    trf.complete,
    trf.msgsize,
    trf.mab,
    cast(cast(trf.ba_insert_timestamp as date) as timestamp)                                 as ba_insert_timestamp,
    cast(to_date(trf.createdate,'YYYY/MM/DD HH24:MI:SS') as timestamp)                       as mi_accept_ba,
    -- first_eas_sub_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(trf.first_eas_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)        as mi_beg_snd_gw,
    -- fer_sub_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_date(substr(trf.fer_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)              as gw_beg_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(cast(trf.send_req_timestamp as date) as timestamp)                                  as gw_end_snd_gw,
    cast(to_date(substr(trf.fen_del_time, 0,12),'YYMMDDHH24MISS') as timestamp)              as gw_beg_snd_mi,
    cast(to_date(substr(trf.fer_del_time, 0,12),'YYMMDDHH24MISS') as timestamp)              as gw_end_snd_mi,
    cast(to_date(substr(trf.last_eas_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp)         as mi_beg_rcv_gw,
    trf.msg_digest_alg,
    trf.msg_digest,
    trf.local_auth_info_alg,
    trf.local_auth_info
from
    FAS_MSG_SEND trf
    --per estrarre i circuiti SIAnet
--left outer join VCC_ABAB_SIANET ab on (ab.service='MS' and ab.local_ba=trf.local_ba and ab.remote_ba=trf.remote_ba)
    -- per estrarre tipo interfaccia
--left outer join VCL_MS cms         on (trf.local_ba=cms.local_ba and trf.remote_ba=cms.remote_ba)
/
