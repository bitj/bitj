package org.bitj.wire.messages;

import com.google.common.collect.ImmutableSet;
import org.bitj.BaseTest;
import org.bitj.wire.Wire;
import org.bitj.wire.objects.PeerAddress;
import org.testng.annotations.Test;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AddrMsgTest extends BaseTest {

  static long TIMESTAMP = 1292899814;  // Mon Dec 20 21:50:14 EST 2010;
  static InetAddress IP4 = ip("213.180.141.140");
  static InetAddress IP6 = ip("4001:0db8:0022:0000:0000:ff00:0042:8329");

  static AddrMsg MSG = new AddrMsg(
    ImmutableSet.of(
      new PeerAddress.Builder().timestampOfTheLastMessage(TIMESTAMP).ip(IP4).port(8333).services(1).get(),
      new PeerAddress.Builder().timestampOfTheLastMessage(0).ip(IP6).port(65535).services(0).get()
    )
  );

  static byte[] MSG_BYTES = bytes(
    // count
    "02" +
      // net_addr 1
      "E6 15 10 4D" +
      "01 00 00 00 00 00 00 00" +
      "00 00 00 00 00 00 00 00 00 00 FF FF D5 B4 8D 8C" +
      "20 8D" +
      // net_addr 2
      "00 00 00 00" +
      "00 00 00 00 00 00 00 00" +
      "40 01 0d b8 00 22 00 00 00 00 ff 00 00 42 83 29" +
      "FF FF"
  );

  @Test
  public void serializePayload() throws Exception {
    byte[] msgBytes = MSG.serializePayload();
    assertEquals(msgBytes, MSG_BYTES);
  }

  @Test
  public void deserializePayload() throws Exception {
    AddrMsg msg = AddrMsg.deserializePayload(bitcoinStream(MSG_BYTES));
    assertEquals(msg, MSG);
  }

  @Test(expectedExceptions = AddrMsg.TooMany.class)
  public void deserializePayload_WhenToManyAddresses() throws Exception {
    byte[] maliciousBytes = Wire.unsignedIntToVarBytes(1001);
    AddrMsg.deserializePayload(bitcoinStream(maliciousBytes));
  }

  @Test(expectedExceptions = EOFException.class)
  public void deserializePayload_WhenToLittleAddresses() throws Exception {
    byte[] maliciousBytes = Wire.unsignedIntToVarBytes(1); // claimd 1 address but got none in the stream
    AddrMsg.deserializePayload(bitcoinStream(maliciousBytes));
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MSG.toString().contains("AddrMsg{"));
  }

  private static InetAddress ip(String s) {
    try {
      return InetAddress.getByName(s);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

}
