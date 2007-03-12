package ch.qos.logback.classic;

import org.slf4j.Marker;

import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import junit.framework.TestCase;

public class LoggerTest extends TestCase {

  LoggerContext context;
  Logger logger;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = new LoggerContext();
    context.setName("test");
    context.start();
    logger = context.getLogger(LoggerTest.class);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  private void addYesFilter() {
    YesFilter filter = new YesFilter();
    filter.start();
    context.addTurboFilter(filter);
  }
  
  private void addNoFilter() {
    NoFilter filter = new NoFilter();
    filter.start();
    context.addTurboFilter(filter);
  }

  public void testIsDebugEnabledWithYesFilter() {
    addYesFilter();
    logger.setLevel(Level.INFO);
    assertTrue(logger.isDebugEnabled());
  }

  public void testIsInfoEnabledWithYesFilter() {
    addYesFilter();
    logger.setLevel(Level.WARN);
    assertTrue(logger.isInfoEnabled());
  }

  public void testIsWarnEnabledWithYesFilter() {
    addYesFilter();
    logger.setLevel(Level.ERROR);
    assertTrue(logger.isWarnEnabled());
  }

  public void testIsErrorEnabledWithYesFilter() {
    addYesFilter();
    logger.setLevel(Level.OFF);
    assertTrue(logger.isErrorEnabled());
  }

  public void testIsEnabledForWithYesFilter() {
    addYesFilter();
    logger.setLevel(Level.ERROR);
    assertTrue(logger.isEnabledFor(Level.INFO));
  }
  
  public void testIsDebugEnabledWithNoFilter() {
    addNoFilter();
    logger.setLevel(Level.DEBUG);
    assertFalse(logger.isDebugEnabled());
  }
  
  public void testIsInfoEnabledWithNoFilter() {
    addNoFilter();
    logger.setLevel(Level.DEBUG);
    assertFalse(logger.isInfoEnabled());
  }
  
  public void testIsWarnEnabledWithNoFilter() {
    addNoFilter();
    logger.setLevel(Level.DEBUG);
    assertFalse(logger.isWarnEnabled());
  }
  
  public void testIsErrorEnabledWithNoFilter() {
    addNoFilter();
    logger.setLevel(Level.DEBUG);
    assertFalse(logger.isErrorEnabled());
  }

}

class YesFilter extends TurboFilter {
  @Override
  public FilterReply decide(Marker marker, Logger logger, Level level,
      String format, Object[] params, Throwable t) {
    return FilterReply.ACCEPT;
  }
}

class NoFilter extends TurboFilter {
  @Override
  public FilterReply decide(Marker marker, Logger logger, Level level,
      String format, Object[] params, Throwable t) {
    return FilterReply.DENY;
  }
}