package org.bitj.wire;

import org.bitj.BaseTest;
import org.bitj.utils.Debug;
import org.bitj.wire.messages.VersionMessage;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.ProtocolException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InvItemTest extends BaseTest {

  static byte[] HASH = bytes(
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
    240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
  );

  @Test
  public void serialize() throws Exception {
    InvItem invItem = new InvItem(InvItem.Type.Transaction, HASH);
    assertEquals(
      invItem.serialize(),
      bytes(
        1, 0, 0, 0,  // 1 - transaction
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
      )
    );
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      2, 0, 0, 0,  // 2 - block
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
      240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
    );
    InvItem invItem = new InvItem(InvItem.Type.Block, HASH);
    assertEquals(InvItem.deserialize(in), invItem);
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void deserialize_WhenInvItemTypeIsInvalid() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      3, 0, 0, 0,  // invalid type
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
      240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
    );
    InvItem.deserialize(in);
  }

  @Test
  public void serdeserConsistency() throws Exception {
    InvItem invItem = new InvItem(InvItem.Type.Block, randomBytes(32));
    InvItem deserializedInvItem = InvItem.deserialize(bitcoinStream(invItem.serialize()));
    assertEquals(deserializedInvItem, invItem);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new InvItem(InvItem.Type.Transaction, HASH).toString().contains("InvItem{"));
  }

}
