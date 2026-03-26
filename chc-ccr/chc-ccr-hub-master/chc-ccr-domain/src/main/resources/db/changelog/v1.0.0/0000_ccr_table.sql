CREATE SEQUENCE CHC_CCR_OWN_OBJ.seq_ba_url start WITH 1 increment BY 1 CACHE 20 NOORDER NOCYCLE;

GRANT ALTER,SELECT ON CHC_CCR_OWN_OBJ.seq_ba_url TO CHC_CCR_OWN_RW;
GRANT SELECT ON CHC_CCR_OWN_OBJ.seq_ba_url TO CHC_CCR_OWN_RO;
CREATE SYNONYM CHC_CCR_OWN_SV.seq_ba_url FOR CHC_CCR_OWN_OBJ.seq_ba_url;



CREATE TABLE CHC_CCR_OWN_OBJ.ba_url (
    id NUMBER(19,0) not null,
    active NUMBER(1,0) not null,
    ba_id VARCHAR2(255) not null,
    url VARCHAR2(255 char) not null,
    client_id VARCHAR2(100)
    ) TABLESPACE CHC_CCR_OWN_DATA;

alter table CHC_CCR_OWN_OBJ.ba_url add constraint PK_ba_url 
  primary key (ID) using index  tablespace CHC_CCR_OWN_INDX;

-- Grant/Revoke object privileges
GRANT select, insert, update, delete on CHC_CCR_OWN_OBJ.ba_url to CHC_CCR_OWN_RW;
GRANT SELECT ON CHC_CCR_OWN_OBJ.ba_url TO CHC_CCR_OWN_RO;
CREATE SYNONYM CHC_CCR_OWN_SV.ba_url FOR CHC_CCR_OWN_OBJ.ba_url;


CREATE TABLE CHC_CCR_OWN_OBJ.ccr_fms_message (
  phy_msg_id NUMBER(19,0) not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id NUMBER(19,0) not null,
  client_sw_msg_id NUMBER(19,0),
  direction VARCHAR2(10) not null,
  insert_date TIMESTAMP not null,
  local_auth_info VARCHAR2(128),
  local_auth_info_alg VARCHAR2(8),
  localba_id VARCHAR2(12) not null,
  log VARCHAR2(4000),
  remote_ba_id VARCHAR2(12 char) not null,
  status VARCHAR2(255) not null,
  sub_status VARCHAR2(255 char) not null,
  cat_appl VARCHAR2(4),
  code_page VARCHAR2(10),
  file_digest VARCHAR2(128),
  file_digest_alg VARCHAR2(8),
  file_hash VARCHAR2(32),
  file_name VARCHAR2(1024),
  file_size NUMBER(19,0),
  line_separator VARCHAR2(255),
  max_record_length NUMBER(10,0),
  message_leng NUMBER(10,0),
  message_type VARCHAR2(3),
  message_digest VARCHAR2(128),
  message_digest_alg VARCHAR2(8),
  record_format VARCHAR2(255 ),
  repo_file_id VARCHAR2(50),
  repo_message_id VARCHAR2(50),
  tur VARCHAR2(16),
  udr VARCHAR2(80),
  vfn VARCHAR2(32) not null,
  retry_cnt NUMBER(10) DEFAULT 0,
  correlation_id VARCHAR2(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP
  ) TABLESPACE CHC_CCR_OWN_DATA;

alter table CHC_CCR_OWN_OBJ.ccr_fms_message add constraint PK_ccr_fms_message 
  primary key (phy_msg_id) using index  tablespace CHC_CCR_OWN_INDX;

-- Grant/Revoke object privileges
GRANT select, insert, update, delete on CHC_CCR_OWN_OBJ.ccr_fms_message to CHC_CCR_OWN_RW;
GRANT SELECT ON CHC_CCR_OWN_OBJ.ccr_fms_message TO CHC_CCR_OWN_RO;
CREATE SYNONYM CHC_CCR_OWN_SV.ccr_fms_message FOR CHC_CCR_OWN_OBJ.ccr_fms_message;

CREATE INDEX CHC_CCR_OWN_OBJ.idx_fms_message ON CHC_CCR_OWN_OBJ.ccr_fms_message(vfn,udr,phy_msg_id) TABLESPACE CHC_CCR_OWN_INDX ONLINE;

CREATE TABLE CHC_CCR_OWN_OBJ.ccr_fts_message (
  phy_msg_id NUMBER(19,0) not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id NUMBER(19,0) not null,
  client_sw_msg_id NUMBER(19,0),
  direction VARCHAR2(10) not null,
  insert_Date TIMESTAMP not null,
  local_auth_info VARCHAR2(128),
  local_auth_info_alg VARCHAR2(8),
  localba_id VARCHAR2(12) not null,
  log VARCHAR2(4000),
  remote_ba_id VARCHAR2(12) not null,
  status VARCHAR2(255) not null,
  sub_status VARCHAR2(255) not null,
  code_page VARCHAR2(10),
  fsize NUMBER(19,0),
  file_digest VARCHAR2(128),
  file_digest_alg VARCHAR2(8),
  fname VARCHAR2(1024),
  fmd5 VARCHAR2(32),
  line_separator VARCHAR2(255),
  max_record_length NUMBER(10,0),
  record_format VARCHAR2(255),
  repo_file_id VARCHAR2(50),
  vfn VARCHAR2(32) not null,
  tur VARCHAR2(16),
  udr VARCHAR2(80),
  retry_cnt NUMBER(10) DEFAULT 0,
  fts_interface VARCHAR2(2) DEFAULT '0',
  correlation_id VARCHAR2(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP
  ) 
  TABLESPACE CHC_CCR_OWN_DATA;

alter table CHC_CCR_OWN_OBJ.ccr_fts_message add constraint PK_ccr_fts_message 
  primary key (phy_msg_id) using index  tablespace CHC_CCR_OWN_INDX;

-- Grant/Revoke object privileges
GRANT select, insert, update, delete on CHC_CCR_OWN_OBJ.ccr_fts_message to CHC_CCR_OWN_RW;
GRANT SELECT ON CHC_CCR_OWN_OBJ.ccr_fts_message TO CHC_CCR_OWN_RO;
CREATE SYNONYM CHC_CCR_OWN_SV.ccr_fts_message FOR CHC_CCR_OWN_OBJ.ccr_fts_message;

CREATE INDEX CHC_CCR_OWN_OBJ.idx_fts_message ON  CHC_CCR_OWN_OBJ.ccr_fts_message(vfn,localba_id,phy_msg_id) TABLESPACE CHC_CCR_OWN_INDX ONLINE;



CREATE TABLE CHC_CCR_OWN_OBJ.ccr_mss_message (
  phy_msg_id NUMBER(19,0) not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id NUMBER(19,0) not null,
  client_sw_msg_id NUMBER(19,0),
  direction VARCHAR2(10) not null,
  insert_date TIMESTAMP not null,
  local_auth_info VARCHAR2(128),
  local_auth_info_alg VARCHAR2(8),
  localba_id VARCHAR2(12) not null,
  log VARCHAR2(4000),
  remote_ba_id VARCHAR2(12) not null,
  status VARCHAR2(255) not null,
  sub_status VARCHAR2(255) not null,
  cat_appl VARCHAR2(4),
  msg_type VARCHAR2(3) not null,
  msg_digest VARCHAR2(128),
  msg_digest_alg VARCHAR2(8),
  msgid VARCHAR2(30),
  msgsize NUMBER(10,0),
  net_msgid VARCHAR2(16),
  priority NUMBER(10,0),
  repo_message_id VARCHAR2(50),
  tur VARCHAR2(16),
  udr VARCHAR2(80),
  retry_cnt NUMBER(10) DEFAULT 0,
  correlation_id VARCHAR2(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP
  ) TABLESPACE CHC_CCR_OWN_DATA;

alter table CHC_CCR_OWN_OBJ.ccr_mss_message add constraint PK_ccr_mss_message 
  primary key (phy_msg_id) using index  tablespace CHC_CCR_OWN_INDX;

-- Grant/Revoke object privileges
GRANT select, insert, update, delete on CHC_CCR_OWN_OBJ.ccr_mss_message to CHC_CCR_OWN_RW;
GRANT SELECT ON CHC_CCR_OWN_OBJ.ccr_mss_message TO CHC_CCR_OWN_RO;
CREATE SYNONYM CHC_CCR_OWN_SV.ccr_mss_message FOR CHC_CCR_OWN_OBJ.ccr_mss_message;  
  
CREATE INDEX CHC_CCR_OWN_OBJ.idx_mss_message ON  CHC_CCR_OWN_OBJ.ccr_mss_message(msgid,localba_id,phy_msg_id) TABLESPACE CHC_CCR_OWN_INDX ONLINE;

