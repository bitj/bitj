package org.bitj.wire;

import org.bitj.BaseTest;
import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.PeerAddress;
import org.testng.annotations.Test;

import java.net.InetAddress;

import static org.testng.Assert.*;

public class PeerAddressTest extends BaseTest {

  @Test
  public void serialize_IP4() throws Exception {
    PeerAddress peerAddress = new PeerAddress.Builder()
      .timestampOfTheLastMessage(1292899814)     // Mon Dec 20 21:50:14 EST 2010;
      .services(1)
      .ip(InetAddress.getByName("10.0.0.1"))
      .port(8333)
      .get();
    assertEquals(
      peerAddress.serialize(),
      Debug.hexToBytes(
        "E6 15 10 4D" +
          "01 00 00 00 00 00 00 00" +  // 1 (NODE_NETWORK: see services listed under version command)
          "00 00 00 00 00 00 00 00 00 00 FF FF 0A 00 00 01" +
          "20 8D"
      )
    );
  }

  @Test
  public void serialize_IP6() throws Exception {
    PeerAddress peerAddress = new PeerAddress.Builder()
      .timestampOfTheLastMessage(0)
      .services(0)
      .ip(InetAddress.getByName("0000:0db8:0000:0000:0000:ff00:0042:8329"))
      .port(18333)
      .get();
    assertEquals(
      peerAddress.serialize(),
      Debug.hexToBytes(
        "00 00 00 00" +
          "00 00 00 00 00 00 00 00" +  // 1 (NODE_NETWORK: see services listed under version command)
          "00 00 0d b8 00 00 00 00 00 00 ff 00 00 42 83 29" +
          "47 9D"
      )
    );
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      "E6 15 10 4D" +
      "01 00 00 00 00 00 00 00" +  // 1 (NODE_NETWORK: see services listed under version command)
      "00 00 00 00 00 00 00 00 00 00 FF FF 0A 00 00 01" +
      "20 8D"
    );
    PeerAddress peerAddress = new PeerAddress.Builder()
      .timestampOfTheLastMessage(1292899814)     // Mon Dec 20 21:50:14 EST 2010;
      .services(1)
      .ip(InetAddress.getByName("10.0.0.1"))
      .port(8333)
      .get();
    assertEquals(PeerAddress.deserialize(in), peerAddress);
  }

  // TODO: self consistency test

}
