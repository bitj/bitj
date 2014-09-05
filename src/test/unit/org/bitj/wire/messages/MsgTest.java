package org.bitj.wire.messages;

import com.google.common.primitives.Bytes;
import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class MsgTest extends BaseTest {

  // Example from Wiki for verack empty message https://en.bitcoin.it/wiki/Protocol_specification#verack

  static class EmptyMsg extends Msg {
    @Override
    public String name() { return "verack"; }
    @Override
    public byte[] serializePayload() throws IOException { return bytes(); }
  }

  static byte[] EMPTY_MESSAGE_BYTES = bytes(
    "F9 BE B4 D9" +                             // magic
    "76 65 72 61 63 6B 00 00 00 00 00 00" +     // "verack"
    "00 00 00 00" +                             // 0 (length of payload)
    "5D F6 E0 E2"                               // checksum of the empty payload
  );

  @Test
  public void serializesEmptyMessage() throws Exception {
    assertEquals(serializeToBytes(new EmptyMsg()), EMPTY_MESSAGE_BYTES);
  }

  static class NonEmptyMsg extends Msg {
    @Override
    public String name() { return "nonempty"; }

    @Override
    public byte[] serializePayload() throws IOException { return bytes("FF 00 80 00 7F 00 81"); }
  }

  @Test
  public void serializesNonEmptyMessage() throws Exception {
    assertEquals(
      serializeToBytes(new NonEmptyMsg()),
      bytes(
        "F9 BE B4 D9" +                              // magic
        "6E 6F 6E 65 6D 70 74 79 00 00 00 00" +      // "nonempty"
        "07 00 00 00" +                              // length
        "87 A0 91 CB" +                              // checksum of the payload
        "FF 00 80 00 7F 00 81"                       // payload
      )
    );
  }

  @Test
  public void deserializes_WhenEmptyMessage() throws Exception {
    BitcoinInputStream in = bitcoinStream(EMPTY_MESSAGE_BYTES);
    VerackMsg version = (VerackMsg) Msg.deserialize(in);
  }

  @Test
  public void deserializes_WhenGarbageBytesPreceedTheMessage() throws Exception {
    byte[] headerBytes = bytes(
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
    VerackMsg version = (VerackMsg) Msg.deserialize(in);
  }

  @Test(expectedExceptions = Msg.InvalidChecksum.class)
  public void deserialize_WhenChecksumIsInvalid() throws Exception {
    byte[] headerBytes = bytes(
      "F9 BE B4 D9" +
        "76 65 72 73 69 6F 6E 00 00 00 00 00" +
        "64 00 00 00" +
        "70 6C 51 2F"  // invalid checksum
    );
    byte[] payloadBytes = VersionMsgTest.PAYLOAD2_BYTES;
    byte[] messageBytes = Bytes.concat(headerBytes, payloadBytes);
    BitcoinInputStream in = bitcoinStream(messageBytes);
    Msg.deserialize(in);
  }

  @Test(expectedExceptions = Msg.TooLarge.class)
  public void deserialize_WhenMessageIsTooLarge() throws Exception {
    byte[] headerBytes = bytes(
      "F9 BE B4 D9" +
        "76 65 72 61 63 6B 00 00 00 00 00 00" +
        "01 00 00 02" +       // 32MB + 1
        "5D F6 E0 E2"
    );
    BitcoinInputStream in = bitcoinStream(headerBytes);
    Msg.deserialize(in);
  }

  @Test(expectedExceptions = Msg.Unrecognized.class)
  public void deserialize_WhenMessageNameIsNotRecognized() throws Exception {
    byte[] headerBytes = bytes(
      "F9 BE B4 D9" +
      "00 00 00 00 00 00 00 00 00 00 00 00" +   // empty name
      "00 00 00 00" +
      "5D F6 E0 E2"
    );
    BitcoinInputStream in = bitcoinStream(headerBytes);
    Msg.deserialize(in);
  }

  @Test
  public void deserializesNonEmptyMessage() throws Exception {
    byte[] headerBytes = bytes(
      "F9 BE B4 D9" +
        "76 65 72 73 69 6F 6E 00 00 00 00 00" +
        "64 00 00 00" +
        "80 6C 51 2F"
    );
    byte[] payloadBytes = VersionMsgTest.PAYLOAD2_BYTES;
    byte[] messageBytes = Bytes.concat(headerBytes, payloadBytes);
    BitcoinInputStream in = bitcoinStream(messageBytes);
    VersionMsg version = (VersionMsg) Msg.deserialize(in);
    assertEquals(version.getStartHeight(), 212672);  // example message seems to be properly deserialized
  }

}
