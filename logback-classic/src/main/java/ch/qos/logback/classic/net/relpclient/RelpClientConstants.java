package ch.qos.logback.classic.net.relpclient;

/**
 * RELP constants
 * @author Enikő Balogh
 */
public class RelpClientConstants {

    static final String CONNECT_REQUEST = "open 30 commands=syslog\nrelp_version=1\n";
    static final String CLOSE_REQUEST = "close 0\n";
    static final String OK_RESPONSE = "200 OK";

}
