package ch.qos.logback.classic.net.relpclient;

/**
 *
 * @author Enikő Balogh
 */
public class RelpException extends Exception {

    /**
     * Creates a new instance of <code>RelpException</code> without detail message.
     */
    public RelpException() {
    }


    /**
     * Constructs an instance of <code>RelpException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RelpException(String msg) {
        super(msg);
    }
}
