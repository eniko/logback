package ch.qos.logback.classic.net.relpclient;

import java.io.IOException;
import java.net.UnknownHostException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Enikő Balogh
 */
public class RelpClientTest {

    RelpClient instance = null;
    
    public RelpClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        String host = "localhost";
        int port = 20514;
        instance = new RelpClient(host, port);
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of methods of class RelpClient.
     */
    @Test
    public void testRelpClient() throws UnknownHostException, IOException, RelpException {
        System.out.println("connect");        
        instance.connect();
        // TODO review the generated test code and remove the default call to fail.
        assertTrue(instance.isConnected());
        System.out.println("sendSyslogMessage");
        String message = "user message";
        String answer = instance.sendSyslogMessage(message);
        assertTrue(answer.indexOf("200 OK") > 0);
        System.out.println("disconnect");
        instance.disconnect();
        assertFalse(instance.isConnected());
    }

  

}