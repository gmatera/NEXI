
#jdbc:oracle:thin:@(DESCRIPTION=(CONNECT_TIMEOUT=90)(RETRY_COUNT=20)(RETRY_DELAY=3)(TRANSPORT_CONNECT_TIMEOUT=3)(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=poddb02-pesi-scan.cloudblue.local)(PORT=1521)))(ADDRESS_LIST=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=orac04-scan2.crtnet)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=SRVCWINBANK)))

version=1.4.0-SNAPSHOT

server_port=40001 
client_id=CSW-1
client_group_id=GRP-1
client_hostname=10.1.32.14
integration_test_active=true
#crypto_hub_url=https://intapi.nexi.it:8443/b2b/chc/cryptohub/getkey
crypto_hub_url=http://10.1.32.15:60005/chc-cryptohub/rest/getkey
datasource_url=jdbc:oracle:thin:@//poddb02-pesi-scan.cloudblue.local:1521/chccores_pry
datasource_username=CHC_CSW_01_OWN_OBJ
datasource_password=FYHMJNGZ
profiles_active=POLLER,POLLER_ADDON,BATCH_OUTBOUND_WORKER,BATCH_INBOUND_WORKER,SOAP_OUTBOUND_FACADE,SOAP_INBOUND_WORKER,REST_OUTBOUND_FACADE,DASHBOARD
ccr_host_batch=http:/10.1.32.15:50001 
ccr_host_online=http://10.1.32.15:50001
outbound_workers[0]=http://10.1.32.14:40001
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
echo "datasource_username ->"$datasource_username
echo "ccr_host_batch ->"$ccr_host_batch
echo "ccr_host_online ->"$ccr_host_online
echo "client_software_id ->"$client_software_id
echo "api_gateway_public_key_url ->"$api_gateway_public_key_url
echo "nexi_oauth_token_url ->"$nexi_oauth_token_url
echo "enable_security ->"$enable_security
echo "enable_encryption ->"$enable_encryption


app="chc-ccr-clientsw-all-app-"$version".jar"
echo "Starting app "$app"
 
nohup /home/javauser/java/jre/bin/java -jar -Dserver.port=$server_port -Dclient_id=$client_id -Dclient_hostname=$client_hostname -Dintegration_test_active=$integration_test_active -Dcrypto_hub_url=$crypto_hub_url -Dspring.datasource.url=$datasource_url -Dspring.datasource.username=$datasource_username -Dspring.datasource.password=$datasource_password -Dspring.liquibase.enabled=false -Dspring.profiles.active=$profiles_active -Dccr_host_batch=$ccr_host_batch -Dccr_host_online=$ccr_host_online -Doutbound_workers=$outbound_workers -Dclient-software-id=$client_software_id -Dapi-gateway-public-key-url=$api_gateway_public_key_url -Dnexi-oauth-token-url=$nexi_oauth_token_url -Denable-security=$enable_security -Denable_encryption=$enable_encryption $app > "csw_"$client_id".log" &


