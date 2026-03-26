
#jdbc:oracle:thin:@(DESCRIPTION=(CONNECT_TIMEOUT=90)(RETRY_COUNT=20)(RETRY_DELAY=3)(TRANSPORT_CONNECT_TIMEOUT=3)(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=poddb02-pesi-scan.cloudblue.local)(PORT=1521)))(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=orac04-scan2.crtnet)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=SRVCWINBANK)))

version=''

server_port=40001 
client_id=CSW-1
client_group_id=GRP-1
client_hostname=localhost
integration_test_active=false
#crypto_hub_url=https://intapi.nexi.it:8443/b2b/chc/cryptohub/getkey
crypto_hub_url=http://localhost:40001/chc/b2b/cryptohub/getkey

datasource_url="jdbc:sqlserver://10.0.0.233:1433;databaseName=CSW;encrypt=true;trustServerCertificate=true;"
datasource_username=SA
datasource_password=aaQQ11!!

profiles_active=POLLER,POLLER_ADDON,BATCH_OUTBOUND_WORKER,BATCH_INBOUND_WORKER,SOAP_OUTBOUND_FACADE,SOAP_INBOUND_WORKER,REST_OUTBOUND_FACADE,DASHBOARD
ccr_host_batch="http://localhost:50001" 
ccr_host_online="http://localhost:50001" 
outbound_workers[0]=http://localhost:40001
client_software_id=dbd3f868-84c9-48ea-b789-eeb7a04d246a
#api_gateway_public_key_url=https://intapi.nexi.it/openid/connect/jwks.json
api_gateway_public_key_url=https://intapi.nexi.it/chc/ccr/jwks.json
nexi_oauth_token_url=https://intapi.nexi.it/chc/oauth/ccr/token
#nexi_oauth_token_url=http://localhost:60005/chc/oauth/ccr/token
enable_security=false
enable_encryption=false

echo "server_port ->" $server_port
echo "integration_test_active ->"$integration_test_active
echo "crypto_hub_url ->"$crypto_hub_url
echo "datasource_url ->"$datasource_url
echo "datasource_username ->"$datasource_username
echo "ccr_host_batch ->"$ccr_host_batch
echo "ccr_host_online ->"$ccr_host_online
echo "client_software_id ->"$client_software_id
echo "api_gateway_public_key_url ->"$api_gateway_public_key_url
echo "nexi_oauth_token_url ->"$nexi_oauth_token_url
echo "enable_security ->"$enable_security
echo "enable_encryption ->"$enable_encryption


app="chc-ccr-clientsw-all-app.jar"
echo "Starting app "$app
 
#/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar -Dspring.jpa.mapping-resources=classpath=/orm.xml -Dserver.port=$server_port -Dclient_id=$client_id -Dclient_hostname=$client_hostname -Dintegration_test_active=$integration_test_active -Dcrypto_hub_url=$crypto_hub_url -Dspring.datasource.url=$datasource_url -Dspring.datasource.username=$datasource_username -Dspring.datasource.password=$datasource_password -Dspring.liquibase.enabled=false -Dspring.profiles.active=$profiles_active -Dccr_host_batch=$ccr_host_batch -Dccr_host_online=$ccr_host_online -Doutbound_workers=$outbound_workers -Dclient-software-id=$client_software_id -Dapi-gateway-public-key-url=$api_gateway_public_key_url -Dnexi-oauth-token-url=$nexi_oauth_token_url -Denable-security=$enable_security -Denable_encryption=$enable_encryption $app
/usr/lib/jvm/adoptopenjdk-8-openj9-amd64/bin/java -jar -Dspring.jpa.mapping-resources="classpath:/orm.xml" -Dserver.port=$server_port -Dclient_id=$client_id -Dclient_hostname=$client_hostname -Dintegration_test_active=$integration_test_active -Dcrypto_hub_url=$crypto_hub_url -Dspring.datasource.url=$datasource_url -Dspring.datasource.username=$datasource_username -Dspring.datasource.password=$datasource_password -Dspring.liquibase.enabled=false -Dspring.profiles.active=$profiles_active -Dccr_host_batch=$ccr_host_batch -Dccr_host_online=$ccr_host_online -Doutbound_workers=$outbound_workers -Dclient-software-id=$client_software_id -Dapi-gateway-public-key-url=$api_gateway_public_key_url -Dnexi-oauth-token-url=$nexi_oauth_token_url -Denable-security=$enable_security -Denable_encryption=$enable_encryption $app


