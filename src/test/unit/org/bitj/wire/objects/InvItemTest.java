package org.bitj.wire.objects;

import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.net.ProtocolException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InvItemTest extends BaseTest {

  static Sha256Hash HASH = new Sha256Hash(bytes(
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
    240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
  ));

  @Test
  public void serialize() throws Exception {
    InvItem invItem = new InvItem(InvItem.Type.Transaction, HASH);
    assertEquals(
      invItem.serialize(),
      bytes(
        1, 0, 0, 0,  // 1 - transaction
        255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240,
        15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
      )
    );
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      2, 0, 0, 0,  // 2 - block
      255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240,
      15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
    );
    InvItem invItem = new InvItem(InvItem.Type.Block, HASH);
    assertEquals(InvItem.deserialize(in), invItem);
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void deserialize_WhenInvItemTypeIsInvalid() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      3, 0, 0, 0,  // 3 - invalid type
      255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240,
      15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
    );
    InvItem.deserialize(in);
  }

  @Test
  public void serdeserConsistency() throws Exception {
    InvItem invItem = new InvItem(InvItem.Type.Block, new Sha256Hash(randomBytes(32)));
    InvItem deserializedInvItem = InvItem.deserialize(bitcoinStream(invItem.serialize()));
    assertEquals(deserializedInvItem, invItem);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new InvItem(InvItem.Type.Transaction, HASH).toString().contains("InvItem{"));
  }

}
