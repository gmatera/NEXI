-- *************************************************************************
-- conf_route_interface
-- *************************************************************************
create sequence SEQ_CONF_ROUTE_INTERFACE start 1 increment 1
/
CREATE TABLE CONF_ROUTE_INTERFACE (
       id varchar(255) not null,
        SERVICE varchar(255) not null,
        REMOTEBA_ID varchar(255) not null,
        LOCALBA_ID varchar(255) not null,
        INTERFACE varchar(255) not null,
        primary key (id))
/
alter table if exists CONF_ROUTE_INTERFACE add constraint UNIQUE_ROUTE_INTERFACE unique (LOCALBA_ID, REMOTEBA_ID, INTERFACE, SERVICE)
/
-- *************************************************************************
-- add_on_configuration_fts
-- *************************************************************************
create sequence seq_add_on_configuration_fts_localba_remoteba start with 1 increment by  1
/
create table ADD_ON_CONFIGURATION_FTS (
        id bigint not null,
		LOCALBAID VARCHAR(12) not null,
        REMOTEBAID VARCHAR(12) not null,
        SND_PATH VARCHAR(255) not null,
        SENDING_PREFIX VARCHAR(255),
        ERROR_PREFIX VARCHAR(255),
        SENT_PREFIX VARCHAR(255),
        ERRORDELIVER_PREFIX VARCHAR(255),
        primary key (id))
/
alter table ADD_ON_CONFIGURATION_FTS add constraint UNIQUE_ADD_ON_CONFIGURATION_FTS unique (LOCALBAID, REMOTEBAID)
/
-- *************************************************************************
-- configuration_fms_localba_remoteba
-- *************************************************************************
create sequence seq_configuration_fms_localba_remoteba start 1 increment 1
/
create table CONFIGURATION_FMS_LOCALBA_REMOTEBA (
        id bigint not null,
		REMOTEBAID varchar(12) not null,
        LOCALBAID varchar(12) not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean,
        LAU_FORMAT varchar(255),
        LEFT_LAU_KEY varchar(255),
        RCV_CODE_PAGE varchar(255),
        RCV_DIGEST_FILE_ALG int4,
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT int4,
        RCV_PATH varchar(255),
        RIGHT_LAU_KEY varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO boolean,
        RCV_COMPLETION_ALGO boolean,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        primary key (id))
/
alter table if exists CONFIGURATION_FMS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID);
/
-- *************************************************************************
-- configuration_fts_localba_remoteba
-- *************************************************************************
create sequence seq_configuration_fts_localba_remoteba start 1 increment 1
/
create table CONFIGURATION_FTS_LOCALBA_REMOTEBA (
       id varchar(255) not null,
		REMOTEBAID varchar(12) not null,
        LOCALBAID varchar(12) not null,
        INTERFACE_TYPE varchar(255) not null,
        LAU_ENABLED boolean,
        LAU_FORMAT varchar(255),
        LEFT_LAU_KEY varchar(255),
        RCV_CODE_PAGE varchar(255),
        RCV_DIGEST_FILE_ALG int4,
        RCV_DSN_CREATION_ALGO int4,
        RCV_DSN_PREFIX varchar(44),
        RCV_LINE_SEPARATOR varchar(255),
        RCV_RECORD_FORMAT varchar(255),
        RCV_MAX_REC_LENGHT  int4,
        RCV_PATH varchar(255),
        RIGHT_LAU_KEY varchar(255),
        SND_CODE_PAGE varchar(255),
        SND_COMPLETION_ALGO boolean,
        RCV_COMPLETION_ALGO boolean,
        SND_LINE_SEPARATOR varchar(255),
        SND_MAX_REC_LENGHT int4,
        SND_PATH varchar(255),
        SND_RECORD_FORMAT varchar(255),
        primary key (id))
/
alter table if exists CONFIGURATION_FTS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_FTS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID);
/
-- *************************************************************************
-- configuration_mss_localba_remoteba
-- *************************************************************************
create sequence seq_configuration_mss_localba_remoteba start with 1 increment by  1
/
create table CONFIGURATION_MSS_LOCALBA_REMOTEBA (
        id bigint not null,
        INTERFACE_TYPE VARCHAR(255) not null,
        LOCALBAID VARCHAR(12) not null,
		REMOTEBAID VARCHAR(12) not null,
        SND_COMPLETION_ALGO boolean,
        RCV_COMPLETION_ALGO boolean,
        LAU_ENABLED boolean,
        LAU_FORMAT VARCHAR(255),
        LEFT_LAU_KEY VARCHAR(255),
        RIGHT_LAU_KEY VARCHAR(255),
         primary key (id))
/
alter table CONFIGURATION_MSS_LOCALBA_REMOTEBA add constraint UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA unique (INTERFACE_TYPE, LOCALBAID, REMOTEBAID)
/
-- *************************************************************************
-- Sync_send
-- *************************************************************************
CREATE TABLE sync_send (
	localba_id			character varying(12),
	remoteba_id			character varying(12),
	vfn			character varying(32),
	status_ba			character varying(60) DEFAULT 'SUBMITTED'::character varying,
	status_sync			character varying(60),
	stcode_ba			bigint DEFAULT 0,
	stcode_sync			bigint DEFAULT 0,
	fname			character varying(1024),
	retry_cnt			bigint,
	userdataremote			character varying(80),
	priority			bigint DEFAULT 0,
	bamsg_id			character varying(30),
	message			bytea,
	accepttime			timestamp without time zone,
	ftssendtime			timestamp without time zone,
	ftscompletetime			timestamp without time zone,
	mssendtime			timestamp without time zone,
	mscompletetime			timestamp without time zone,
	notifytime			timestamp without time zone,
	modtime			timestamp without time zone DEFAULT timezone('utc'::text, now()),
	userdataremotelen			bigint,
	tur			character varying(16),
	messagelen			bigint,
	messagetype			character varying(3),
	catappl			character varying(4),
	errortime			timestamp without time zone,
	fer_sub_fst_fts_tmp			timestamp without time zone,
	fer_sub_lst_fts_tmp			timestamp without time zone,
	fer_dlv_fst_fts_tmp			timestamp without time zone,
	fer_dlv_lst_fts_tmp			timestamp without time zone,
	fen_sub_mss_tmp			timestamp without time zone,
	fen_dlv_mss_tmp			timestamp without time zone,
	crt_sub_mss_tmp			timestamp without time zone,
	crt_dlv_mss_tmp			timestamp without time zone,
	datasetname			character varying(255),
	local_data			character varying(255),
	certf_req			bigint,
	fsize			bigint,
	fhash			character varying(32),
	fblockmoved			bigint,
	fmap			character varying(256),
	complete			bigint DEFAULT 0,
	last_update			bigint,
	ba_insert_timestamp			timestamp without time zone DEFAULT timezone('utc'::text, now()),
	send_accepted_timestamp			timestamp without time zone,
	send_request_timestamp			timestamp without time zone,
	send_confirmed_timestamp			timestamp without time zone,
	send_completed_timestamp			timestamp without time zone,
	send_error_timestamp			timestamp without time zone,
	status_info			character varying(1024),
	id_sync_send			bigint,
	operation_timestamp			timestamp without time zone,
	operation			character varying(10),
	reactivate			character varying(10),
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	file_digest_alg			character varying(8),
	file_digest			character varying(128),
	msg_digest_alg			character varying(8),
	msg_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128))
	--tablespace ${DB_USER}_table
--/
--alter table sync_send add constraint pk_sync_send 
--primary key (id_sync_send)
--using index tablespace ${DB_USER}_index
--/
--alter table sync_send add constraint uk_sync_send_vfn 
--unique (vfn)
--using index tablespace ${DB_USER}_index
--/
--alter table sync_send add constraint ck_sync_send_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table sync_send add constraint ck_sync_send_cleanup_type 
--check (((cleanup_type)::text = 'IRL_FMS_SND'::text))
--/
--alter table sync_send add constraint ck_sync_send_fdigest_alg 
--check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
--/
--alter table sync_send add constraint ck_sync_send_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--alter table sync_send add constraint ck_sync_send_mdigest_alg 
--check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
--/
--CREATE INDEX idx_sync_send_1 ON sync_send USING btree (stcode_ba, stcode_sync, id_sync_send)
 --tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_sync_send_2 ON sync_send USING btree (complete, stcode_ba, stcode_sync, last_update)
 --tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_sync_send_3 ON sync_send USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE sync_send ALTER COLUMN localba_id SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN remoteba_id SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN stcode_ba SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN stcode_sync SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN fname SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN userdataremote SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN modtime SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN messagetype SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN id_sync_send SET NOT NULL
/
ALTER TABLE sync_send ALTER COLUMN cleanup_status SET NOT NULL
/
-- *************************************************************************
-- Sync_recv
-- *************************************************************************
CREATE TABLE sync_recv
(
	localba_id			character varying(12),
	remoteba_id			character varying(12),
	vfn			character varying(32),
	status_ba			character varying(60),
	status_sync			character varying(60),
	stcode_ba			bigint,
	stcode_sync			bigint,
	timeout			bigint,
	ftsrecvtime			timestamp without time zone,
	msrecvtime			timestamp without time zone,
	fname			character varying(1024),
	userdataremote			character varying(80),
	priority			bigint,
	messagetype			character varying(3),
	modtime			timestamp without time zone,
	message			bytea,
	messagelength			bigint,
	cat_apply			character varying(4),
	tur			character varying(16),
	bamsgid			character varying(30),
	receivetime			timestamp without time zone,
	ftsinserttime			timestamp without time zone,
	msconftime			timestamp without time zone,
	ftsdelivtime			timestamp without time zone,
	ba_processed_tmp			timestamp without time zone,
	datasetname			character varying(255),
	fer_sub_fst_fts_tmp			timestamp without time zone,
	fer_sub_lst_fts_tmp			timestamp without time zone,
	fer_dlv_fst_fts_tmp			timestamp without time zone,
	fer_dlv_lst_fts_tmp			timestamp without time zone,
	fen_sub_mss_tmp			timestamp without time zone,
	fen_dlv_mss_tmp			timestamp without time zone,
	crt_sub_mss_tmp			timestamp without time zone,
	crt_dlv_mss_tmp			timestamp without time zone,
	read_str_fts_tmp			timestamp without time zone,
	read_end_fts_tmp			timestamp without time zone,
	local_data			character varying(255),
	certf_req			bigint,
	start_recv_gmt			timestamp without time zone,
	fsize			bigint,
	fhash			character varying(32),
	fblockmoved			bigint,
	fmap			character varying(256),
	complete			bigint,
	last_update			bigint,
	read_request_timestamp			timestamp without time zone,
	read_confirmed_timestamp			timestamp without time zone,
	read_completed_timestamp			timestamp without time zone,
	read_notified_timestamp			timestamp without time zone,
	read_error_timestamp			timestamp without time zone,
	status_info			character varying(1024),
	file_format			character varying(32),
	compression_algo			character varying(32),
	fas_seqid			bigint,
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	file_digest_alg			character varying(8),
	file_digest			character varying(128),
	msg_digest_alg			character varying(8),
	msg_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128))
--	tablespace ${DB_USER}_table
--/
--alter table sync_recv add constraint pk_sync_recv 
--primary key (fas_seqid)
--using index tablespace ${DB_USER}_index
--/
--alter table sync_recv add constraint uk_syncrecvlocalbaidremoteb_1 
--unique (vfn,localba_id,remoteba_id)
--using index tablespace ${DB_USER}_index
--/
--alter table sync_recv add constraint ck_sync_recv_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table sync_recv add constraint ck_sync_recv_cleanup_type 
--check (((cleanup_type)::text = 'IRL_FMS_RCV'::text))
--/
--alter table sync_recv add constraint ck_sync_recv_fdigest_alg 
--check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
--/
--alter table sync_recv add constraint ck_sync_recv_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--alter table sync_recv add constraint ck_sync_recv_mdigest_alg 
--check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
--/
--CREATE INDEX idx_sync_recv_1 ON sync_recv USING btree (stcode_ba, stcode_sync)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_sync_recv_2 ON sync_recv USING btree (complete, stcode_ba, stcode_sync, last_update)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_sync_recv_3 ON sync_recv USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE sync_recv ALTER COLUMN localba_id SET NOT NULL
/
ALTER TABLE sync_recv ALTER COLUMN remoteba_id SET NOT NULL
/
ALTER TABLE sync_recv ALTER COLUMN vfn SET NOT NULL
/
ALTER TABLE sync_recv ALTER COLUMN fas_seqid SET NOT NULL
/
ALTER TABLE sync_recv ALTER COLUMN cleanup_status SET NOT NULL
/
-- *************************************************************************
-- Send_file
-- *************************************************************************
CREATE TABLE send_file
(
	vfn			character varying(32),
	fname			character varying(1024),
	local_ba			character varying(12),
	remote_ba			character varying(12),
	status			character varying(60) DEFAULT 'FILE TO BE PROCESSED'::character varying,
	easstatus			character varying(3),
	stscode			bigint DEFAULT 90,
	complete			bigint DEFAULT 0,
	transp_type			character varying(8) DEFAULT 'EAS'::character varying,
	createdate			character varying(24),
	lastupdate			character varying(24),
	appl_check			bigint DEFAULT 0,
	original_fname			character varying(1024),
	retry_cnt			bigint,
	update_mark			bigint,
	fsize			bigint,
	fmd5			character varying(32),
	fblkmoved			bigint,
	filemap			character varying(255),
	operation_timestamp			timestamp without time zone,
	operation			character varying(10),
	reactivate			character varying(10),
	ref_date			character varying(6),
	act_req_time			character varying(12),
	eas_acq_time			character varying(17),
	queue_ins_time			character varying(17),
	start_time			character varying(17),
	eas_compl_time			character varying(17),
	ba_proc_time			character varying(12),
	eas_elab_time			character varying(17),
	ba_insert_timestamp			timestamp without time zone DEFAULT timezone('utc'::text, now()),
	send_accepted_timestamp			timestamp without time zone,
	send_gft_request_timestamp			timestamp without time zone,
	send_request_timestamp			timestamp without time zone,
	send_confirmed_timestamp			timestamp without time zone,
	send_completed_timestamp			timestamp without time zone,
	send_error_timestamp			timestamp without time zone,
	status_info			character varying(1024),
	id_send_file			bigint,
	applicative_data_field			character varying(80),
	applicative_data_field_length			bigint,
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	file_digest_alg			character varying(8),
	file_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128),
	local_auth_info_fs			character varying(128))
--	tablespace ${DB_USER}_table
--/
--alter table send_file add constraint pk_send_file 
--primary key (id_send_file)
--using index tablespace ${DB_USER}_index
--/
--alter table send_file add constraint uk_send_file_vfn 
--unique (vfn)
--using index tablespace ${DB_USER}_index
--/
--alter table send_file add constraint ck_send_file_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table send_file add constraint ck_send_file_cleanup_type 
--check (((cleanup_type)::text = 'IRL_FTS_SND'::text))
--/
--alter table send_file add constraint ck_send_file_fdigest_alg 
--check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
--/
--alter table send_file add constraint ck_send_file_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL) AND (local_auth_info_fs IS NULL)) OR ((local_auth_info IS NOT NULL) AND (local_auth_info_fs IS NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text)) OR ((local_auth_info IS NULL) AND (local_auth_info_fs IS NOT NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--CREATE INDEX idx_send_file_1 ON send_file USING btree (stscode, id_send_file)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_send_file_2 ON send_file USING btree (complete, stscode, lastupdate)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_send_file_3 ON send_file USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_send_file_ab ON send_file USING btree (appl_check, stscode)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE send_file ALTER COLUMN vfn SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN local_ba SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN remote_ba SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN status SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN stscode SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN complete SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN transp_type SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN appl_check SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN id_send_file SET NOT NULL
/
ALTER TABLE send_file ALTER COLUMN cleanup_status SET NOT NULL
/
-- *************************************************************************
-- Recv_file
-- *************************************************************************
CREATE TABLE recv_file
(
	vfn			character varying(32),
	fname			character varying(1024),
	local_ba			character varying(12),
	remote_ba			character varying(12),
	status			character varying(60),
	easstatus			character varying(3),
	stscode			bigint,
	complete			bigint,
	transp_type			character varying(8),
	createdate			character varying(24),
	lastupdate			character varying(24),
	appl_check			bigint,
	original_fname			character varying(1024),
	retry_cnt			bigint,
	update_mark			bigint,
	fsize			bigint,
	fmd5			character varying(32),
	fblkmoved			bigint,
	filemap			character varying(255),
	ref_date			character varying(6),
	act_req_time			character varying(12),
	eas_acq_time			character varying(17),
	queue_ins_time			character varying(17),
	start_time			character varying(17),
	eas_compl_time			character varying(17),
	first_del_time			character varying(17),
	last_del_time			character varying(17),
	first_read_time			character varying(17),
	last_read_time			character varying(17),
	ba_proc_time			character varying(12),
	eas_elab_time			character varying(17),
	recv_request_timestamp			timestamp without time zone,
	recv_error_timestamp			timestamp without time zone,
	recv_complete_timestamp			timestamp without time zone,
	recv_delivered_timestamp			timestamp without time zone,
	fas_seqid			bigint,
	status_info			character varying(1024),
	applicative_data_field			character varying(80),
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	file_digest_alg			character varying(8),
	file_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128),
	local_auth_info_fs			character varying(128))
--	tablespace ${DB_USER}_table
--/
--alter table recv_file add constraint pk_recv_file 
--primary key (fas_seqid)
--using index tablespace ${DB_USER}_index
--/
--alter table recv_file add constraint uk_recvfilevfnlocalbaremoteba 
--unique (vfn,local_ba,remote_ba)
--using index tablespace ${DB_USER}_index
--/
--alter table recv_file add constraint ck_recv_file_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table recv_file add constraint ck_recv_file_cleanup_type 
--check (((cleanup_type)::text = 'IRL_FTS_RCV'::text))
--/
--alter table recv_file add constraint ck_recv_file_fdigest_alg 
--check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
--/
--alter table recv_file add constraint ck_recv_file_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL) AND (local_auth_info_fs IS NULL)) OR ((local_auth_info IS NOT NULL) AND (local_auth_info_fs IS NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text)) OR ((local_auth_info IS NULL) AND (local_auth_info_fs IS NOT NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--CREATE INDEX idx_recv_file_1 ON recv_file USING btree (stscode)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_recv_file_2 ON recv_file USING btree (complete, stscode, lastupdate)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_recv_file_3 ON recv_file USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_recv_file_ab ON recv_file USING btree (appl_check, stscode, local_ba)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE recv_file ALTER COLUMN vfn SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN local_ba SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN remote_ba SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN status SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN stscode SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN complete SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN transp_type SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN appl_check SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN fas_seqid SET NOT NULL
/
ALTER TABLE recv_file ALTER COLUMN cleanup_status SET NOT NULL
/
-- *************************************************************************
-- Fas_msg_send
-- *************************************************************************

CREATE TABLE fas_msg_send
(
	local_ba			character varying(12),
	remote_ba			character varying(12),
	msg_type			character varying(3),
	cat_appl			character varying(4),
	tur			character varying(16),
	msgid			character varying(30),
	remote_ref			character varying(80),
	priority			bigint DEFAULT 0,
	certf_req			bigint,
	stscode			bigint DEFAULT 0,
	status			character varying(256) DEFAULT 'NEW TRAFFIC'::character varying,
	easstatus			character varying(3),
	complete			bigint DEFAULT 0,
	appl_check			bigint DEFAULT 0,
	createdate			character varying(24),
	lastupdate			character varying(24),
	update_mark			bigint,
	seqid			bigint,
	msgsize			bigint,
	mab			bytea,
	ba_req_time			character varying(17),
	bar_acq_time			character varying(17),
	femsi_retry_number			bigint,
	first_eas_sub_time			character varying(17),
	last_eas_sub_time			character varying(17),
	fer_sub_time			character varying(17),
	fen_del_time			character varying(17),
	fer_del_time			character varying(17),
	ba_insert_timestamp			timestamp without time zone DEFAULT timezone('utc'::text, now()),
	load_timestamp			timestamp without time zone,
	send_req_timestamp			timestamp without time zone,
	send_err_timestamp			timestamp without time zone,
	send_cnf_timestamp			timestamp without time zone,
	send_sc_timestamp			timestamp without time zone,
	status_info			character varying(1024),
	fas_seqid			bigint,
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	net_msgid			character varying(16),
	msg_digest_alg			character varying(8),
	msg_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128))
--	tablespace ${DB_USER}_table
--/
--alter table fas_msg_send add constraint pk_fas_msg_send 
--primary key (fas_seqid)
--using index tablespace ${DB_USER}_index
--/
--alter table fas_msg_send add constraint uk_fasmsgsendr6cufasmsgsend 
--unique (local_ba,msgid)
--using index tablespace ${DB_USER}_index
--/
--alter table fas_msg_send add constraint ck_fas_msg_send_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table fas_msg_send add constraint ck_fas_msg_send_cleanup_type 
--check (((cleanup_type)::text = 'IRL_MSS_SND'::text))
--/
--alter table fas_msg_send add constraint ck_fas_msg_send_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--alter table fas_msg_send add constraint ck_fas_msg_send_mdigest_alg 
--check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
--/
--CREATE INDEX idx_fas_msg_send_1 ON fas_msg_send USING btree (stscode, fas_seqid)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_send_2 ON fas_msg_send USING btree (complete, stscode, lastupdate)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_send_3 ON fas_msg_send USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_send_ab ON fas_msg_send USING btree (appl_check, stscode)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE fas_msg_send ALTER COLUMN local_ba SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN remote_ba SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN msgid SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN stscode SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN appl_check SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN fas_seqid SET NOT NULL
/
ALTER TABLE fas_msg_send ALTER COLUMN cleanup_status SET NOT NULL
/
-- *************************************************************************
-- Fas_msg_recv
-- *************************************************************************
CREATE TABLE fas_msg_recv
(
	local_ba			character varying(12),
	remote_ba			character varying(12),
	msg_type			character varying(3),
	cat_appl			character varying(4),
	tur			character varying(16),
	msgid			character varying(30),
	remote_ref			character varying(80),
	priority			bigint,
	certf_req			bigint,
	stscode			bigint,
	status			character varying(60),
	easstatus			character varying(3),
	complete			bigint,
	appl_check			bigint,
	createdate			character varying(24),
	lastupdate			character varying(24),
	update_mark			bigint,
	seqid			bigint,
	msgsize			bigint,
	mab			bytea,
	ba_req_time			character varying(17),
	bar_acq_time			character varying(17),
	eas_sub_time			character varying(17),
	fer_sub_time			character varying(17),
	fen_del_time			character varying(17),
	fer_del_time			character varying(17),
	first_bar_sub_time			character varying(17),
	last_bar_sub_time			character varying(17),
	recv_rnc_timestamp			timestamp without time zone,
	recv_rc_timestamp			timestamp without time zone,
	status_info			character varying(1024),
	cleanup_status			character varying(20) DEFAULT 'FREE'::character varying,
	cleanup_lot			bigint,
	cleanup_type			character varying(20),
	net_msgid			character varying(16),
	msg_digest_alg			character varying(8),
	msg_digest			character varying(128),
	local_auth_info_alg			character varying(8),
	local_auth_info			character varying(128))
--	tablespace ${DB_USER}_table
--/
--alter table fas_msg_recv add constraint pk_fas_msg_recv 
--primary key (seqid)
--using index tablespace ${DB_USER}_index
--/
--alter table fas_msg_recv add constraint uk_fasmsgrecvr6cufasmsgrecv 
--unique (local_ba,msgid)
--using index tablespace ${DB_USER}_index
--/
--alter table fas_msg_recv add constraint ck_fas_msg_recv_cleanup_status 
--check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
--/
--alter table fas_msg_recv add constraint ck_fas_msg_recv_cleanup_type 
--check (((cleanup_type)::text = 'IRL_MSS_RCV'::text))
--/
--alter table fas_msg_recv add constraint ck_fas_msg_recv_lau_alg 
--check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
--/
--alter table fas_msg_recv add constraint ck_fas_msg_recv_mdigest_alg 
--check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
--/
--CREATE INDEX idx_fas_msg_recv_ab ON fas_msg_recv USING btree (appl_check, stscode, local_ba)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_recv_1 ON fas_msg_recv USING btree (stscode)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_recv_2 ON fas_msg_recv USING btree (complete, stscode, lastupdate)
-- tablespace ${DB_USER}_index
--/
--CREATE INDEX idx_fas_msg_recv_3 ON fas_msg_recv USING btree (cleanup_status, cleanup_type, cleanup_lot)
-- tablespace ${DB_USER}_index
--
-- TRIGGER DA INSERIRE
--
/
ALTER TABLE fas_msg_recv ALTER COLUMN local_ba SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN remote_ba SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN msgid SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN stscode SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN appl_check SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN seqid SET NOT NULL
/
ALTER TABLE fas_msg_recv ALTER COLUMN cleanup_status SET NOT NULL
/
