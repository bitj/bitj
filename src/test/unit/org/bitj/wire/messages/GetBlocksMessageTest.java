package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetBlocksMessageTest extends BaseTest {

  static Sha256Hash HASH_GENESIS = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000000");
  static Sha256Hash HASH1 =        new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000001");
  static Sha256Hash HASH2 =        new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000002");
  static Sha256Hash HASH3 =        new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000003");
  static Sha256Hash HASH4 =        new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000004");
  static Sha256Hash HASH_STOP =    new Sha256Hash("1111111111111111111111111111111111111111111111111111111111111111");

  static GetBlocksMessage MESSAGE1 = new GetBlocksMessage(
    31900,
    ImmutableList.of(HASH2, HASH1, HASH_GENESIS),
    HASH_STOP
  );

  public static byte[] PAYLOAD1_BYTES = bytes(
    "9C 7C 00 00" +   // version
      "03" +          // number of hashes in block locator
      // block locator
      "0200000000000000000000000000000000000000000000000000000000000000" +  // HASH2
      "0100000000000000000000000000000000000000000000000000000000000000" +  // HASH1
      "0000000000000000000000000000000000000000000000000000000000000000" +  // HASH_GENESIS
      // stop hash
      "1111111111111111111111111111111111111111111111111111111111111111"    // HASH_STOP
  );

  @Test
  public void serializePayload() throws IOException {
    assertEquals(MESSAGE1.serializePayload(), PAYLOAD1_BYTES);
  }

  @Test
  public void deserializePayload() throws IOException {
    BitcoinInputStream in = bitcoinStream(PAYLOAD1_BYTES);
    assertEquals(GetBlocksMessage.deserializePayload(in), MESSAGE1);
  }

  @Test(expectedExceptions = GetBlocksMessage.TooMany.class)
  public void deserializePayload_WhenTooManyHashes() throws IOException {
    byte[] maliciousPayload = new byte[PAYLOAD1_BYTES.length];
    System.arraycopy(PAYLOAD1_BYTES, 0, maliciousPayload, 0, PAYLOAD1_BYTES.length);
    // Set number of hashes to 38 (one more than allowed)
    maliciousPayload[4] = (byte) 38;
    GetBlocksMessage.deserializePayload(bitcoinStream(maliciousPayload));
  }

  @Test
  public void serdeserConsistency() throws IOException {
    GetBlocksMessage msg = new GetBlocksMessage(ImmutableList.of(HASH4, HASH3, HASH2, HASH1, HASH_GENESIS));
    GetBlocksMessage deserializedMsg = GetBlocksMessage.deserializePayload(bitcoinStream(msg.serializePayload()));
    assertEquals(deserializedMsg, msg);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MESSAGE1.toString().contains("GetBlocksMessage{"));
  }

}
