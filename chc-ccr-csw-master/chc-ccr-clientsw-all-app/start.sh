#!/bin/bash

#jdbc:oracle:thin:@(DESCRIPTION=(CONNECT_TIMEOUT=90)(RETRY_COUNT=20)(RETRY_DELAY=3)(TRANSPORT_CONNECT_TIMEOUT=3)(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=poddb02-pesi-scan.cloudblue.local)(PORT=1521)))(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=orac04-scan2.crtnet)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=SRVCWINBANK)))

## modifica con il nome del jar
app="target/chc-ccr-clientsw-all-app.jar"


nohup java -jar \
-Dspring.datasource.url="jdbc:sqlserver://chc_database:1433;databaseName=csw;encrypt=true;trustServerCertificate=true" \
-Dspring.datasource.username=SA \
-Dspring.datasource.password=aaQQ11!! \
-Dliquibase.enabled=true \
-Dserver.port=40001 \
-Dspringdoc.swagger-ui.enabled=false \
-Dspring.profiles.active=POLLER,POLLER_ADDON,BATCH_OUTBOUND_WORKER,BATCH_INBOUND_WORKER,SOAP_OUTBOUND_FACADE,SOAP_INBOUND_WORKER,REST_OUTBOUND_FACADE,DASHBOARD,MQ \
-Dclient_id=CSW-1 \
-Dclient_group_id=GRP-1 \
-Dclient_hostname=localhost \
-Dccr_host_batch=http://10.222.210.6:50001 \
-Dccr_host_online=http://10.222.210.6:50001 \
-Dcrypto_hub_url=http://10.1.32.18:60005/chc-cryptohub/rest/getkey \
-Dspring.servlet.multipart.location=/tmp/chc/csw/inbound/encrypted \
-Dlocal_storage_path_encrypted=/tmp/chc/csw/inbound/encrypted \
-Dlocal_storage_path_decrypted=/tmp/chc/csw/inbound/decrypted \
-Dcsw_service_status_update_interval=5000 \
-Dlocal_temp_folder_encrypted=/tmp/chc/csw/outbound/encrypted \
-Dconversion-folder=/tmp/chc/csw/outbound/conversion \
-Dfixed_poller_delay=5000 \
-Dlogging.file.name=/tmp/chc/csw/CSW.log \
-Doutbound_workers[0]=http://localhost:40001 \
-Djava.security.egd=file:/dev/./urandom \
-Dibm.mq.queueManager=QM1 \
-Dibm.mq.channel=DEV.ADMIN.SVRCONN \
-Dibm.mq.connName="chc_imbmq(1414)" \
-Dibm.mq.user=admin \
-Dibm.mq.password=passw0rd \
-Dlogging.level.com.cbi=INFO \
-Denable-security=false \
-Denable_encryption=false \
-Dcsw_issued_at_seconds_to_remove=10 \
-Dcsw_not_before_seconds_to_remove=120 \
$app &




