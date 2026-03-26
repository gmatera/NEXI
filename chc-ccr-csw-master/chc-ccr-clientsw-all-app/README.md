# CHC CCR and ClientSW

## Test env

Required database
	
	chc_csw_test	
	
## ClietSW start	

	docker exec -it --user=oracle myxedb bash

    sqlplus sys@XE as sysdba
    alter session set "_oracle_script"=true;
    CREATE USER csw IDENTIFIED BY csw;
    GRANT CONNECT, RESOURCE, DBA TO csw;
    GRANT UNLIMITED TABLESPACE TO csw;

    CREATE USER csw2 IDENTIFIED BY csw2;
    GRANT CONNECT, RESOURCE, DBA TO csw2;
    GRANT UNLIMITED TABLESPACE TO csw2;
    
	!!!!!!!!!! DO mvn install before start!!!
	aprirlo nel terminale di clientsw-all-app
	
		mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=40002 --spring.datasource.url=jdbc:oracle:thin:@//10.0.0.214:1521/XE --spring.datasource.username=csw2 --spring.datasource.password=csw2"
	
## Swagger
	
		http://localhost:40001/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/
		http://localhost:50001/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/
	
## Cifrare password database
	java -cp chc-ccr-clientsw-all-app.jar -Dloader.main=com.cbi.ccr.csw.app.StaticEncryptor org.springframework.boot.loader.PropertiesLauncher
	
## Generare chiavi SSH 

genera chiave privata e certificato
	
		openssl req -newkey rsa:2048 -new -nodes -x509 -days 3650 -keyout key.pem -out cert.pem
		
estrai chiave pubblica dal certificato
	
		openssl   x509 -pubkey -noout -in cert.pem > pubkey.pem
	
Remove \n
	
	awk 'NF {sub(/\r/, ""); printf "%s\\n",$0;}'  cert.pem
	