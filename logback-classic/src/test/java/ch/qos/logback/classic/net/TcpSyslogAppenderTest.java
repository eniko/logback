/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2009, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.net;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.recovery.RecoveryCoordinator;
import ch.qos.logback.core.util.StatusPrinter;

public class TcpSyslogAppenderTest {

  LoggerContext lc = new LoggerContext();
  TcpSyslogAppender sa = new TcpSyslogAppender();
  String loggerName = this.getClass().getName();
  Logger logger = lc.getLogger(loggerName);

  @Before
  public void setUp() throws Exception {
    lc.setName("test");
    sa.setContext(lc);
  }

  @After
  public void tearDown() throws Exception {
  }

  public void configure()
      throws InterruptedException {

    sa.setSyslogHost("localhost");
    sa.setFacility("AUTH");
    sa.setPort(10514);
    sa.setSuffixPattern("%-5level [%logger:%line] [%thread] %msg %ex{short}%n");
    sa.start();
    assertTrue(sa.isStarted());

    String loggerName = this.getClass().getName();
    Logger logger = lc.getLogger(loggerName);
    logger.addAppender(sa);

  }

  @Test
  public void basic() throws InterruptedException {

    configure();
    String logMsg = "hello 1";
    logger.debug(logMsg);
    StatusPrinter.print(lc);
  }

  @Test
  public void tException() throws InterruptedException {
    configure();

    String logMsg = "hello 2";
    String exMsg = " just testing";
    Exception ex = new Exception(exMsg);
    logger.debug(logMsg, ex);
    StatusPrinter.print(lc);
  }

  @Test
  public void large() throws InterruptedException {
    configure();
    StringBuilder largeBuf = new StringBuilder();
    for (int i = 0; i < 2 * 1024 * 1024; i++) {
      largeBuf.append('a');
    }
    logger.debug(largeBuf.toString());

    String logMsg = "hello 3";
    logger.debug(logMsg);
    Thread.sleep(RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN+10);
    logger.debug(logMsg);
    StatusPrinter.print(lc);
  }

  @Test
  public void LBCLASSIC_50() throws JoranException {

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(lc);
    lc.reset();
    configurator.doConfigure(ClassicTestConstants.JORAN_INPUT_PREFIX
        + "syslog_LBCLASSIC_50.xml");

    org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    logger.info("hello");
    StatusPrinter.print(lc);
  }
}
