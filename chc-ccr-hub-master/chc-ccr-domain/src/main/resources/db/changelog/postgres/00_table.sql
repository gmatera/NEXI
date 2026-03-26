CREATE SEQUENCE seq_ba_url;
--rollback DROP SEQUENCE seq_ba_url;

CREATE TABLE ba_url (
    id int8 not null,
    active boolean not null,
    ba_id VARCHAR(255) not null,
    url VARCHAR(255) not null,
    client_id VARCHAR(100),
    PRIMARY KEY (id));
--rollback DROP TABLE ba_url;

CREATE TABLE ccr_fms_message (
  phy_msg_id int8 not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id int8 not null,
  client_sw_msg_id int8,
  direction VARCHAR(10) not null,
  insert_date TIMESTAMP not null,
  local_auth_info VARCHAR(128),
  local_auth_info_alg VARCHAR(8),
  localba_id VARCHAR(12) not null,
  log VARCHAR(4000),
  remote_ba_id VARCHAR(12) not null,
  status VARCHAR(255) not null,
  sub_status VARCHAR(255) not null,
  cat_appl VARCHAR(4),
  code_page VARCHAR(10),
  file_digest VARCHAR(128),
  file_digest_alg VARCHAR(8),
  file_hash VARCHAR(32),
  file_name VARCHAR(1024),
  file_size int8,
  line_separator VARCHAR(255),
  max_record_length int8,
  message_leng int8,
  message_type VARCHAR(3),
  message_digest VARCHAR(128),
  message_digest_alg VARCHAR(8),
  record_format VARCHAR(255),
  repo_file_id VARCHAR(50),
  repo_message_id VARCHAR(50),
  tur VARCHAR(16),
  udr VARCHAR(80),
  vfn VARCHAR(32) not null,
  retry_cnt int8 DEFAULT 0,
  correlation_id VARCHAR(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP,
  PRIMARY KEY (phy_msg_id));
--rollback DROP TABLE ba_url;    
  
CREATE INDEX idx_fms_message ON ccr_fms_message(vfn,udr,phy_msg_id);
--rollback DROP INDEX idx_fms_message;

CREATE TABLE ccr_fts_message (
  phy_msg_id int8 not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id int8 not null,
  client_sw_msg_id int8,
  direction VARCHAR(10) not null,
  insert_Date TIMESTAMP not null,
  local_auth_info VARCHAR(128),
  local_auth_info_alg VARCHAR(8),
  localba_id VARCHAR(12) not null,
  log VARCHAR(4000),
  remote_ba_id VARCHAR(12) not null,
  status VARCHAR(255) not null,
  sub_status VARCHAR(255) not null,
  code_page VARCHAR(10),
  fsize int8,
  file_digest VARCHAR(128),
  file_digest_alg VARCHAR(8),
  fname VARCHAR(1024),
  fmd5 VARCHAR(32),
  line_separator VARCHAR(255),
  max_record_length int8,
  record_format VARCHAR(255),
  repo_file_id VARCHAR(50),
  vfn VARCHAR(32) not null,
  tur VARCHAR(16),
  udr VARCHAR(80),
  retry_cnt int8 DEFAULT 0,
  fts_interface VARCHAR(2) DEFAULT '0',
  correlation_id VARCHAR(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP,
  PRIMARY KEY (phy_msg_id));
--rollback DROP TABLE ccr_fts_message;

CREATE INDEX idx_fts_message ON  ccr_fts_message(vfn,localba_id,phy_msg_id);
--rollback DROP INDEX idx_fts_message;

CREATE TABLE ccr_mss_message (
  phy_msg_id int8 not null,
  ba_insert_timestamp TIMESTAMP,
  chc_id int8 not null,
  client_sw_msg_id int8,
  direction VARCHAR(10) not null,
  insert_date TIMESTAMP not null,
  local_auth_info VARCHAR(128),
  local_auth_info_alg VARCHAR(8),
  localba_id VARCHAR(12) not null,
  log VARCHAR(4000),
  remote_ba_id VARCHAR(12) not null,
  status VARCHAR(255) not null,
  sub_status VARCHAR(255) not null,
  cat_appl VARCHAR(4),
  msg_type VARCHAR(3) not null,
  msg_digest VARCHAR(128),
  msg_digest_alg VARCHAR(8),
  msgid VARCHAR(30),
  msgsize int8,
  net_msgid VARCHAR(16),
  priority int8,
  repo_message_id VARCHAR(50),
  tur VARCHAR(16),
  udr VARCHAR(80),
  retry_cnt int8 DEFAULT 0,
  correlation_id VARCHAR(30),
  received_timestamp TIMESTAMP,
  start_sending_timestamp TIMESTAMP,
  PRIMARY KEY (phy_msg_id));
--rollback DROP TABLE ccr_mss_message;

CREATE INDEX idx_mss_message ON  ccr_mss_message(msgid,localba_id,phy_msg_id);
--rollback DROP INDEX idx_mss_message;

