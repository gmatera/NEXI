package com.cbi.ccr.csw.femws.service.utils;


import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//HKEY_LOCAL_MACHINE\SYSTEM\ControlSet001\Services\Eventlog

/**
 * gestore dell'event log di Windows
 *
 * !!!!
 * ATTENZIONE! il logging su EventLog di Windows è utilizzato dal monitoraggio per inviare le segnalazioni agli operatori
 * Prima di modificare il modo in cui vengono scritti gli eventi occorre confrontarsi con l'ufficio di monitoraggio
 * per assicurarsi di non uscire dalle policy di segnalazione degli eventi
 * !!!!
 * @author TFSCostabileMichele
 */
@Service
public class EventLog {
	
    /**
     * Elenco di tutti gli eventi segnalati su WsEvent Log di Windows.
     * IMPORTANTE!!! l'id deve rimanere lo stesso nel tempo perché usato dal monitoraggio
     * per filtrare gli eventi!!!!!
     * Se deve aggiungere un evento lo si aggiunga alla fine.
     */
    public enum WsEvent {
        // NON CAMBIATE GLI ID
        CONTEXT_STARTED(1,"Context started"),
        CONTEXT_START_ERROR(2,"Servlet Init failed"),
        FEMSWS_INIT(3,"Initializing FEMS-WS"),
        FEMSWS_STARTING(4,"Starting service"),
        FEMSWS_STARTED(5,"Service started"),
        FEMSWS_START_ERROR(6,"Cannot start FEMS-WS"),
        FEMSWS_STOP_ERROR(7,"Cannot stop service"),
        FEMSWS_STOPPING(8,"Stopping service"),
        FEMSWS_STOPPED(9,"Service stopped"),

        DB_ERROR(10,"DB Error"),
        DB_CONF_ERROR(11,"Error reading configuration from database"),
        DB_DRIVER_NOT_FOUND(12,"Database driver not found"),

        TRUSTSTORE_CREATE_REMOVE_ERROR(13,"An error occurred Removing\\Creating TrustStore"),
        TRUSTSTORE_ADD_CERT_ERROR(14,"An error occurred adding certificates to TrustStore"),
        SOAP_FAULT(15,"Soap fault"),
        REQUEST_CLOSE_ERROR(16,"Registration of the request closing not done"),
        GET_NOT_SUPPORTED(17,"Get not supported"),
        HTTPS_CONF_ERROR(18,"Error in https configuration"),

        WSSERVER_MAX_REQUEST_ERROR(19,"femsws:Server.MaxRequestError - Maximum number of connections excedeed"),
        WSSERVER_OTHER_ERROR(20,"femsws:Server.OtherError"),
        WSSERVER_CLIENT_PROTOCOL_ERROR(21,"femsws:Server. Client Protocol Exception while handling request"),
        WSSERVER_HANDLER_IO_ERROR(22,"femsws:Server. I/O exception while handling request"),
        WSSERVER_HANDLER_RECEIVED_EXCEPTION(23,"femsws:Server.Reporting Exception during handle request"),
        WSSERVER_HANDLER_ISTREAM_ERROR(24,"femsws:Server error reading input stream"),
        WSSERVER_WEBSERVER_COMM_ERROR(25,"femsws:Server.WebServerCommunication error"),
        WSSERVER_TIMEOUT_ERROR(26,"femsws:Server.TimeoutError"),
        WSSERVER_CERT_EXPIRED_ERROR(27,"femsws:Server.Certificate expired error"),
        WSSERVER_CERT_SERVER_INVALID(28,"femsws:Server.Invalid server certificate error"),
        WSSERVER_CERT_CLIENT_INVALID(29,"femsws:Server.Invalid client certificate error"),

        WSCLIENT_MAX_REQUEST_ERROR(30,"femsws:Client.MaxRequestError - Maximum number of connections excedeed"),
        WSCLIENT_OTHER_ERROR(31,"femsws:Client.OtherError"),
        WSCLIENT_CLIENT_PROTOCOL_ERROR(32,"femsws:Client. Client Protocol Exception while handling request"),
        WSCLIENT_HANDLER_IO_ERROR(33,"femsws:Client. I/O exception while handling request"),
        WSCLIENT_HANDLER_RECEIVED_EXCEPTION(34,"femsws:Client.Reporting Exception during handle request"),
        WSCLIENT_SERVER_COMM_ERROR(35,"femsws:Client.ServerCommunicationError"),
        WSCLIENT_TIMEOUT_ERROR(36, "femsws:Client.TimeoutError"),
        WSCLIENT_INCOMING_MSG_ERROR(37,"Error in Incoming Message"),

        CERT_EXPIRED(38,"Certificate is expired"),
        CERT_WILL_EXPIRE(39,"Certificate will expire soon"),
        UDR_CALC_ERR(40, "unable to calculate udr wirh UDRALG PDD")


                ;

        private String text;
        private int id;

        WsEvent(int id,String text) {
            this.text=text;
            this.id=id;
        }

        public String getText(){
            return this.text;
        }
        public int getId(){
            return this.id;
        }

    }

    private static final String LOG_CATEGORY = "FEMS";
    private static final String LOG_INFORMATION = "INFORMATION";
    private static final String LOG_ERROR = "ERROR";
    private String source = null;
    private final Logger logger = LoggerFactory.getLogger(EventLog.class);
    private static String prefix = "EventLog -> {}";


    public void info(WsEvent event){
        info(event.getText(), event.getId());
    }

    public void info(WsEvent event, String detail){
        info(event.getText() + " (" + detail + ")", event.getId());
    }

    /**
     * Scrive un informazione nel log e nel registro eventi
     *
     * @param msg il messaggio da scrivere sul log
     */
    private void info(String msg, int id) {
        if (logger != null)
            logger.info(prefix, msg);
        write(LOG_INFORMATION, msg, id);
    }

    public void error(WsEvent event){
        error(event.getText(), event.getId());
    }

    public void error(WsEvent event, String detail){
        error(event.getText() + " (" + detail + ")", event.getId());
    }

    /**
     * Scrive un errore nel log e nel registro eventi
     *
     * @param msg il messaggio da scrivere sul log
     */
    private void error(String msg, int id) {
        if (logger != null)
            logger.error(prefix, msg);
        write(LOG_ERROR, msg, id);
    }


    /**
     * Crea la riga di errore nell'event log
     *
     * @param level livello dell'errore
     * @param msg   il messaggio da scrivere sul log
     */
    private void write(String level, String msg, int id) {

    	if(!SystemUtils.IS_OS_WINDOWS) {
    		return;
    	}
        try {
            if (source == null) {
            	 logger.error("WsEvent Viewer Instance not Set for event logging");
            	 logger.error("Error occurred level:{} level msg:{} id:{}", level, msg, id);
            }else {

                final String command = "eventcreate /ID " + id + " /L " + LOG_CATEGORY + " /T " + level + " /SO "
                        + source + " /D \"" + msg + "\"";

                Process p = Runtime.getRuntime().exec(command);
                p.waitFor();
                int rc = p.exitValue();
                if (rc != 0 && logger != null) {
                    logger.error("Error writing in event log. command {" + command + "} returned: " + rc);
                }
            }

        } catch (Exception e) {
        	logger.error("Error writing in event log: {}", e.getMessage());
        	Thread.currentThread().interrupt();
        }
    }

    /**
     * Imposta il nome del FEMS-WS che sta eseguendo il log.
     * Questo metodo vieta che la classe sia una utility class statica
     *
     * @param source Il nome del FEMSWS
     */
    public void setSource(String source) {
        this.source = source;
    }

    public String getSource(){
        return source;
    }
}
