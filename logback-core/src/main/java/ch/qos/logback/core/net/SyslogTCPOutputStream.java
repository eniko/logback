package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * SyslogTCPOutputStream is a wrapper around the {@link Socket} class so that it
 * behaves like an {@link OutputStream}.
 */
public class SyslogTCPOutputStream extends OutputStream implements SyslogOutputStream {

  private InetAddress address;
  private Socket s;
  private OutputStream os;
  final private int port;

  public SyslogTCPOutputStream(String syslogHost, int port) throws UnknownHostException,
      SocketException,
      IOException {
    this.address = InetAddress.getByName(syslogHost);
    this.port = port;
    this.s = new Socket(this.address, this.port);
    os = s.getOutputStream();
  }

    @Override
  public void write(byte[] byteArray, int offset, int len) throws IOException {
     os.write(byteArray, offset, len);
  }

    @Override
  public void flush() throws IOException {
    os.flush();
  }

    @Override
  public void close() throws IOException {
    if (os != null) {
        os.close();
        os = null;
    }
    if (s != null) {
        s.close();
        s = null;
    }
  }

    @Override
  public void write(int b) throws IOException {
    os.write(b);
  }
}
