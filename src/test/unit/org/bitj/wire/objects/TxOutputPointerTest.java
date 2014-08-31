package org.bitj.wire.objects;

import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.Wire;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TxOutputPointerTest extends BaseTest {

  static Sha256Hash HASH = new Sha256Hash(bytes(
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
    240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255
  ));

  @Test
  public void serialize() throws Exception {
    TxOutputPointer txOutputPointer = new TxOutputPointer(HASH, 255);
    assertEquals(
      txOutputPointer.serialize(),
      bytes(
        // hash
        255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240,
        15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,
        // output index
        255, 0, 0, 0
      )
    );
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240,
      15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,
      // output index
      255, 255, 255, 255
    );
    TxOutputPointer txOutputPointer = new TxOutputPointer(HASH, Wire.MAX_UINT_32);
    assertEquals(TxOutputPointer.deserialize(in), txOutputPointer);
  }

  @Test
  public void serdeserConsistency() throws Exception {
    TxOutputPointer txOutputPointer = new TxOutputPointer(new Sha256Hash(randomBytes(32)), 7654321);
    TxOutputPointer deserializedTxOutputPointer = TxOutputPointer.deserialize(bitcoinStream(txOutputPointer.serialize()));
    assertEquals(deserializedTxOutputPointer, txOutputPointer);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new TxOutputPointer(HASH, 0).toString().contains("TxOutputPointer{"));
  }

}
