package ch.qos.logback.classic.net;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.net.relpclient.RelpException;
import ch.qos.logback.classic.net.relpclient.RelpSendCmdException;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.classic.net.relpclient.RelpClient;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * RELP Syslog Appender
 *
 *  Example of appender configuration (ex. in backlog.xml) :
 *
 *   <!-- TCP/IP RELP Rsyslog appender -->
 *   <appender name="RELPRSYSLOG" class="ch.qos.logback.classic.net.RelpSyslogAppender">
 *       <SyslogHost>myrsysloghost</SyslogHost>
 *       <Port>20514</Port>
 *       <Facility>USER</Facility>
 *       <ProgramName>MyLoggingProgram</ProgramName>
 *       <SuffixPattern>[%thread] %-5level [%logger:%line] %msg%n</SuffixPattern>
 *   </appender>
 * 
 * @param <E>
 * @author Enikő Balogh
 */
public class RelpSyslogAppender<E> extends AppenderBase<E> {

    int port;
    Layout<E> layout;
    String syslogHost;
    String suffixPattern;
    String facility;
    String programName;

    private static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    private static final int MSG_SIZE_LIMIT = 128000;
    private RelpClient client;

    /**
     * Get RELPhost name
     * @return
     */
    public String getSyslogHost() {
        return syslogHost;
    }

    /**
     * Set RELPhost name
     * @param syslogHost
     */
    public void setSyslogHost(String host) {
        this.syslogHost = host;
    }

    /**
     * Get RELP port
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Set RELP port
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get logging pattern
     * @return
     */
    public String getSuffixPattern() {
        return suffixPattern;
    }

    /**
     * Set logging pattern
     * @param suffixPattern
     */
    public void setSuffixPattern(String suffixPattern) {
        this.suffixPattern = suffixPattern;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facilityStr) {
        this.facility = facilityStr;
    }


    /**
     * Get programName
     *
     * @return the value of programName
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Set programName
     *
     * @param programName new value of programName
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    @Override
    public void start() {
       int errorCount = connect();

        if (layout == null) {
            layout = buildLayout(facility);
        }

        if (errorCount == 0) {
            super.start();
            addInfo("Appender started");
        }
    }

    private int connect() {
        int errorCount = 0;

        if (port == 0) {
            errorCount++;
            addError("No port was configured for appender"
                    + name);
        }
        if (syslogHost == null) {
            errorCount++;
            addError("No remote host was configured for appender"
                    + name);
        }
        client = new RelpClient(syslogHost, port);
        try {
            client.connect();
        } catch (UnknownHostException e) {
            errorCount++;
            addError("Unknown host "
                    + syslogHost);
        } catch (IOException e) {
            errorCount++;
            addError("Unable to connect to host "
                    + e);
        } catch (RelpException e) {
            if (e instanceof RelpSendCmdException) {
                errorCount++;
                addError("Unable to send connect command to host "
                        + e);
            }
        }
        if (!client.isConnected()) {
            errorCount++;
            addError("Appender "
                    + name
                    + " could not connect to RELP rsyslog host");
        }
        return errorCount;
    }

 PatternLayout prefixLayout = new PatternLayout();
 /**
     * Build layout
     * @return
     */
    public Layout<E> buildLayout(String facilityStr) {
        String prefixPattern = "%syslogStart{" + facilityStr + "}%nopex";

        prefixLayout.getInstanceConverterMap().put("syslogStart",
        SyslogStartConverter.class.getName());
        prefixLayout.setPattern(prefixPattern);
        prefixLayout.setContext(getContext());
        prefixLayout.start();

        PatternLayout fullLayout = new PatternLayout();
        fullLayout.getInstanceConverterMap().put("syslogStart",
                SyslogStartConverter.class.getName());
        if (suffixPattern == null) {
            suffixPattern = DEFAULT_SUFFIX_PATTERN;
        }
        if (programName == null || programName.isEmpty()) {
          fullLayout.setPattern(prefixPattern + suffixPattern);
        }
        else {
          fullLayout.setPattern(prefixPattern + "{}" + programName.trim()
                  + " " + suffixPattern);
        }        
        fullLayout.setContext(getContext());
        fullLayout.start();
        return (Layout<E>) fullLayout;
    }

    /** Actually do the logging.
     * @param event
     */
    public synchronized void append(E event) {

        if (client == null || !client.isConnected()) {           
            return;
        }
        // format message
        String message = layout.doLayout(event);

        if (message != null && message.length() > MSG_SIZE_LIMIT) {
            message = message.substring(0, MSG_SIZE_LIMIT);
        }
        try {
            client.sendSyslogMessage(message);
        } catch (RelpException e) {
            if (e instanceof RelpSendCmdException) {
                addError("Unable to send message.");
                reconnect();
                throw new RuntimeException(e);
            }
        }
    }

    private void reconnect() {
        addInfo("Reconnecting to host");
        disconnect();
        if (connect() > 0) {
            addError("Unable to reconnect to host " + syslogHost);
        }
    }

    @Override
    public synchronized void stop() {
        addInfo("Stopping adapter");
        if (!isStarted()) {
            return;
        }
        disconnect();
        this.started = false;
        super.stop();
    }

    private void disconnect() {
        try {
            client.disconnect();
        } catch (IOException e) {
            addInfo("Error disconnecting from " + syslogHost);
        } catch (RelpException e) {
            if (e instanceof RelpSendCmdException) {
                addInfo("Error while sending close command to Relp host");
            }
        }
        client = null;
    }
}
