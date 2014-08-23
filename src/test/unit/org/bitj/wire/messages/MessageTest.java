package org.bitj.wire.messages;

import com.google.common.primitives.Bytes;
import org.bitj.BaseTest;
import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class MessageTest extends BaseTest {

  // Example from Wiki for verack empty message https://en.bitcoin.it/wiki/Protocol_specification#verack

  static class EmptyMessage extends Message {
    @Override
    public String name() { return "verack"; }
    @Override
    public byte[] serializePayload() throws IOException { return bytes(); }
  }

  static byte[] EMPTY_MESSAGE_BYTES = bytes(
    0xF9, 0xBE, 0xB4, 0xD9,                                                      // magic
    0x76, 0x65, 0x72, 0x61, 0x63, 0x6B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,      // "verack"
    0x00, 0x00, 0x00, 0x00,                                                      // 0 (length of payload)
    0x5D, 0xF6, 0xE0, 0xE2                                                       // checksum of the empty payload
  );

  @Test
  public void serializesEmptyMessage() throws Exception {
    assertEquals(serializeToBytes(new EmptyMessage()), EMPTY_MESSAGE_BYTES);
  }

  static class NonEmptyMessage extends Message {
    @Override
    public String name() { return "nonempty"; }

    @Override
    public byte[] serializePayload() throws IOException { return bytes(0xFF, 0x00, 0x80, 0x00, 0x7F, 0x00, 0x81); }
  }

  @Test
  public void serializesNonEmptyMessage() throws Exception {
    assertEquals(
      serializeToBytes(new NonEmptyMessage()),
      bytes(
        0xF9, 0xBE, 0xB4, 0xD9,                              // magic
        110, 111, 110, 101, 109, 112, 116, 121, 0, 0, 0, 0,  // "nonempty"
        7, 0, 0, 0,                                          // length
        0x87, 0xA0, 0x91, 0xCB,                              // checksum of the payload
        0xFF, 0x00, 0x80, 0x00, 0x7F, 0x00, 0x81             // payload
      )
    );
  }

  @Test
  public void deserializes_WhenEmptyMessage() throws Exception {
    BitcoinInputStream in = bitcoinStream(EMPTY_MESSAGE_BYTES);
    VerackMessage version = (VerackMessage) Message.deserialize(in);
  }

  @Test
  public void deserializes_WhenGarbageBytesPreceedTheMessage() throws Exception {
    byte[] headerBytes = Debug.hexToBytes(
      "00 00 00 00" +  // garbage
        "F9 00 00 00" +  // garbage
        "F9 BE 00 00" +  // garbage
        "F9 BE B4 00" +  // garbage
        "F9 BE B4 D9" +  // magic, finally
        "76 65 72 61 63 6B 00 00 00 00 00 00" +
        "00 00 00 00" +
        "5D F6 E0 E2"
    );
    BitcoinInputStream in = bitcoinStream(headerBytes);
    VerackMessage version = (VerackMessage) Message.deserialize(in);
  }

  @Test(expectedExceptions = Message.InvalidChecksum.class)
  public void deserialize_WhenChecksumIsInvalid() throws Exception {
    byte[] headerBytes = Debug.hexToBytes(
      "F9 BE B4 D9" +
        "76 65 72 73 69 6F 6E 00 00 00 00 00" +
        "64 00 00 00" +
        "70 6C 51 2F"  // invalid checksum
    );
    byte[] payloadBytes = VersionMessageTest.PAYLOAD2_BYTES;
    byte[] messageBytes = Bytes.concat(headerBytes, payloadBytes);
    BitcoinInputStream in = bitcoinStream(messageBytes);
    Message.deserialize(in);
  }

  @Test(expectedExceptions = Message.TooLarge.class)
  public void deserialize_WhenMessageIsTooLarge() throws Exception {
    byte[] headerBytes = Debug.hexToBytes(
      "F9 BE B4 D9" +
        "76 65 72 61 63 6B 00 00 00 00 00 00" +
        "01 00 00 02" +       // 32MB + 1
        "5D F6 E0 E2"
    );
    BitcoinInputStream in = bitcoinStream(headerBytes);
    Message.deserialize(in);
  }

  @Test(expectedExceptions = Message.Unrecognized.class)
  public void deserialize_WhenMessageNameIsNotRecognized() throws Exception {
    byte[] headerBytes = Debug.hexToBytes(
      "F9 BE B4 D9" +
      "00 00 00 00 00 00 00 00 00 00 00 00" +   // empty name
      "00 00 00 00" +
      "5D F6 E0 E2"
    );
    BitcoinInputStream in = bitcoinStream(headerBytes);
    Message.deserialize(in);
  }

  @Test
  public void deserializesNonEmptyMessage() throws Exception {
    byte[] headerBytes = Debug.hexToBytes(
      "F9 BE B4 D9" +
        "76 65 72 73 69 6F 6E 00 00 00 00 00" +
        "64 00 00 00" +
        "80 6C 51 2F"
    );
    byte[] payloadBytes = VersionMessageTest.PAYLOAD2_BYTES;
    byte[] messageBytes = Bytes.concat(headerBytes, payloadBytes);
    BitcoinInputStream in = bitcoinStream(messageBytes);
    VersionMessage version = (VersionMessage) Message.deserialize(in);
    assertEquals(version.getStartHeight(), 212672);  // example message seems to be properly deserialized
  }

}
