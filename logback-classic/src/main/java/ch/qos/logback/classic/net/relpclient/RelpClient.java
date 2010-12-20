package ch.qos.logback.classic.net.relpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple RELP client
 * @author Enikő Balogh
 */
public class RelpClient {
    private final String relpHost;
    private final int relpPort;
    private Socket socket = null;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean connected = false;
    private int transactionNr = 0;

    public RelpClient(String host, int port) {
        this.relpHost = host;
        this.relpPort = port;
    }

    /**
     * Open a RELP session on relpHost.
     * @param relpHost
     * @param relpPort
     */
    public synchronized void connect() throws UnknownHostException, IOException, RelpException {
        disconnect();
        socket = new Socket(relpHost, relpPort);
        os = socket.getOutputStream();
        is = socket.getInputStream();
        if (isOKAnswer(sendCmd(RelpClientConstants.CONNECT_REQUEST))) {
            connected = true;
        }
    }

    /**
     * Send close command.
     */
    private synchronized void close() throws RelpException {
        sendCmd(RelpClientConstants.CLOSE_REQUEST);
        connected = false;
    }

    /**
     * Stop a RELP session.
     */
    public void disconnect() throws IOException, RelpException {
        if (connected) {
            close();
        }
        if (os != null) {
            os.close();
            os = null;
        }
        if (is != null) {
            is.close();
            is = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    /**
     * Send a SYSLOG message trough RELP.
     * @param message
     * @return RELP server's response
     */
    public synchronized String sendSyslogMessage(String message) throws RelpException {
        String request = "syslog " + message.length() + " " + message + "\n";
        return sendCmd(request);
    }

    /**
     * Send a RELP command. Transaction identifier is handled internally.
     * @param cmd command to be sent
     * @return reply from RELP server
     */
    private String sendCmd(String cmd) throws RelpException {
        String request = ++transactionNr + " " + cmd;
        String reply = " ";
        try {
            os.write(request.getBytes("UTF-8"));
            os.flush();
            //logger.log(Level.INFO, "Snd:|{0}|", request.trim());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                //do nothing
            }

            byte[] buf = new byte[is.available()];
            is.read(buf);
            reply = new String(buf);
        } catch (IOException e) {           
            throw new RelpSendCmdException("Failed to send command:" + e);
        }
        //logger.log(Level.INFO, "Rcv:|{0}|", reply.trim());

        //check if the answer contains the same transaction id as the command.
        if (!reply.startsWith(Integer.toString(transactionNr))) {
            throw new RelpWrongReplyException("Got wrong transaction ID in reply ["+reply.trim()+"]");
            // TODO: handle situation (ex.:keep cmds, messages in queue-socket)
        }
        return reply;
    }

    /**
     * Check answer.
     * @param rsv
     * @return true if the rsv contains the "200 OK" string.
     */
    private boolean isOKAnswer(String rsv) {
        return rsv.indexOf(RelpClientConstants.OK_RESPONSE) > 0;
    }

    /**
     * 
     * @return RELP sessions connection status
     */
    public boolean isConnected() {
        return connected;
    }
}
