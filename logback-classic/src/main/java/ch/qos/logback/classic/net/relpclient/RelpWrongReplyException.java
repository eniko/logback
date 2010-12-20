package ch.qos.logback.classic.net.relpclient;

/**
 *
 * @author Enikő Balogh
 */
public class RelpWrongReplyException extends RelpException {

    /**
     * Creates a new instance of <code>RelpWrongReplyException</code> without detail message.
     */
    public RelpWrongReplyException() {
    }


    /**
     * Constructs an instance of <code>RelpWrongReplyException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RelpWrongReplyException(String msg) {
        super(msg);
    }
}
