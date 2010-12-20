package ch.qos.logback.classic.net.relpclient;

/**
 * RELP client exception - sending command to the server
 * @author Enikő Balogh
 */
public class RelpSendCmdException extends RelpException {

    /**
     * Creates a new instance of <code>RelpSendCmdException</code> without detail message.
     */
    public RelpSendCmdException() {
    }


    /**
     * Constructs an instance of <code>RelpSendCmdException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RelpSendCmdException(String msg) {
        super(msg);
    }
}
