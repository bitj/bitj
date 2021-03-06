package org.bitj.wire;

import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;

import static org.testng.Assert.assertEquals;

public class BitcoinOutputStreamTest extends BaseTest {

  // !!! BitcoinOutputStream delegates most calls to the Wire. See the WireTest for tests !!!

  @Test
  public void writeIP_localhostIP4() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeIP(InetAddress.getByName("127.0.0.1"));
    assertEquals(out.toByteArray(), bytes(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 127, 0, 0, 1));
  }

  @Test
  public void writeIP_lanIP4() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeIP(InetAddress.getByName("192.168.0.1"));
    assertEquals(out.toByteArray(), bytes(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 192, 168, 0, 1));
  }

  @Test
  public void writeIP_publicIP4() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeIP(InetAddress.getByName("10.0.0.1"));
    assertEquals(out.toByteArray(), bytes(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 10, 0, 0, 1));
  }

  @Test
  public void writeIP_localhostIP6() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeIP(InetAddress.getByName("::1"));
    assertEquals(out.toByteArray(), bytes(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
  }

  @Test
  public void writeIP_publicIP6() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeIP(InetAddress.getByName("0000:0DB8:0000::0000:FF00:0042:8329"));
    assertEquals(out.toByteArray(), bytes("0000 0DB8 0000 0000 0000 FF00 0042 8329"));
  }

  @Test
  public void writeSha256HashLE() throws Exception {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    Sha256Hash hash = new Sha256Hash("63349bbc15682c1a0af8fc9b045c6b25077d697149ebd31a0000000000000000");
    out.writeSha256HashLE(hash);
    assertEquals(out.toByteArray(), bytes("00000000000000001ad3eb4971697d07256b5c049bfcf80a1a2c6815bc9b3463"));
  }

  @Test
  public void toByteArray() throws Exception {
    byte[] expectedBytes = bytes(255, 100, 0, 173);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    byteOut.write(expectedBytes);
    BitcoinOutputStream out = new BitcoinOutputStream(byteOut);
    assertEquals(out.toByteArray(), expectedBytes);
  }

}
