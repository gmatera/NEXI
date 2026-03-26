-- POSTGRESQL

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
tablespace &JEAS_USERNAME._table
/

alter table fas_msg_send add constraint pk_fas_msg_send 
primary key (fas_seqid)
using index tablespace &JEAS_USERNAME._index
/

alter table fas_msg_send add constraint uk_fasmsgsendr6cufasmsgsend 
unique (local_ba,msgid)
using index tablespace &JEAS_USERNAME._index
/

alter table fas_msg_send add constraint ck_fas_msg_send_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table fas_msg_send add constraint ck_fas_msg_send_cleanup_type 
check (((cleanup_type)::text = 'IRL_MSS_SND'::text))
/
alter table fas_msg_send add constraint ck_fas_msg_send_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/
alter table fas_msg_send add constraint ck_fas_msg_send_mdigest_alg 
check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
/

CREATE INDEX idx_fas_msg_send_1 ON fas_msg_send USING btree (stscode, fas_seqid)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_fas_msg_send_2 ON fas_msg_send USING btree (complete, stscode, lastupdate)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_fas_msg_send_3 ON fas_msg_send USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
/

CREATE INDEX idx_fas_msg_send_ab ON fas_msg_send USING btree (appl_check, stscode)
 tablespace &JEAS_USERNAME._index
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
tablespace &JEAS_USERNAME._table
/

alter table fas_msg_recv add constraint pk_fas_msg_recv 
primary key (seqid)
using index tablespace &JEAS_USERNAME._index
/

alter table fas_msg_recv add constraint uk_fasmsgrecvr6cufasmsgrecv 
unique (local_ba,msgid)
using index tablespace &JEAS_USERNAME._index
/

alter table fas_msg_recv add constraint ck_fas_msg_recv_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table fas_msg_recv add constraint ck_fas_msg_recv_cleanup_type 
check (((cleanup_type)::text = 'IRL_MSS_RCV'::text))
/
alter table fas_msg_recv add constraint ck_fas_msg_recv_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/
alter table fas_msg_recv add constraint ck_fas_msg_recv_mdigest_alg 
check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
/

CREATE INDEX idx_fas_msg_recv_ab ON fas_msg_recv USING btree (appl_check, stscode, local_ba)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_fas_msg_recv_1 ON fas_msg_recv USING btree (stscode)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_fas_msg_recv_2 ON fas_msg_recv USING btree (complete, stscode, lastupdate)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_fas_msg_recv_3 ON fas_msg_recv USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
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
-- *************************************************************************
-- Sync_send
-- *************************************************************************
CREATE TABLE sync_send
(
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
tablespace &JEAS_USERNAME._table
/

alter table sync_send add constraint pk_sync_send 
primary key (id_sync_send)
using index tablespace &JEAS_USERNAME._index
/

alter table sync_send add constraint uk_sync_send_vfn 
unique (vfn)
using index tablespace &JEAS_USERNAME._index
/

alter table sync_send add constraint ck_sync_send_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table sync_send add constraint ck_sync_send_cleanup_type 
check (((cleanup_type)::text = 'IRL_FMS_SND'::text))
/
alter table sync_send add constraint ck_sync_send_fdigest_alg 
check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
/
alter table sync_send add constraint ck_sync_send_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/
alter table sync_send add constraint ck_sync_send_mdigest_alg 
check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
/

CREATE INDEX idx_sync_send_1 ON sync_send USING btree (stcode_ba, stcode_sync, id_sync_send)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_sync_send_2 ON sync_send USING btree (complete, stcode_ba, stcode_sync, last_update)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_sync_send_3 ON sync_send USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
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
tablespace &JEAS_USERNAME._table
/

alter table sync_recv add constraint pk_sync_recv 
primary key (fas_seqid)
using index tablespace &JEAS_USERNAME._index
/

alter table sync_recv add constraint uk_syncrecvlocalbaidremoteb_1 
unique (vfn,localba_id,remoteba_id)
using index tablespace &JEAS_USERNAME._index
/

alter table sync_recv add constraint ck_sync_recv_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table sync_recv add constraint ck_sync_recv_cleanup_type 
check (((cleanup_type)::text = 'IRL_FMS_RCV'::text))
/
alter table sync_recv add constraint ck_sync_recv_fdigest_alg 
check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
/
alter table sync_recv add constraint ck_sync_recv_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL)) OR ((local_auth_info_alg IS NOT NULL) AND (local_auth_info IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/
alter table sync_recv add constraint ck_sync_recv_mdigest_alg 
check ((((msg_digest_alg IS NULL) AND (msg_digest IS NULL)) OR ((msg_digest_alg IS NOT NULL) AND (msg_digest IS NOT NULL) AND ((msg_digest_alg)::text = 'SHA-256'::text))))
/

CREATE INDEX idx_sync_recv_1 ON sync_recv USING btree (stcode_ba, stcode_sync)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_sync_recv_2 ON sync_recv USING btree (complete, stcode_ba, stcode_sync, last_update)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_sync_recv_3 ON sync_recv USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
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
tablespace &JEAS_USERNAME._table
/

alter table send_file add constraint pk_send_file 
primary key (id_send_file)
using index tablespace &JEAS_USERNAME._index
/

alter table send_file add constraint uk_send_file_vfn 
unique (vfn)
using index tablespace &JEAS_USERNAME._index
/

alter table send_file add constraint ck_send_file_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table send_file add constraint ck_send_file_cleanup_type 
check (((cleanup_type)::text = 'IRL_FTS_SND'::text))
/
alter table send_file add constraint ck_send_file_fdigest_alg 
check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
/
alter table send_file add constraint ck_send_file_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL) AND (local_auth_info_fs IS NULL)) OR ((local_auth_info IS NOT NULL) AND (local_auth_info_fs IS NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text)) OR ((local_auth_info IS NULL) AND (local_auth_info_fs IS NOT NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/

CREATE INDEX idx_send_file_1 ON send_file USING btree (stscode, id_send_file)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_send_file_2 ON send_file USING btree (complete, stscode, lastupdate)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_send_file_3 ON send_file USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
/

CREATE INDEX idx_send_file_ab ON send_file USING btree (appl_check, stscode)
 tablespace &JEAS_USERNAME._index
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
tablespace &JEAS_USERNAME._table
/

alter table recv_file add constraint pk_recv_file 
primary key (fas_seqid)
using index tablespace &JEAS_USERNAME._index
/

alter table recv_file add constraint uk_recvfilevfnlocalbaremoteba 
unique (vfn,local_ba,remote_ba)
using index tablespace &JEAS_USERNAME._index
/

alter table recv_file add constraint ck_recv_file_cleanup_status 
check (((cleanup_status)::text = ANY ((ARRAY['FREE'::character varying, 'MARKED'::character varying, 'EXPORTED'::character varying, 'PARTIAL_DELETED'::character varying, 'DELETABLE'::character varying])::text[])))
/
alter table recv_file add constraint ck_recv_file_cleanup_type 
check (((cleanup_type)::text = 'IRL_FTS_RCV'::text))
/
alter table recv_file add constraint ck_recv_file_fdigest_alg 
check ((((file_digest_alg IS NULL) AND (file_digest IS NULL)) OR ((file_digest_alg IS NOT NULL) AND (file_digest IS NOT NULL) AND ((file_digest_alg)::text = 'SHA-256'::text))))
/
alter table recv_file add constraint ck_recv_file_lau_alg 
check ((((local_auth_info_alg IS NULL) AND (local_auth_info IS NULL) AND (local_auth_info_fs IS NULL)) OR ((local_auth_info IS NOT NULL) AND (local_auth_info_fs IS NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text)) OR ((local_auth_info IS NULL) AND (local_auth_info_fs IS NOT NULL) AND (local_auth_info_alg IS NOT NULL) AND ((local_auth_info_alg)::text = 'HS256'::text))))
/

CREATE INDEX idx_recv_file_1 ON recv_file USING btree (stscode)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_recv_file_2 ON recv_file USING btree (complete, stscode, lastupdate)
 tablespace &JEAS_USERNAME._index
/
CREATE INDEX idx_recv_file_3 ON recv_file USING btree (cleanup_status, cleanup_type, cleanup_lot)
 tablespace &JEAS_USERNAME._index
/

CREATE INDEX idx_recv_file_ab ON recv_file USING btree (appl_check, stscode, local_ba)
 tablespace &JEAS_USERNAME._index
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
-- VI_ABAB_SND
-- *************************************************************************
-- 10900_VI_ABAB_SND.sql

CREATE OR REPLACE VIEW VI_ABAB_SND
(SERVICE,LOCALBA,REMOTEBA, AGGR1, AGGR2)
AS
select
    (case 
        when (ab.service='FT' and ab.with_msg=0) then 'FTS'
        when (ab.service='FT' and ab.with_msg=1) then 'FMS'
        else 'MSS'
        end
    ) as service,
ab.local_ba,
ab.remote_ba,
coalesce(agg.AGGR1_ID, ab.STAT_AGGR1_ID),
ab.STAT_AGGR2_ID
from VCC_ABAB_SIANET ab
left outer join VCL_FT cft on (ab.service='FT' and ab.local_ba=cft.local_ba and ab.remote_ba=cft.remote_ba and ab.with_msg=cft.with_msg)
left outer join VCL_MS cms on (ab.service='MS' and ab.local_ba=cms.local_ba and ab.remote_ba=cms.remote_ba)
left outer join TCC_AGGREGATOR_R agg on (ab.TCC_AGGREGATOR_R_ID = agg.ID)
where ab.service in('MS','FT')
and ((ab.service='FT' and cft.interface_type not in ('MQI-STD','MQI-XML')) or (ab.service='MS' and cms.interface_type not in ('MQI-STD','MQI-XML')))
/
-- *************************************************************************
-- VI_FMS_RCV
-- *************************************************************************
-- 01400_VI_FMS_RCV.sql

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
    cast(rf.ftsdelivtime as timestamp(0)) as mi_beg_snd_gw, -- Bug #12415
    cast(rf.crt_sub_mss_tmp as timestamp(0)) as gw_beg_snd_gw_ms,
    cast(rf.fen_sub_mss_tmp as timestamp(0)) as gw_end_snd_gw_ms,
    cast(rf.fen_dlv_mss_tmp as timestamp(0)) as gw_beg_snd_mi_ms,
    cast(rf.crt_dlv_mss_tmp as timestamp(0)) as gw_end_snd_mi_ms,
    cast(rf.fer_sub_fst_fts_tmp as timestamp(0)) as gw_beg_snd_gw,
    cast(rf.fer_sub_lst_fts_tmp as timestamp(0)) as gw_end_snd_gw,
    cast(rf.fer_dlv_fst_fts_tmp as timestamp(0)) as gw_beg_snd_mi, 
    cast(rf.fer_dlv_lst_fts_tmp as timestamp(0)) as gw_end_snd_mi,
    cast(rf.msrecvtime as timestamp(0)) as mi_beg_rcv_gw,
    cast(rf.receivetime as timestamp(0)) as mi_end_rcv_gw,
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
-- 01500_VI_FMS_SND.sql

CREATE OR REPLACE VIEW VI_FMS_SND
(LOCALBA_ID,REMOTEBA_ID,VFN,USERDATAREMOTE,STCODE_BA,STCODE_SYNC,STATUS_BA,STATUS_INFO,SEND_ERROR_TIMESTAMP,COMPLETE,FNAME,FSIZE,FHASH,TUR,MESSAGETYPE,CATAPPL,MESSAGELEN,MESSAGE,BA_INSERT_TIMESTAMP,MI_ACCEPT_BA,MI_BEG_SND_GW,GW_BEG_SND_GW_MS,GW_END_SND_GW_MS,GW_BEG_SND_MI_MS,GW_END_SND_MI_MS,MI_END_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,MSG_DIGEST_ALG,MSG_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    sf.localba_id,
    sf.remoteba_id,
    sf.vfn,
    sf.userdataremote,
    sf.stcode_ba,
    case
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and ab.id is NULL then 1
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and cft.interface_type in ('MQI-STD','MQI-XML') then 2
        else sf.stcode_sync
    end
    as stcode_sync,
    case 
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and ab.id is NULL then 'INVALID BA'
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and cft.interface_type in ('MQI-STD','MQI-XML')  then 'INVALID INTERFACE'
        else sf.status_ba
    end
    as status_ba,
    case 
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and ab.id is NULL then 'BA not configured in the SI-Std'
        when sf.stcode_ba = 0 and sf.stcode_sync = 0 and cft.interface_type in ('MQI-STD','MQI-XML') then 'Wrong Interface Type is configured in SI-Std'
        else sf.status_info
    end
    as status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094, -- Bug #12415
    cast(sf.send_error_timestamp as timestamp(0)) as send_error_timestamp,
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
    cast(sf.ba_insert_timestamp as timestamp(0)) as ba_insert_timestamp,
    cast(sf.accepttime as timestamp(0)) as mi_accept_ba,
    cast(sf.ftssendtime as timestamp(0)) as mi_beg_snd_gw,
    cast(sf.crt_sub_mss_tmp as timestamp(0)) as gw_beg_snd_gw_ms,
    cast(sf.fen_sub_mss_tmp as timestamp(0)) as gw_end_snd_gw_ms,
    cast(sf.fen_dlv_mss_tmp as timestamp(0)) as gw_beg_snd_mi_ms,
    cast(sf.crt_dlv_mss_tmp as timestamp(0)) as gw_end_snd_mi_ms,
    cast(sf.ftscompletetime as timestamp(0)) as mi_end_snd_gw,
    cast(sf.send_accepted_timestamp as timestamp(0)) as gw_beg_snd_gw,
    cast(sf.fer_sub_lst_fts_tmp as timestamp(0)) as gw_end_snd_gw,
    cast(sf.send_request_timestamp as timestamp(0)) as gw_beg_snd_mi, 
    cast(sf.send_completed_timestamp as timestamp(0)) as gw_end_snd_mi,
    cast(sf.notifytime as timestamp(0)) as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.msg_digest_alg,
    sf.msg_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SYNC_SEND sf
    --per estrarre i circuiti SIAnet
left outer join VCC_ABAB_SIANET ab on (ab.service='FT' and ab.local_ba=sf.localba_id and ab.remote_ba=sf.remoteba_id and ab.with_msg=1)
    -- per estrarre tipo interfaccia
left outer join VCL_FT cft on (sf.localba_id=cft.local_ba and sf.remoteba_id=cft.remote_ba and cft.with_msg=1)
/
-- *************************************************************************
-- VI_FTS_RCV
-- *************************************************************************
-- 01600_VI_FTS_RCV.sql

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
    to_timestamp(substr(rf.start_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone as mi_beg_snd_gw,
    --first_del_time, eliminare gli ultimi 5 caratteri del timezone
    to_timestamp(substr(rf.first_del_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone as gw_beg_snd_gw,
    --last_del_time, eliminare gli ultimi 5 caratteri del timezone
    to_timestamp(substr(rf.last_del_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone as gw_end_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(recv_request_timestamp as timestamp(0)) as gw_beg_snd_mi,
    cast(recv_complete_timestamp as timestamp(0)) as gw_end_snd_mi,
    cast(recv_delivered_timestamp as timestamp(0)) as mi_beg_rcv_gw,
    --eas_compl_time, eliminare gli ultimi 5 caratteri del timezone
    to_timestamp(substr(rf.eas_compl_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone as mi_end_rcv_gw,
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
-- 01700_VI_FTS_SND.sql

CREATE OR REPLACE VIEW VI_FTS_SND
(LOCAL_BA,REMOTE_BA,VFN,STSCODE,STATUS,STATUS_INFO,SEND_ERROR_TIMESTAMP,APPL_CHECK,COMPLETE,FNAME,APPLICATIVE_DATA_FIELD,FSIZE,FMD5,BA_INSERT_TIMESTAMP,MI_ACCEPT_BA,MI_BEG_SND_GW,MI_END_SND_GW,GW_BEG_SND_GW,GW_END_SND_GW,GW_BEG_SND_MI,GW_END_SND_MI,MI_BEG_RCV_GW,FILE_DIGEST_ALG,FILE_DIGEST,LOCAL_AUTH_INFO_ALG,LOCAL_AUTH_INFO)
AS
select
    sf.local_ba,
    sf.remote_ba,
    sf.vfn,
    case
        when sf.stscode = 90 and ab.id is NULL then 91
        when sf.stscode = 90 and cft.interface_type in ('MQI-STD','MQI-XML') then 92
        else sf.stscode
    end
    as stscode,
    case 
        when sf.stscode = 90 and ab.id is NULL then 'INVALID BA'
        when sf.stscode = 90 and cft.interface_type in ('MQI-STD','MQI-XML') then 'INVALID INTERFACE'
        else sf.status
    end
    as status,
    case 
        when sf.stscode = 90 and ab.id is NULL then 'BA not configured in the SI-Std'
        when sf.stscode = 90 and cft.interface_type in ('MQI-STD','MQI-XML') then 'Wrong Interface Type is configured in SI-Std'
        else sf.status_info
    end
    as status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(sf.send_error_timestamp as timestamp(0)) as send_error_timestamp,
    sf.appl_check,
    sf.complete,
    sf.fname,
    sf.applicative_data_field,
    sf.fsize,
    sf.fmd5,
    cast(sf.ba_insert_timestamp as timestamp(0)) as ba_insert_timestamp,
    cast(to_timestamp(sf.createdate,'YYYY/MM/DD HH24:MI:SS') as timestamp(0)) as mi_accept_ba,
    -- start_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_timestamp(substr(sf.start_time, 0,12),'YYMMDDHH24MISS') as timestamp(0)) as mi_beg_snd_gw,
    -- eas_compl_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_timestamp(substr(sf.eas_compl_time, 0,12),'YYMMDDHH24MISS') as timestamp(0)) as mi_end_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(sf.send_accepted_timestamp as timestamp(0)) as gw_beg_snd_gw,
    cast(sf.send_gft_request_timestamp as timestamp(0))  as gw_end_snd_gw,
    cast(sf.send_request_timestamp as timestamp(0))  as gw_beg_snd_mi,
    cast(sf.send_confirmed_timestamp as timestamp(0))  as gw_end_snd_mi,
    cast(sf.send_completed_timestamp as timestamp(0)) as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SEND_FILE sf
    --per estrarre i circuiti SIAnet
left outer join VCC_ABAB_SIANET ab on (ab.service='FT' and ab.local_ba=sf.local_ba and ab.remote_ba=sf.remote_ba and ab.with_msg=0)
    -- per estrarre tipo interfaccia
left outer join VCL_FT cft on (sf.local_ba=cft.local_ba and sf.remote_ba=cft.remote_ba and cft.with_msg=0)
/
-- *************************************************************************
-- VI_MSS_RCV
-- *************************************************************************
-- 01800_VI_MSS_RCV.sql

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
    to_timestamp(substr(trf.eas_sub_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone          as mi_beg_snd_gw,
    to_timestamp(substr(trf.fer_sub_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone          as gw_beg_snd_gw,
    to_timestamp(substr(trf.first_bar_sub_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone    as gw_end_snd_gw,
    to_timestamp(substr(trf.fen_del_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone          as gw_beg_snd_mi,
    to_timestamp(substr(trf.fer_del_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone          as gw_end_snd_mi,
    to_timestamp(substr(trf.last_bar_sub_time, 0,12),'YYMMDDHH24MISS')::timestamp without time zone     as mi_beg_rcv_gw,
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
-- 01900_VI_MSS_SND.sql

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
    case
        when trf.stscode = 0 and ab.id is NULL            then 1101
        when trf.stscode = 0 and cms.interface_type in ('MQI-STD','MQI-XML') then 1102
        else trf.stscode
    end
    as stscode,
    case 
        when trf.stscode = 0 and ab.id is NULL            then 'INVALID BA'
        when trf.stscode = 0 and cms.interface_type in ('MQI-STD','MQI-XML') then 'INVALID INTERFACE'
        else trf.status
    end
    as status,
    case 
        when trf.stscode = 0 and ab.id is NULL            then 'BA not configured in the SI-Std'
        when trf.stscode = 0 and cms.interface_type in ('MQI-STD','MQI-XML') then 'Wrong Interface Type is configured in SI-Std'
        else trf.status_info
    end
    as status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(trf.send_err_timestamp as timestamp(0))                                  as send_err_timestamp,
    trf.appl_check,
    trf.complete,
    trf.msgsize,
    trf.mab,
    cast(trf.ba_insert_timestamp as timestamp(0))                                 as ba_insert_timestamp,
    cast(to_timestamp(trf.createdate,'YYYY/MM/DD HH24:MI:SS') as timestamp(0))                       as mi_accept_ba,
    -- first_eas_sub_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_timestamp(substr(trf.first_eas_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp(0))        as mi_beg_snd_gw,
    -- fer_sub_time, eliminare gli ultimi 5 caratteri del timezone
    cast(to_timestamp(substr(trf.fer_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp(0))              as gw_beg_snd_gw,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    cast(trf.send_req_timestamp as timestamp(0))                                  as gw_end_snd_gw,
    cast(to_timestamp(substr(trf.fen_del_time, 0,12),'YYMMDDHH24MISS') as timestamp(0))              as gw_beg_snd_mi,
    cast(to_timestamp(substr(trf.fer_del_time, 0,12),'YYMMDDHH24MISS') as timestamp(0))              as gw_end_snd_mi,
    cast(to_timestamp(substr(trf.last_eas_sub_time, 0,12),'YYMMDDHH24MISS') as timestamp(0))         as mi_beg_rcv_gw,
    trf.msg_digest_alg,
    trf.msg_digest,
    trf.local_auth_info_alg,
    trf.local_auth_info
from
    FAS_MSG_SEND trf
    --per estrarre i circuiti SIAnet
left outer join VCC_ABAB_SIANET ab on (ab.service='MS' and ab.local_ba=trf.local_ba and ab.remote_ba=trf.remote_ba)
    -- per estrarre tipo interfaccia
left outer join VCL_MS cms         on (trf.local_ba=cms.local_ba and trf.remote_ba=cms.remote_ba)
/