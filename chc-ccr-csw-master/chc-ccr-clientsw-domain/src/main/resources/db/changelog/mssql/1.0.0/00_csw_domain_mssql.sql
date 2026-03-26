-- SQLSERVER

-- *************************************************************************
-- Fas_msg_send
-- *************************************************************************
CREATE TABLE [FAS_MSG_SEND](
	[local_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[msg_type] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[cat_appl] [varchar](4) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[tur] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[msgid] [varchar](30) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ref] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[priority] [int] NULL,
	[certf_req] [int] NULL,
	[stscode] [int] NOT NULL,
	[status] [varchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[easstatus] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[complete] [int] NULL,
	[appl_check] [int] NOT NULL,
	[createdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[lastupdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[update_mark] [int] NULL,
	[seqid] [int] NULL,
	[msgsize] [int] NULL,
	[mab] [varbinary](max) NULL,
	[ba_req_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[bar_acq_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[femsi_retry_number] [int] NULL,
	[first_eas_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[last_eas_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fer_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fen_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fer_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ba_insert_timestamp] [datetime] NULL,
	[load_timestamp] [datetime] NULL,
	[send_req_timestamp] [datetime] NULL,
	[send_err_timestamp] [datetime] NULL,
	[send_cnf_timestamp] [datetime] NULL,
	[send_sc_timestamp] [datetime] NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fas_seqid] [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[net_msgid] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_fas_msg_send] PRIMARY KEY CLUSTERED 
(
	[fas_seqid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [uk_FasMsgSendR6CUFASMSGSEND] UNIQUE NONCLUSTERED 
(
	[local_ba] ASC,
	[msgid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/


CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_SEND_1] ON [FAS_MSG_SEND] 
(
	[stscode] ASC,
	[fas_seqid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_SEND_2] ON [FAS_MSG_SEND] 
(
	[complete] ASC,
	[stscode] ASC,
	[lastupdate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_SEND_3] ON [FAS_MSG_SEND] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_SEND_AB] ON [FAS_MSG_SEND] 
(
	[appl_check] ASC,
	[stscode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [DF_FAS_MSG_SEND_PRIORITY]  DEFAULT ((0)) FOR [priority]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [DF_FAS_MSG_SEND_STSCODE]  DEFAULT ((0)) FOR [stscode]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [DF_FAS_MSG_SEND_STATUS]  DEFAULT ('NEW TRAFFIC') FOR [status]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [DF_FAS_MSG_SEND_COMPLETE]  DEFAULT ((0)) FOR [complete]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [DF_FAS_MSG_SEND_APPL_CHECK]  DEFAULT ((0)) FOR [appl_check]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [df_fas_msg_send_ba_insert_timestamp]  DEFAULT (getutcdate()) FOR [ba_insert_timestamp]
/
ALTER TABLE [FAS_MSG_SEND] ADD  CONSTRAINT [df_fas_msg_send_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [FAS_MSG_SEND]  WITH CHECK ADD  CONSTRAINT [ck_fas_msg_send_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [FAS_MSG_SEND] CHECK CONSTRAINT [ck_fas_msg_send_cleanup_status]
/
ALTER TABLE [FAS_MSG_SEND]  WITH CHECK ADD  CONSTRAINT [ck_fas_msg_send_cleanup_type] CHECK  (([cleanup_type]='IRL_MSS_SND'))
/
ALTER TABLE [FAS_MSG_SEND] CHECK CONSTRAINT [ck_fas_msg_send_cleanup_type]
/
ALTER TABLE [FAS_MSG_SEND]  WITH CHECK ADD  CONSTRAINT [CK_FAS_MSG_SEND_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL OR [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [FAS_MSG_SEND] CHECK CONSTRAINT [CK_FAS_MSG_SEND_LAU_ALG]
/
ALTER TABLE [FAS_MSG_SEND]  WITH CHECK ADD  CONSTRAINT [CK_FAS_MSG_SEND_MDIGEST_ALG] CHECK  (([MSG_DIGEST_ALG] IS NULL AND [MSG_DIGEST] IS NULL OR [MSG_DIGEST_ALG] IS NOT NULL AND [MSG_DIGEST] IS NOT NULL AND [MSG_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [FAS_MSG_SEND] CHECK CONSTRAINT [CK_FAS_MSG_SEND_MDIGEST_ALG]
/


-- *************************************************************************
-- Fas_msg_recv
-- *************************************************************************
CREATE TABLE [FAS_MSG_RECV](
	[local_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[msg_type] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[cat_appl] [varchar](4) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[tur] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[msgid] [varchar](30) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ref] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[priority] [int] NULL,
	[certf_req] [int] NULL,
	[stscode] [int] NOT NULL,
	[status] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[easstatus] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[complete] [int] NULL,
	[appl_check] [int] NOT NULL,
	[createdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[lastupdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[update_mark] [int] NULL,
	[seqid] [bigint] NOT NULL,
	[msgsize] [int] NULL,
	[mab] [varbinary](max) NULL,
	[ba_req_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[bar_acq_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fer_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fen_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fer_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[first_bar_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[last_bar_sub_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[recv_rnc_timestamp] [datetime] NULL,
	[recv_rc_timestamp] [datetime] NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[net_msgid] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_fas_msg_recv] PRIMARY KEY CLUSTERED 
(
	[seqid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [uk_FasMsgRecvR6CUFASMSGRECV] UNIQUE NONCLUSTERED 
(
	[local_ba] ASC,
	[msgid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/

CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_RECV_1] ON [FAS_MSG_RECV] 
(
	[stscode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_RECV_2] ON [FAS_MSG_RECV] 
(
	[complete] ASC,
	[stscode] ASC,
	[lastupdate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_RECV_3] ON [FAS_MSG_RECV] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_FAS_MSG_RECV_AB] ON [FAS_MSG_RECV] 
(
	[appl_check] ASC,
	[stscode] ASC,
	[local_ba] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [FAS_MSG_RECV] ADD  CONSTRAINT [df_fas_msg_recv_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [FAS_MSG_RECV]  WITH CHECK ADD  CONSTRAINT [ck_fas_msg_recv_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [FAS_MSG_RECV] CHECK CONSTRAINT [ck_fas_msg_recv_cleanup_status]
/
ALTER TABLE [FAS_MSG_RECV]  WITH CHECK ADD  CONSTRAINT [ck_fas_msg_recv_cleanup_type] CHECK  (([cleanup_type]='IRL_MSS_RCV'))
/
ALTER TABLE [FAS_MSG_RECV] CHECK CONSTRAINT [ck_fas_msg_recv_cleanup_type]
/
ALTER TABLE [FAS_MSG_RECV]  WITH CHECK ADD  CONSTRAINT [CK_FAS_MSG_RECV_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL OR [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [FAS_MSG_RECV] CHECK CONSTRAINT [CK_FAS_MSG_RECV_LAU_ALG]
/
ALTER TABLE [FAS_MSG_RECV]  WITH CHECK ADD  CONSTRAINT [CK_FAS_MSG_RECV_MDIGEST_ALG] CHECK  (([MSG_DIGEST_ALG] IS NULL AND [MSG_DIGEST] IS NULL OR [MSG_DIGEST_ALG] IS NOT NULL AND [MSG_DIGEST] IS NOT NULL AND [MSG_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [FAS_MSG_RECV] CHECK CONSTRAINT [CK_FAS_MSG_RECV_MDIGEST_ALG]
/

-- *************************************************************************
-- Sync_send
-- *************************************************************************
CREATE TABLE [SYNC_SEND](
	[localba_id] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remoteba_id] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[vfn] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[status_ba] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[status_sync] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[stcode_ba] [int] NOT NULL,
	[stcode_sync] [int] NOT NULL,
	[fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[retry_cnt] [int] NULL,
	[userdataremote] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[priority] [int] NULL,
	[bamsg_id] [varchar](30) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[message] [varbinary](max) NULL,
	[accepttime] [datetime] NULL,
	[ftssendtime] [datetime] NULL,
	[ftscompletetime] [datetime] NULL,
	[mssendtime] [datetime] NULL,
	[mscompletetime] [datetime] NULL,
	[notifytime] [datetime] NULL,
	[modtime] [datetime] NOT NULL,
	[userdataremotelen] [int] NULL,
	[tur] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[messagelen] [int] NULL,
	[messagetype] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[catappl] [varchar](4) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[errortime] [datetime] NULL,
	[fer_sub_fst_fts_tmp] [datetime] NULL,
	[fer_sub_lst_fts_tmp] [datetime] NULL,
	[fer_dlv_fst_fts_tmp] [datetime] NULL,
	[fer_dlv_lst_fts_tmp] [datetime] NULL,
	[fen_sub_mss_tmp] [datetime] NULL,
	[fen_dlv_mss_tmp] [datetime] NULL,
	[crt_sub_mss_tmp] [datetime] NULL,
	[crt_dlv_mss_tmp] [datetime] NULL,
	[datasetname] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[local_data] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[certf_req] [int] NULL,
	[fsize] [bigint] NULL,
	[fhash] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fblockmoved] [int] NULL,
	[fmap] [varchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[complete] [int] NULL,
	[last_update] [int] NULL,
	[ba_insert_timestamp] [datetime] NULL,
	[send_accepted_timestamp] [datetime] NULL,
	[send_request_timestamp] [datetime] NULL,
	[send_confirmed_timestamp] [datetime] NULL,
	[send_completed_timestamp] [datetime] NULL,
	[send_error_timestamp] [datetime] NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[id_sync_send] [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[operation_timestamp] [datetime] NULL,
	[operation] [varchar](10) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[reactivate] [varchar](10) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_sync_send] PRIMARY KEY CLUSTERED 
(
	[id_sync_send] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [uk_sync_send_vfn] UNIQUE NONCLUSTERED 
(
	[vfn] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/

CREATE NONCLUSTERED INDEX [IDX_SYNC_SEND_1] ON [SYNC_SEND] 
(
	[stcode_ba] ASC,
	[stcode_sync] ASC,
	[id_sync_send] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SYNC_SEND_2] ON [SYNC_SEND] 
(
	[complete] ASC,
	[stcode_ba] ASC,
	[stcode_sync] ASC,
	[last_update] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SYNC_SEND_3] ON [SYNC_SEND] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_STATUS_BA]  DEFAULT ('SUBMITTED') FOR [status_ba]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_STCODE_BA]  DEFAULT ((0)) FOR [stcode_ba]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_STCODE_SYNC]  DEFAULT ((0)) FOR [stcode_sync]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_PRIORITY]  DEFAULT ((0)) FOR [priority]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_MODTIME]  DEFAULT (getutcdate()) FOR [modtime]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [DF_SYNC_SEND_COMPLETE]  DEFAULT ((0)) FOR [complete]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [df_sync_send_ba_insert_timestamp]  DEFAULT (getutcdate()) FOR [ba_insert_timestamp]
/
ALTER TABLE [SYNC_SEND] ADD  CONSTRAINT [df_sync_send_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [SYNC_SEND]  WITH CHECK ADD  CONSTRAINT [ck_sync_send_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [SYNC_SEND] CHECK CONSTRAINT [ck_sync_send_cleanup_status]
/
ALTER TABLE [SYNC_SEND]  WITH CHECK ADD  CONSTRAINT [ck_sync_send_cleanup_type] CHECK  (([cleanup_type]='IRL_FMS_SND'))
/
ALTER TABLE [SYNC_SEND] CHECK CONSTRAINT [ck_sync_send_cleanup_type]
/
ALTER TABLE [SYNC_SEND]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_SEND_FDIGEST_ALG] CHECK  (([FILE_DIGEST_ALG] IS NULL AND [FILE_DIGEST] IS NULL OR [FILE_DIGEST_ALG] IS NOT NULL AND [FILE_DIGEST] IS NOT NULL AND [FILE_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [SYNC_SEND] CHECK CONSTRAINT [CK_SYNC_SEND_FDIGEST_ALG]
/
ALTER TABLE [SYNC_SEND]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_SEND_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL OR [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [SYNC_SEND] CHECK CONSTRAINT [CK_SYNC_SEND_LAU_ALG]
/
ALTER TABLE [SYNC_SEND]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_SEND_MDIGEST_ALG] CHECK  (([MSG_DIGEST_ALG] IS NULL AND [MSG_DIGEST] IS NULL OR [MSG_DIGEST_ALG] IS NOT NULL AND [MSG_DIGEST] IS NOT NULL AND [MSG_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [SYNC_SEND] CHECK CONSTRAINT [CK_SYNC_SEND_MDIGEST_ALG]
/


-- *************************************************************************
-- Sync_recv
-- *************************************************************************
CREATE TABLE [SYNC_RECV](
	[localba_id] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remoteba_id] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[vfn] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[status_ba] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[status_sync] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[stcode_ba] [int] NULL,
	[stcode_sync] [int] NULL,
	[timeout] [int] NULL,
	[ftsrecvtime] [datetime] NULL,
	[msrecvtime] [datetime] NULL,
	[fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[userdataremote] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[priority] [int] NULL,
	[messagetype] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[modtime] [datetime] NULL,
	[message] [varbinary](max) NULL,
	[messagelength] [int] NULL,
	[cat_apply] [varchar](4) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[tur] [varchar](16) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[bamsgid] [varchar](30) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[receivetime] [datetime] NULL,
	[ftsinserttime] [datetime] NULL,
	[msconftime] [datetime] NULL,
	[ftsdelivtime] [datetime] NULL,
	[ba_processed_tmp] [datetime] NULL,
	[datasetname] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fer_sub_fst_fts_tmp] [datetime] NULL,
	[fer_sub_lst_fts_tmp] [datetime] NULL,
	[fer_dlv_fst_fts_tmp] [datetime] NULL,
	[fer_dlv_lst_fts_tmp] [datetime] NULL,
	[fen_sub_mss_tmp] [datetime] NULL,
	[fen_dlv_mss_tmp] [datetime] NULL,
	[crt_sub_mss_tmp] [datetime] NULL,
	[crt_dlv_mss_tmp] [datetime] NULL,
	[read_str_fts_tmp] [datetime] NULL,
	[read_end_fts_tmp] [datetime] NULL,
	[local_data] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[certf_req] [int] NULL,
	[start_recv_gmt] [datetime] NULL,
	[fsize] [bigint] NULL,
	[fhash] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fblockmoved] [int] NULL,
	[fmap] [varchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[complete] [int] NULL,
	[last_update] [int] NULL,
	[read_request_timestamp] [datetime] NULL,
	[read_confirmed_timestamp] [datetime] NULL,
	[read_completed_timestamp] [datetime] NULL,
	[read_notified_timestamp] [datetime] NULL,
	[read_error_timestamp] [datetime] NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[file_format] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[compression_algo] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fas_seqid] [bigint] NOT NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MSG_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_sync_recv] PRIMARY KEY CLUSTERED 
(
	[fas_seqid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [UK_SYNCRECVLOCALBAIDREMOTEB_1] UNIQUE NONCLUSTERED 
(
	[vfn] ASC,
	[localba_id] ASC,
	[remoteba_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/

CREATE NONCLUSTERED INDEX [IDX_SYNC_RECV_1] ON [SYNC_RECV] 
(
	[stcode_ba] ASC,
	[stcode_sync] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SYNC_RECV_2] ON [SYNC_RECV] 
(
	[complete] ASC,
	[stcode_ba] ASC,
	[stcode_sync] ASC,
	[last_update] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SYNC_RECV_3] ON [SYNC_RECV] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [SYNC_RECV] ADD  CONSTRAINT [df_sync_recv_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [SYNC_RECV]  WITH CHECK ADD  CONSTRAINT [ck_sync_recv_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [SYNC_RECV] CHECK CONSTRAINT [ck_sync_recv_cleanup_status]
/
ALTER TABLE [SYNC_RECV]  WITH CHECK ADD  CONSTRAINT [ck_sync_recv_cleanup_type] CHECK  (([cleanup_type]='IRL_FMS_RCV'))
/
ALTER TABLE [SYNC_RECV] CHECK CONSTRAINT [ck_sync_recv_cleanup_type]
/
ALTER TABLE [SYNC_RECV]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_RECV_FDIGEST_ALG] CHECK  (([FILE_DIGEST_ALG] IS NULL AND [FILE_DIGEST] IS NULL OR [FILE_DIGEST_ALG] IS NOT NULL AND [FILE_DIGEST] IS NOT NULL AND [FILE_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [SYNC_RECV] CHECK CONSTRAINT [CK_SYNC_RECV_FDIGEST_ALG]
/
ALTER TABLE [SYNC_RECV]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_RECV_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL OR [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [SYNC_RECV] CHECK CONSTRAINT [CK_SYNC_RECV_LAU_ALG]
/
ALTER TABLE [SYNC_RECV]  WITH CHECK ADD  CONSTRAINT [CK_SYNC_RECV_MDIGEST_ALG] CHECK  (([MSG_DIGEST_ALG] IS NULL AND [MSG_DIGEST] IS NULL OR [MSG_DIGEST_ALG] IS NOT NULL AND [MSG_DIGEST] IS NOT NULL AND [MSG_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [SYNC_RECV] CHECK CONSTRAINT [CK_SYNC_RECV_MDIGEST_ALG]
/


-- *************************************************************************
-- Send_file
-- *************************************************************************
CREATE TABLE [SEND_FILE](
	[vfn] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[local_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[status] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[easstatus] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[stscode] [int] NOT NULL,
	[complete] [int] NOT NULL,
	[transp_type] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[createdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[lastupdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[appl_check] [int] NOT NULL,
	[original_fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[retry_cnt] [int] NULL,
	[update_mark] [int] NULL,
	[fsize] [bigint] NULL,
	[fmd5] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fblkmoved] [int] NULL,
	[filemap] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[operation_timestamp] [datetime] NULL,
	[operation] [varchar](10) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[reactivate] [varchar](10) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ref_date] [varchar](6) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[act_req_time] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_acq_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[queue_ins_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[start_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_compl_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ba_proc_time] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_elab_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ba_insert_timestamp] [datetime] NULL,
	[send_accepted_timestamp] [datetime] NULL,
	[send_gft_request_timestamp] [datetime] NULL,
	[send_request_timestamp] [datetime] NULL,
	[send_confirmed_timestamp] [datetime] NULL,
	[send_completed_timestamp] [datetime] NULL,
	[send_error_timestamp] [datetime] NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[id_send_file] [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[applicative_data_field] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[applicative_data_field_length] [int] NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_FS] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_send_file] PRIMARY KEY CLUSTERED 
(
	[id_send_file] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [uk_send_file_vfn] UNIQUE NONCLUSTERED 
(
	[vfn] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/

CREATE NONCLUSTERED INDEX [idx_send_file_1] ON [SEND_FILE] 
(
	[stscode] ASC,
	[id_send_file] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SEND_FILE_2] ON [SEND_FILE] 
(
	[complete] ASC,
	[stscode] ASC,
	[lastupdate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SEND_FILE_3] ON [SEND_FILE] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_SEND_FILE_AB] ON [SEND_FILE] 
(
	[appl_check] ASC,
	[stscode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [DF_SEND_FILE_STATUS]  DEFAULT ('FILE TO BE PROCESSED') FOR [status]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [DF_SEND_FILE_STSCODE]  DEFAULT ((90)) FOR [stscode]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [DF_SEND_FILE_COMPLETE]  DEFAULT ((0)) FOR [complete]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [DF_SEND_FILE_TRANSP_TYPE]  DEFAULT ('EAS') FOR [transp_type]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [DF_SEND_FILE_APPL_CHECK]  DEFAULT ((0)) FOR [appl_check]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [df_send_file_ba_insert_timestamp]  DEFAULT (getutcdate()) FOR [ba_insert_timestamp]
/
ALTER TABLE [SEND_FILE] ADD  CONSTRAINT [df_send_file_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [SEND_FILE]  WITH CHECK ADD  CONSTRAINT [ck_send_file_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [SEND_FILE] CHECK CONSTRAINT [ck_send_file_cleanup_status]
/
ALTER TABLE [SEND_FILE]  WITH CHECK ADD  CONSTRAINT [ck_send_file_cleanup_type] CHECK  (([cleanup_type]='IRL_FTS_SND'))
/
ALTER TABLE [SEND_FILE] CHECK CONSTRAINT [ck_send_file_cleanup_type]
/
ALTER TABLE [SEND_FILE]  WITH CHECK ADD  CONSTRAINT [CK_SEND_FILE_FDIGEST_ALG] CHECK  (([FILE_DIGEST_ALG] IS NULL AND [FILE_DIGEST] IS NULL OR [FILE_DIGEST_ALG] IS NOT NULL AND [FILE_DIGEST] IS NOT NULL AND [FILE_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [SEND_FILE] CHECK CONSTRAINT [CK_SEND_FILE_FDIGEST_ALG]
/
ALTER TABLE [SEND_FILE]  WITH CHECK ADD  CONSTRAINT [CK_SEND_FILE_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL AND [LOCAL_AUTH_INFO_FS] IS NULL OR [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_FS] IS NULL AND [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256' OR [LOCAL_AUTH_INFO] IS NULL AND [LOCAL_AUTH_INFO_FS] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [SEND_FILE] CHECK CONSTRAINT [CK_SEND_FILE_LAU_ALG]
/


-- *************************************************************************
-- Recv_file
-- *************************************************************************
CREATE TABLE [RECV_FILE](
	[vfn] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[local_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[remote_ba] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[status] [varchar](60) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[easstatus] [varchar](3) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[stscode] [int] NOT NULL,
	[complete] [int] NOT NULL,
	[transp_type] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[createdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[lastupdate] [varchar](24) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[appl_check] [int] NOT NULL,
	[original_fname] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[retry_cnt] [int] NULL,
	[update_mark] [int] NULL,
	[fsize] [bigint] NULL,
	[fmd5] [varchar](32) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[fblkmoved] [int] NULL,
	[filemap] [varchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ref_date] [varchar](6) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[act_req_time] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_acq_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[queue_ins_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[start_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_compl_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[first_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[last_del_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[first_read_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[last_read_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ba_proc_time] [varchar](12) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[eas_elab_time] [varchar](17) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[recv_request_timestamp] [datetime] NULL,
	[recv_error_timestamp] [datetime] NULL,
	[recv_complete_timestamp] [datetime] NULL,
	[recv_delivered_timestamp] [datetime] NULL,
	[fas_seqid] [bigint] NOT NULL,
	[status_info] [varchar](1024) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[applicative_data_field] [varchar](80) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[cleanup_status] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[cleanup_lot] [bigint] NULL,
	[cleanup_type] [varchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILE_DIGEST] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_ALG] [varchar](8) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCAL_AUTH_INFO_FS] [varchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [pk_recv_file] PRIMARY KEY CLUSTERED 
(
	[fas_seqid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX],
 CONSTRAINT [uk_RecvFileVfnLocalBaRemoteBa] UNIQUE NONCLUSTERED 
(
	[vfn] ASC,
	[local_ba] ASC,
	[remote_ba] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
) ON [${DB_USER}._INDEX]
/

CREATE NONCLUSTERED INDEX [IDX_RECV_FILE_1] ON [RECV_FILE] 
(
	[stscode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_RECV_FILE_2] ON [RECV_FILE] 
(
	[complete] ASC,
	[stscode] ASC,
	[lastupdate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_RECV_FILE_3] ON [RECV_FILE] 
(
	[cleanup_status] ASC,
	[cleanup_type] ASC,
	[cleanup_lot] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/
CREATE NONCLUSTERED INDEX [IDX_RECV_FILE_AB] ON [RECV_FILE] 
(
	[appl_check] ASC,
	[stscode] ASC,
	[local_ba] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [${DB_USER}._INDEX]
/

ALTER TABLE [RECV_FILE] ADD  CONSTRAINT [df_recv_file_cleanup_status]  DEFAULT ('FREE') FOR [cleanup_status]
/

ALTER TABLE [RECV_FILE]  WITH CHECK ADD  CONSTRAINT [ck_recv_file_cleanup_status] CHECK  (([cleanup_status]='DELETABLE' OR [cleanup_status]='PARTIAL_DELETED' OR [cleanup_status]='EXPORTED' OR [cleanup_status]='MARKED' OR [cleanup_status]='FREE'))
/
ALTER TABLE [RECV_FILE] CHECK CONSTRAINT [ck_recv_file_cleanup_status]
/
ALTER TABLE [RECV_FILE]  WITH CHECK ADD  CONSTRAINT [ck_recv_file_cleanup_type] CHECK  (([cleanup_type]='IRL_FTS_RCV'))
/
ALTER TABLE [RECV_FILE] CHECK CONSTRAINT [ck_recv_file_cleanup_type]
/
ALTER TABLE [RECV_FILE]  WITH CHECK ADD  CONSTRAINT [CK_RECV_FILE_FDIGEST_ALG] CHECK  (([FILE_DIGEST_ALG] IS NULL AND [FILE_DIGEST] IS NULL OR [FILE_DIGEST_ALG] IS NOT NULL AND [FILE_DIGEST] IS NOT NULL AND [FILE_DIGEST_ALG]='SHA-256'))
/
ALTER TABLE [RECV_FILE] CHECK CONSTRAINT [CK_RECV_FILE_FDIGEST_ALG]
/
ALTER TABLE [RECV_FILE]  WITH CHECK ADD  CONSTRAINT [CK_RECV_FILE_LAU_ALG] CHECK  (([LOCAL_AUTH_INFO_ALG] IS NULL AND [LOCAL_AUTH_INFO] IS NULL AND [LOCAL_AUTH_INFO_FS] IS NULL OR [LOCAL_AUTH_INFO] IS NOT NULL AND [LOCAL_AUTH_INFO_FS] IS NULL AND [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256' OR [LOCAL_AUTH_INFO] IS NULL AND [LOCAL_AUTH_INFO_FS] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG] IS NOT NULL AND [LOCAL_AUTH_INFO_ALG]='HS256'))
/
ALTER TABLE [RECV_FILE] CHECK CONSTRAINT [CK_RECV_FILE_LAU_ALG]
/
-- *************************************************************************
-- VI_FMS_RCV
-- *************************************************************************
-- 03600_VI_FMS_RCV.sql

IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_FMS_RCV')
DROP VIEW VI_FMS_RCV
/
create view VI_FMS_RCV as
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
    convert(datetime, convert(varchar(32), rf.ftsdelivtime, 120), 120) as mi_beg_snd_gw,
    convert(datetime, convert(varchar(32), rf.crt_sub_mss_tmp, 120), 120) as gw_beg_snd_gw_ms,
    convert(datetime, convert(varchar(32), rf.fen_sub_mss_tmp, 120), 120) as gw_end_snd_gw_ms,
    convert(datetime, convert(varchar(32), rf.fen_dlv_mss_tmp, 120), 120) as gw_beg_snd_mi_ms,
    convert(datetime, convert(varchar(32), rf.crt_dlv_mss_tmp, 120), 120) as gw_end_snd_mi_ms,
    convert(datetime, convert(varchar(32), rf.fer_sub_fst_fts_tmp, 120), 120) as gw_beg_snd_gw,
    convert(datetime, convert(varchar(32), rf.fer_sub_lst_fts_tmp, 120), 120) as gw_end_snd_gw,
    convert(datetime, convert(varchar(32), rf.fer_dlv_fst_fts_tmp, 120), 120) as gw_beg_snd_mi, 
    convert(datetime, convert(varchar(32), rf.fer_dlv_lst_fts_tmp, 120), 120) as gw_end_snd_mi,
    convert(datetime, convert(varchar(32), rf.msrecvtime, 120), 120) as mi_beg_rcv_gw,
    convert(datetime, convert(varchar(32), rf.receivetime, 120), 120) as mi_end_rcv_gw,
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
-- 03700_VI_FMS_SND.sql

IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_FMS_SND')
DROP VIEW VI_FMS_SND
/
--/
create view VI_FMS_SND as
select
    sf.localba_id,
    sf.remoteba_id,
    sf.vfn,
    sf.userdataremote,
    sf.stcode_ba,
    sf.stcode_sync as stcode_sync,
    sf.status_ba as status_ba,
    sf.status_info status_info,
    -- troncare al secondo ma il tipo deve restare timestamp #8094
    convert(datetime, convert(varchar(32), sf.send_error_timestamp, 120), 120) as send_error_timestamp,
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
    convert(datetime, convert(varchar(32), sf.ba_insert_timestamp, 120), 120) as ba_insert_timestamp,
    convert(datetime, convert(varchar(32), sf.accepttime, 120), 120) as mi_accept_ba,
    convert(datetime, convert(varchar(32), sf.ftssendtime, 120), 120) as mi_beg_snd_gw,
    convert(datetime, convert(varchar(32), sf.crt_sub_mss_tmp, 120), 120) as gw_beg_snd_gw_ms,
    convert(datetime, convert(varchar(32), sf.fen_sub_mss_tmp, 120), 120) as gw_end_snd_gw_ms,
    convert(datetime, convert(varchar(32), sf.fen_dlv_mss_tmp, 120), 120) as gw_beg_snd_mi_ms,
    convert(datetime, convert(varchar(32), sf.crt_dlv_mss_tmp, 120), 120) as gw_end_snd_mi_ms,
    convert(datetime, convert(varchar(32), sf.ftscompletetime, 120), 120) as mi_end_snd_gw,
    convert(datetime, convert(varchar(32), sf.send_accepted_timestamp, 120), 120) as gw_beg_snd_gw,
    convert(datetime, convert(varchar(32), sf.fer_sub_lst_fts_tmp, 120), 120) as gw_end_snd_gw,
    convert(datetime, convert(varchar(32), sf.send_request_timestamp, 120), 120) as gw_beg_snd_mi, 
    convert(datetime, convert(varchar(32), sf.send_completed_timestamp, 120), 120) as gw_end_snd_mi,
    convert(datetime, convert(varchar(32), sf.notifytime, 120), 120) as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.msg_digest_alg,
    sf.msg_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SYNC_SEND sf
/   
IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_FTS_RCV')
DROP VIEW VI_FTS_RCV
/
create view [VI_FTS_RCV] as
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
    --
    --start_time, eliminare gli ultimi 5 caratteri del timezone
    -- cast(to_date(substr(rf.start_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_beg_snd_gw
    ( convert(datetime, substring(rf.start_time, 1, 6), 112)
    + convert(datetime, substring(rf.start_time, 7, 2) + ':' +
                        substring(rf.start_time, 9, 2) + ':' +
                        substring(rf.start_time, 11,2), 108)
    )  as mi_beg_snd_gw,
    --
    --first_del_time, eliminare gli ultimi 5 caratteri del timezone
    -- cast(to_date(substr(rf.first_del_time, 0,12),'YYMMDDHH24MISS') as timestamp) as gw_beg_snd_gw
    ( convert(datetime, substring(rf.first_del_time, 1, 6), 112)
    + convert(datetime, substring(rf.first_del_time, 7, 2) + ':' +
                        substring(rf.first_del_time, 9, 2) + ':' +
                        substring(rf.first_del_time, 11,2), 108)
    )  as gw_beg_snd_gw,
    --
    --last_del_time, eliminare gli ultimi 5 caratteri del timezone
    -- cast(to_date(substr(rf.last_del_time, 0,12),'YYMMDDHH24MISS') as timestamp) as gw_end_snd_gw,
    ( convert(datetime, substring(rf.last_del_time, 1, 6), 112)
    + convert(datetime, substring(rf.last_del_time, 7, 2) + ':' +
                        substring(rf.last_del_time, 9, 2) + ':' +
                        substring(rf.last_del_time, 11,2), 108)
    )  as gw_end_snd_gw,
    --
    -- troncare al secondo ma il tipo deve restare datetime #8094
    convert(datetime, convert(varchar(32), recv_request_timestamp, 120), 120)       as gw_beg_snd_mi,
    convert(datetime, convert(varchar(32), recv_complete_timestamp, 120), 120)      as gw_end_snd_mi,
    convert(datetime, convert(varchar(32), recv_delivered_timestamp, 120), 120)     as mi_beg_rcv_gw,
    --
    --eas_compl_time, eliminare gli ultimi 5 caratteri del timezone
    -- cast(to_date(substr(rf.eas_compl_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_end_rcv_gw,
    ( convert(datetime, substring(rf.eas_compl_time, 1, 6), 112)
    + convert(datetime, substring(rf.eas_compl_time, 7, 2) + ':' +
                        substring(rf.eas_compl_time, 9, 2) + ':' +
                        substring(rf.eas_compl_time, 11,2), 108)
    )  as mi_end_rcv_gw,
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
-- 03900_VI_FTS_SND.sql

IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_FTS_SND')
DROP VIEW VI_FTS_SND
/
create view [VI_FTS_SND] as
select
    sf.local_ba,
    sf.remote_ba,
    sf.vfn,
    sf.stscode as stscode,
    sf.status as status,
    sf.status_info as status_info,
    -- troncare al secondo ma il tipo deve restare datetime #8094
    convert(datetime, convert(varchar(32), sf.send_error_timestamp, 120), 120)  as send_error_timestamp,
    sf.appl_check,
    sf.complete,
    sf.fname,
    sf.applicative_data_field,
    sf.fsize,
    sf.fmd5,
    --
    convert(datetime, convert(varchar(32), sf.ba_insert_timestamp, 120), 120)          as ba_insert_timestamp,
    cast(replace(sf.createdate, '/', '-') as datetime)  as  mi_accept_ba,
    --
    --start_time, eliminare gli ultimi 5 caratteri del timezone
    -- cast(to_date(substr(sf.start_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_beg_snd_gw,
    ( convert(datetime, substring(sf.start_time, 1, 6), 112)
    + convert(datetime, substring(sf.start_time, 7, 2) + ':' +
                        substring(sf.start_time, 9, 2) + ':' +
                        substring(sf.start_time, 11,2), 108)
    )  as mi_beg_snd_gw,
    --
    -- cast(to_date(substr(sf.eas_compl_time, 0,12),'YYMMDDHH24MISS') as timestamp) as mi_end_snd_gw,
    ( convert(datetime, substring(sf.eas_compl_time, 1, 6), 112)
    + convert(datetime, substring(sf.eas_compl_time, 7, 2) + ':' +
                        substring(sf.eas_compl_time, 9, 2) + ':' +
                        substring(sf.eas_compl_time, 11,2), 108)
    )  as mi_end_snd_gw,
    --
    -- troncare al secondo ma il tipo deve restare datetime #8094
    convert(datetime, convert(varchar(32), sf.send_accepted_timestamp, 120), 120)       as gw_beg_snd_gw,
    convert(datetime, convert(varchar(32), sf.send_gft_request_timestamp, 120), 120)    as gw_end_snd_gw,
    convert(datetime, convert(varchar(32), sf.send_request_timestamp, 120), 120)        as gw_beg_snd_mi,
    convert(datetime, convert(varchar(32), sf.send_confirmed_timestamp, 120), 120)      as gw_end_snd_mi,
    convert(datetime, convert(varchar(32), sf.send_completed_timestamp, 120), 120)      as mi_beg_rcv_gw,
    sf.file_digest_alg,
    sf.file_digest,
    sf.local_auth_info_alg,
    sf.local_auth_info
from
    SEND_FILE   sf
/


-- *************************************************************************
-- VI_MSS_RCV
-- *************************************************************************
-- 04000_VI_MSS_RCV.sql

IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_MSS_RCV')
DROP VIEW VI_MSS_RCV
/
create view [VI_MSS_RCV] as
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
    --
    -- varchar(17), eliminare gli ultimi 5 caratteri del timezone e convertire in data
    ( convert(datetime, substring(trf.eas_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.eas_sub_time, 7, 2) + ':' +
                        substring(trf.eas_sub_time, 9, 2) + ':' +
                        substring(trf.eas_sub_time, 11,2), 108)
    )                                                                                  as mi_beg_snd_gw,
    --
    ( convert(datetime, substring(trf.fer_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.fer_sub_time, 7, 2) + ':' +
                        substring(trf.fer_sub_time, 9, 2) + ':' +
                        substring(trf.fer_sub_time, 11,2), 108)
    )                                                                                  as gw_beg_snd_gw,
    --
    ( convert(datetime, substring(trf.first_bar_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.first_bar_sub_time, 7, 2) + ':' +
                        substring(trf.first_bar_sub_time, 9, 2) + ':' +
                        substring(trf.first_bar_sub_time, 11,2), 108)
    )                                                                                  as gw_end_snd_gw,
    --
    ( convert(datetime, substring(trf.fen_del_time, 1, 6), 112)
    + convert(datetime, substring(trf.fen_del_time, 7, 2) + ':' +
                        substring(trf.fen_del_time, 9, 2) + ':' +
                        substring(trf.fen_del_time, 11,2), 108)
    )                                                                                  as gw_beg_snd_mi,
    --
    ( convert(datetime, substring(trf.fer_del_time, 1, 6), 112)
    + convert(datetime, substring(trf.fer_del_time, 7, 2) + ':' +
                        substring(trf.fer_del_time, 9, 2) + ':' +
                        substring(trf.fer_del_time, 11,2), 108)
    )                                                                                  as gw_end_snd_mi,
    --
    ( convert(datetime, substring(trf.last_bar_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.last_bar_sub_time, 7, 2) + ':' +
                        substring(trf.last_bar_sub_time, 9, 2) + ':' +
                        substring(trf.last_bar_sub_time, 11,2), 108)
    )                                                                                  as mi_beg_rcv_gw,
    trf.msg_digest_alg,
    trf.msg_digest,
    trf.local_auth_info_alg,
    trf.local_auth_info
    --
from
    FAS_MSG_RECV   trf
/


-- *************************************************************************
-- VI_MSS_SND
-- *************************************************************************
-- 04100_VI_MSS_SND.sql

IF EXISTS (SELECT TABLE_NAME from INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME='VI_MSS_SND')
DROP VIEW VI_MSS_SND
/
create view [VI_MSS_SND] as
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
    trf.stscode as stscode,
    trf.status as status,
    trf.status_info as status_info,
    -- data da troncare al secondo #8094
    convert(datetime, convert(varchar(32), trf.send_err_timestamp, 120), 120)          as send_err_timestamp,
    trf.appl_check,
    trf.complete,
    trf.msgsize,
    trf.mab,
    --
    -- timestamp da troncare al secondo #8094
    convert(datetime, convert(varchar(32), trf.ba_insert_timestamp, 120), 120)         as ba_insert_timestamp,
    --
    -- varchar(24), convertire in data
    cast(replace(trf.createdate, '/', '-') as datetime)                                as mi_accept_ba,
    --
    -- varchar(17), eliminare gli ultimi 5 caratteri del timezone e convertire in data
    ( convert(datetime, substring(trf.first_eas_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.first_eas_sub_time, 7, 2) + ':' +
                        substring(trf.first_eas_sub_time, 9, 2) + ':' +
                        substring(trf.first_eas_sub_time, 11,2), 108)
    )                                                                                  as mi_beg_snd_gw,
    --
    -- varchar(17), eliminare gli ultimi 5 caratteri del timezone e convertire in data
    ( convert(datetime, substring(trf.fer_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.fer_sub_time, 7, 2) + ':' +
                        substring(trf.fer_sub_time, 9, 2) + ':' +
                        substring(trf.fer_sub_time, 11,2), 108)
    )                                                                                  as gw_beg_snd_gw,
    --
    -- timestamp da troncare al secondo #8094
    convert(datetime, convert(varchar(32), trf.send_req_timestamp, 120), 120)          as gw_end_snd_gw,
    --
    -- varchar(17), eliminare gli ultimi 5 caratteri del timezone e convertire in data
    ( convert(datetime, substring(trf.fen_del_time, 1, 6), 112)
    + convert(datetime, substring(trf.fen_del_time, 7, 2) + ':' +
                        substring(trf.fen_del_time, 9, 2) + ':' +
                        substring(trf.fen_del_time, 11,2), 108)
    )                                                                                  as gw_beg_snd_mi,
    --
    ( convert(datetime, substring(trf.fer_del_time, 1, 6), 112)
    + convert(datetime, substring(trf.fer_del_time, 7, 2) + ':' +
                        substring(trf.fer_del_time, 9, 2) + ':' +
                        substring(trf.fer_del_time, 11,2), 108)
    )                                                                                  as gw_end_snd_mi,
    --
    ( convert(datetime, substring(trf.last_eas_sub_time, 1, 6), 112)
    + convert(datetime, substring(trf.last_eas_sub_time, 7, 2) + ':' +
                        substring(trf.last_eas_sub_time, 9, 2) + ':' +
                        substring(trf.last_eas_sub_time, 11,2), 108)
    )                                                                                  as mi_beg_rcv_gw,
    trf.msg_digest_alg,
    trf.msg_digest,
    trf.local_auth_info_alg,
    trf.local_auth_info
    --
from
    FAS_MSG_SEND   trf
/