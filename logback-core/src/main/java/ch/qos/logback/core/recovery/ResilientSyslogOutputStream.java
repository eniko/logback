/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2010, QOS.ch. All rights reserved.
 * 
 * This program and the accompanying materials are dual-licensed under either
 * the terms of the Eclipse Public License v1.0 as published by the Eclipse
 * Foundation
 * 
 * or (per the licensee's choosing)
 * 
 * under the terms of the GNU Lesser General Public License version 2.1 as
 * published by the Free Software Foundation.
 */
package ch.qos.logback.core.recovery;

import ch.qos.logback.core.net.SyslogConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import ch.qos.logback.core.net.SyslogUDPOutputStream;
import ch.qos.logback.core.net.SyslogTCPOutputStream;

public class ResilientSyslogOutputStream extends ResilientOutputStreamBase {


  String syslogHost;
  int port;
  String protocol;
  
  public ResilientSyslogOutputStream(String syslogHost, int port, String protocol)
      throws UnknownHostException, SocketException, IOException {
    this.syslogHost = syslogHost;
    this.port = port;
    this.protocol = protocol;
    if (this.protocol.equals(SyslogConstants.TCP_PROTOCOL)) {
        super.os = new SyslogTCPOutputStream(syslogHost, port);
    }
    else {
        super.os = new SyslogUDPOutputStream(syslogHost, port);
    }
    this.presumedClean = true;
  }

  public ResilientSyslogOutputStream(String syslogHost, int port)
      throws UnknownHostException, SocketException, IOException {
    this(syslogHost, port, SyslogConstants.UDP_PROTOCOL);
  }

  @Override
  String getDescription() {
    return "syslog ["+syslogHost+":"+port+"]";
  }

  @Override
  OutputStream openNewOutputStream() throws IOException {
    OutputStream out = null;
    if (this.protocol.equals(SyslogConstants.TCP_PROTOCOL)) {
        out = new SyslogTCPOutputStream(syslogHost, port);
    }
    else {
        out = new SyslogUDPOutputStream(syslogHost, port);
    }
    return out;
  }
  
  @Override
  public String toString() {
    return "c.q.l.c.recovery.ResilientSyslogOutputStream@"
        + System.identityHashCode(this);
  }

}
