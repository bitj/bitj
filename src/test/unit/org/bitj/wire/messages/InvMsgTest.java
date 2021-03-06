package org.bitj.wire.messages;

import com.google.common.collect.ImmutableSet;
import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.objects.InvItem;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InvMsgTest extends BaseTest {

  static Sha256Hash HASH1 = new Sha256Hash(bytes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));
  static Sha256Hash HASH2 = new Sha256Hash(bytes(101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132));
  static Sha256Hash HASH3 = new Sha256Hash(bytes(201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232));

  static InvMsg MESSAGE1 = new InvMsg(ImmutableSet.of(
    new InvItem(InvItem.Type.Block, HASH1),
    new InvItem(InvItem.Type.Transaction, HASH2),
    new InvItem(InvItem.Type.Error, HASH3)
  ));

  public static byte[] PAYLOAD1_BYTES = bytes(
    3,                       // 3 inv items
    // inv item 1
    2, 0, 0, 0,              // block
    32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1,
    // inv item 2
    1, 0, 0, 0,              // transaction
    132, 131, 130, 129, 128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 112, 111, 110, 109, 108, 107, 106, 105, 104, 103, 102, 101,
    // inv item 3
    0, 0, 0, 0,              // error
    232, 231, 230, 229, 228, 227, 226, 225, 224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 208, 207, 206, 205, 204, 203, 202, 201
  );

  @Test
  public void serializePayload() throws IOException {
    assertEquals(MESSAGE1.serializePayload(), PAYLOAD1_BYTES);
  }

  @Test
  public void deserializePayload() throws IOException {
    BitcoinInputStream in = bitcoinStream(PAYLOAD1_BYTES);
    assertEquals(InvMsg.deserializePayload(in), MESSAGE1);
  }

  @Test(expectedExceptions = InvMsg.TooMany.class)
  public void deserializePayload_WhenTooManyItems() throws IOException {
    byte[] maliciousPayload = new byte[PAYLOAD1_BYTES.length];
    System.arraycopy(PAYLOAD1_BYTES, 0, maliciousPayload, 0, PAYLOAD1_BYTES.length);
    // Set number of inv items to 500001
    maliciousPayload[0] = (byte) 253;
    maliciousPayload[1] = (byte) 0x51;
    maliciousPayload[2] = (byte) 0xc3;
    InvMsg.deserializePayload(bitcoinStream(maliciousPayload));
  }

  @Test
  public void serdeserConsistency() throws IOException {
    InvMsg msg = new InvMsg(ImmutableSet.of(
      new InvItem(InvItem.Type.Transaction, new Sha256Hash(randomBytes(32))),
      new InvItem(InvItem.Type.Transaction, new Sha256Hash(randomBytes(32)))
    ));
    InvMsg deserializedMsg = InvMsg.deserializePayload(bitcoinStream(msg.serializePayload()));
    assertEquals(deserializedMsg, msg);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MESSAGE1.toString().contains("InvMsg{"));
  }

  // TODO: what about empty inv, is it legal?

}
