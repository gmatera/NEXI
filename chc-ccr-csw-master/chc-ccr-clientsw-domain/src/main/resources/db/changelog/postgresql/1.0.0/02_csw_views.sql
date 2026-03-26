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
;
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
;
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
;
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
    cast(to_date(sf.createdate,'YYYY;MM;DD HH24:MI:SS') as timestamp) as mi_accept_ba,
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
;
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
;
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
    cast(to_date(trf.createdate,'YYYY;MM;DD HH24:MI:SS') as timestamp)                       as mi_accept_ba,
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
;