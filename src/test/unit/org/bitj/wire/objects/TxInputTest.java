package org.bitj.wire.objects;

import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TxInputTest extends BaseTest {

  // Example from https://en.bitcoin.it/wiki/Protocol_specification#tx
  static byte[] SERIALIZED_INPUT = bytes(
    // previous output (outpoint)
    "6D BD DB 08 5B 1D 8A F7  51 84 F0 BC 01 FA D5 8D" +
      "12 66 E9 B6 3B 50 88 19  90 E4 B4 0D 6A EE 36 29" +
      "00 00 00 00" +
      // 139 bytes long
      "8B" +
      // bytes
      "48 30 45 02 21 00 F3 58  1E 19 72 AE 8A C7 C7 36" +
      "7A 7A 25 3B C1 13 52 23  AD B9 A4 68 BB 3A 59 23" +
      "3F 45 BC 57 83 80 02 20  59 AF 01 CA 17 D0 0E 41" +
      "83 7A 1D 58 E9 7A A3 1B  AE 58 4E DE C2 8D 35 BD" +
      "96 92 36 90 91 3B AE 9A  01 41 04 9C 02 BF C9 7E" +
      "F2 36 CE 6D 8F E5 D9 40  13 C7 21 E9 15 98 2A CD" +
      "2B 12 B6 5D 9B 7D 59 E2  0A 84 20 05 F8 FC 4E 02" +
      "53 2E 87 3D 37 B9 6F 09  D6 D4 51 1A DA 8F 14 04" +
      "2F 46 61 4A 4C 70 C0 F1  4B EF F5" +
      // sequence
      "FF FF FF FF"
  );

  static TxOutputPointer TX_OUTPUT_POINTER = new TxOutputPointer(
    new Sha256Hash(
      "29 36 EE 6A 0D B4 E4 90  19 88 50 3B B6 E9 66 12" +
      "8D D5 FA 01 BC F0 84 51  F7 8A 1D 5B 08 DB BD 6D"
    ),
    0
  );

  static TxScript TX_SCRIPT = new TxScript(Arrays.copyOfRange(SERIALIZED_INPUT, 37, 176));

  @Test
  public void serialize() throws Exception {
    TxInput txInput = new TxInput(TX_OUTPUT_POINTER, TX_SCRIPT);
    byte[] serialized = txInput.serialize();
    assertEquals(serialized, SERIALIZED_INPUT);
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_INPUT);
    TxInput txInput = new TxInput(TX_OUTPUT_POINTER, TX_SCRIPT);
    assertEquals(TxInput.deserialize(in), txInput);
  }

  @Test
  public void getSizeInBytes() throws Exception {
    TxInput txInput = new TxInput(TX_OUTPUT_POINTER, TX_SCRIPT);
    assertEquals(txInput.getSizeInBytes(), SERIALIZED_INPUT.length);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new TxInput(TX_OUTPUT_POINTER, TX_SCRIPT).toString().contains("TxInput{"));
  }

}
