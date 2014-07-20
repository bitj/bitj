package org.bitj.wire.messages;

import com.google.common.collect.ImmutableSet;
import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.InvItem;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InvMessageTest extends BaseTest {

  static byte[] HASH1 = bytes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32);
  static byte[] HASH2 = bytes(101, 202, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132);
  static byte[] HASH3 = bytes(201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232);

  static InvMessage MESSAGE1 = new InvMessage(ImmutableSet.of(
    new InvItem(InvItem.Type.Block, HASH1),
    new InvItem(InvItem.Type.Transaction, HASH2),
    new InvItem(InvItem.Type.Error, HASH3)
  ));

  public static byte[] PAYLOAD1_BYTES = bytes(
    3,                       // 3 inv items
    // inv item 1
    2, 0, 0, 0,              // block
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
    // inv item 2
    1, 0, 0, 0,              // transaction
    101, 202, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132,
    // inv item 3
    0, 0, 0, 0,              // error
    201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232
  );

  @Test
  public void serializePayload() throws IOException {
    assertEquals(MESSAGE1.serializePayload(), PAYLOAD1_BYTES);
  }

  @Test
  public void deserializePayload() throws IOException {
    BitcoinInputStream in = bitcoinStream(PAYLOAD1_BYTES);
    assertEquals(InvMessage.deserializePayload(in), MESSAGE1);
  }

  @Test(expectedExceptions = InvMessage.TooMany.class)
  public void deserializePayload_WhenTooManyItems() throws IOException {
    byte[] maliciousPayload = new byte[PAYLOAD1_BYTES.length];
    System.arraycopy(PAYLOAD1_BYTES, 0, maliciousPayload, 0, PAYLOAD1_BYTES.length);
    // Set number of inv items to 500001
    maliciousPayload[0] = (byte) 253;
    maliciousPayload[1] = (byte) 0x51;
    maliciousPayload[2] = (byte) 0xc3;
    InvMessage.deserializePayload(bitcoinStream(maliciousPayload));
  }

  @Test
  public void serdeserConsistency() throws IOException {
    InvMessage msg = new InvMessage(ImmutableSet.of(
      new InvItem(InvItem.Type.Transaction, randomBytes(32)),
      new InvItem(InvItem.Type.Transaction, randomBytes(32))
    ));
    InvMessage deserializedMsg = InvMessage.deserializePayload(bitcoinStream(msg.serializePayload()));
    assertEquals(deserializedMsg, msg);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MESSAGE1.toString().contains("InvMessage{"));
  }

  // TODO: what about empty inv, is it legal?

}
