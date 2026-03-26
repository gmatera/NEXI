    
    docker volume create qm1data
    
docker run \
  --name mqtls \
  --env LICENSE=accept \
  --env MQ_QMGR_NAME=QM1 \
  --publish 1414:1414 \
  --publish 9443:9443 \
  --detach \
  --volume qm1data:/mnt/mqm \
  icr.io/ibm-messaging/mq
  
docker run \
  --name mqtls --env LICENSE=accept --env MQ_QMGR_NAME=QM1 \
  --publish 1414:1414 \
  --publish 9443:9443 \
  --detach \ 
  mqtls:1.0.0
  
HTTPS
keytool -genkeypair -keyalg RSA -alias springboot -keystore clientkey.p12 -storetype pkcs12 -validity 3650
keytool -list -keystore clientkey.p12

IBM_MQ

keytool -export -alias springboot -file clientCertificate.crt -keystore ../https/clientkey.p12
docker cp ./clientCertificate.crt mqtls:/run/runmqserver/tls/clientCertificate.crt


docker exec -it mqtls bash
cd /run/runmqserver/tls
runmqakm -cert -create -db key.kdb -stashed -dn "cn=qm,o=ibm,c=uk" -label ibmwebspheremqqm1
runmqakm -cert -list -db key.kdb -stashed
runmqakm -cert -extract -label ibmwebspheremqqm1 -db key.kdb -stashed -file QM1.cert
runmqakm -cert -add -db key.kdb -stashed -label ibmwebspheremqapp -file ./clientCertificate.crt
runmqakm -cert -list -db key.kdb -stashed

crtl-D
docker cp mqtls:/run/runmqserver/tls/QM1.cert ./
keytool -keystore ../https/clientkey.p12 -storetype pkcs12 -importcert -file QM1.cert -alias server-certificate








docker logs mqtls


docker cp mqtls:/run/runmqserver/tls/. ./


echo "ALTER CHL('DEV.APP.SVRCONN') CHLTYPE(SVRCONN) SSLCIPH(ANY_TLS12) SSLCAUTH(REQUIRED)" | runmqsc QM1
echo "REFRESH SECURITY(*) TYPE(SSL)" | runmqsc QM1
echo "DEFINE QLOCAL (FTS.SND.REQ)" | runmqsc QM1



runmqsc QM1
DISPLAY CHANNEL(DEV.APP.SVRCONN)
DISPLAY CHANNEL(DEV.ADMIN.SVRCONN)

ALTER CHL('DEV.APP.SVRCONN') CHLTYPE(SVRCONN) SSLCIPH('') SSLCAUTH(OPTIONAL)
ALTER CHL('DEV.APP.SVRCONN') CHLTYPE(SVRCONN) SSLCIPH(ANY_TLS12) SSLCAUTH(OPTIONAL)
ALTER CHL('DEV.APP.SVRCONN') CHLTYPE(SVRCONN) SSLCIPH(ANY_TLS12) SSLCAUTH(REQUIRED)

ALTER CHL('DEV.ADMIN.SVRCONN') CHLTYPE(SVRCONN) SSLCIPH(ANY_TLS12) SSLCAUTH(OPTIONAL)

ALTER CHANNEL(DEV.APP.SVRCONN) CHLTYPE(SVRCONN) SSLCAUTH(REQUIRED)
ALTER CHANNEL(DEV.ADMIN.SVRCONN) CHLTYPE(SVRCONN) SSLCAUTH(REQUIRED)

ALTER QMGR CERTLABL('')
REFRESH SECURITY(*) TYPE(SSL)


runmqakm -keydb -create -db key.kdb -pw password -stash
runmqakm -cert -create -db key.kdb -stashed -dn "cn=qm,o=ibm,c=uk" -label ibmwebspheremqqm1
runmqakm -cert -extract -label ibmwebspheremqqm1 -db key.kdb -stashed -file QM1.cert

docker cp mqtls:/var/mqm/qmgrs/QM1/ssl/QM1.cert ./
docker cp mqtls:/run/runmqserver/tls/QM1.cert ./




runmqakm -cert -delete -db key.kdb -stashed -label ibmwebspheremqapp

docker cp ./clientCertificate.crt mqtls:/run/runmqserver/tls/clientCertificate.crt
runmqakm -cert -add -db key.kdb -stashed -label ibmwebspheremqapp -file ./clientCertificate.crt
runmqakm -cert -list -db key.kdb -stashed
REFRESH SECURITY(*) TYPE(SSL)

ALTER QMGR SSLKEYR(/run/runmqserver/tls/key) 
ALTER QMGR CERTLABL( ) on the queue manger isn't set to "ibmwebspheremqqm1" 


setmqaut -m QM1 -n ** -t queue -g User +browse +dsp +inq +put
setmqaut -m QM1 -t queue -n FTS.RCV.IND -g User +browse -get +put
         
SET AUTHREC PROFILE(*) OBJTYPE(QUEUE) PRINCIPAL(User) AUTHADD(ALL) AUTHRMV(SETID)

GRTMQAUT OBJ(VEG) OBJTYPE(*TOPIC) USER(User) AUT(*PUB)

SET AUTHREC OBJTYPE(QMGR) PRINCIPAL('User') AUTHADD(ALL)

setmqaut -m QM1 -t q -n ** -p app +browse +dsp +inq +put +get +set



