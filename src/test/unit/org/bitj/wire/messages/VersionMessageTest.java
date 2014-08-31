package org.bitj.wire.messages;

import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ProtocolException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class VersionMessageTest extends BaseTest {

  // Example from https://en.bitcoin.it/wiki/Protocol_specification#version

  static VersionMessage MESSAGE1 = new VersionMessage.Builder()
    .version(31900)
    .services(1)                  // NODE_NETWORK
    .timestamp(1292899814)        // Mon Dec 20 21:50:14 EST 2010
    .nonce(1393780771635895773L)  // DD 9D 20 2C 3A B4 57 13 / LE
    .replaceUserAgent("")
    .startHeight(98645)
    .get();

  public static byte[] PAYLOAD1_BYTES = bytes(
    "9C 7C 00 00" +
      "01 00 00 00 00 00 00 00" +
      "E6 15 10 4D 00 00 00 00" +
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF 7F 00 00 01 20 8D" +
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF 7F 00 00 01 20 8D" +
      "DD 9D 20 2C 3A B4 57 13" +
      "00" +
      "55 81 01 00"
  );

  // Second example from https://en.bitcoin.it/wiki/Protocol_specification#version

  static VersionMessage MESSAGE2 = new VersionMessage.Builder()
    .version(60002)
    .services(1)                  // NODE_NETWORK
    .timestamp(1355854353)        // Mon Dec 20 21:50:14 EST 2010
    .nonce(7284544412836900411L)  // 3B 2E B3 5D 8C E6 17 65 / LE
    .replaceUserAgent("/Satoshi:0.7.2/")
    .startHeight(212672)
    .get();

  static byte[] PAYLOAD2_BYTES = bytes(
    "62 EA 00 00" +
      "01 00 00 00 00 00 00 00" +
      "11 B2 D0 50 00 00 00 00" +
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF 7F 00 00 01 20 8D" +
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF 7F 00 00 01 20 8D" +
      "3B 2E B3 5D 8C E6 17 65" +
      "0F 2F 53 61 74 6F 73 68 69 3A 30 2E 37 2E 32 2F" +
      "C0 3E 03 00"
  );

  @Test
  public void serializePayload_WikiExamples() throws Exception {
    assertEquals(MESSAGE1.serializePayload(), PAYLOAD1_BYTES);
    assertEquals(MESSAGE2.serializePayload(), PAYLOAD2_BYTES);
  }

  @Test
  public void deserializePayload_WikiExamples() throws IOException {
    assertEquals(VersionMessage.deserializePayload(bitcoinStream(PAYLOAD1_BYTES)), MESSAGE1);
    assertEquals(VersionMessage.deserializePayload(bitcoinStream(PAYLOAD2_BYTES)), MESSAGE2);
  }

  @Test(expectedExceptions = VersionMessage.Incompatible.class)
  public void deserializePayload_WhenProtocolVersionIsTooLow() throws IOException {
    VersionMessage.deserializePayload(bitcoinStream(0x9B, 0x7C, 0, 0));  // 31899
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void deserializePayload_WhenUserAgentStringIsTooLarge() throws IOException {
    byte[] maliciousPayload = new byte[PAYLOAD2_BYTES.length];
    System.arraycopy(PAYLOAD2_BYTES, 0, maliciousPayload, 0, PAYLOAD2_BYTES.length);
    // Set User-Agent string length as 2 GB (2147483647L)
    maliciousPayload[80] = (byte) 254;
    maliciousPayload[81] = (byte) 255;
    maliciousPayload[82] = (byte) 255;
    maliciousPayload[83] = (byte) 255;
    maliciousPayload[84] = 127;
    VersionMessage.deserializePayload(bitcoinStream(maliciousPayload));
  }

  @Test
  public void serdeserConsistency() throws Exception {
    VersionMessage version = new VersionMessage.Builder().get();
    byte[] bytes = version.serializePayload();
    BitcoinInputStream in = new BitcoinInputStream(new ByteArrayInputStream(bytes));
    VersionMessage deserialized = VersionMessage.deserializePayload(in);
    assertEquals(version, deserialized);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MESSAGE1.toString().contains("VersionMessage{"));
  }

}
