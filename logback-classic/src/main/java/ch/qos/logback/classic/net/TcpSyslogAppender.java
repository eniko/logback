package ch.qos.logback.classic.net;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;

/**
 * TCP/IP Syslog appender
 *
  *  Example of appender configuration (ex. in backlog.xml) :
 *
 *   <!-- TCP/IP Rsyslog appender -->
 *   <appender name="TCPIPRSYSLOG" class="ch.qos.logback.classic.net.TcpSyslogAppender">
 *       <SyslogHost>myrsysloghost</SyslogHost>
 *       <Port>10514</Port>
 *       <Facility>MAIL</Facility>
 *       <ProgramName>MyLoggingProgram</ProgramName>
 *       <SuffixPattern>[%thread] %-5level [%logger:%line] %msg%n</SuffixPattern>
 *   </appender>
 *
 * @author Enikő Balogh
 */
public class TcpSyslogAppender extends SyslogAppender {

    String programName = "";

    public TcpSyslogAppender() {
        super();
        this.setProtocol(SyslogConstants.TCP_PROTOCOL);
    }

    /**
     * Get the value of programName
     *
     * @return the value of programName
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Set the value of programName
     *
     * @param programName new value of programName
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }

//    PatternLayout prefixLayout = new PatternLayout();

    @Override
  public Layout<ILoggingEvent> buildLayout(String facilityStr) {
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
      fullLayout.setPattern(prefixPattern + "{} " + programName.trim()
              + " " + suffixPattern);
    }
    fullLayout.setContext(getContext());
    fullLayout.start();
    return fullLayout;
  }

}
