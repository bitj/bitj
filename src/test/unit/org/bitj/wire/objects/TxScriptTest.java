package org.bitj.wire.objects;

import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.net.ProtocolException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TxScriptTest extends BaseTest {

  // Example from https://en.bitcoin.it/wiki/Protocol_specification#tx
  static byte[] SERIALIZED_SCRIPT = bytes(
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
      "2F 46 61 4A 4C 70 C0 F1  4B EF F5"
  );

  @Test
  public void serialize() throws Exception {
    byte[] scriptBytes = Arrays.copyOfRange(SERIALIZED_SCRIPT, 1, SERIALIZED_SCRIPT.length);
    TxScript txScript = new TxScript(scriptBytes);
    byte[] serialized = txScript.serialize();
    assertEquals(serialized, SERIALIZED_SCRIPT);
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_SCRIPT);
    byte[] scriptBytes = Arrays.copyOfRange(SERIALIZED_SCRIPT, 1, SERIALIZED_SCRIPT.length);
    TxScript txScript = new TxScript(scriptBytes);
    assertEquals(TxScript.deserialize(in, TxScript.Type.INPUT), txScript);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*0 bytes.*")
  public void deserializeScriptOf0Bytes() throws Exception {
    BitcoinInputStream in = bitcoinStream(new byte[]{ 0 });
    TxScript.deserialize(in, TxScript.Type.INPUT);
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void deserializeTooLargeOutputScript() throws Exception {
    BitcoinInputStream in = bitcoinStream("fd 11 27");  // 10001 as var int
    TxScript.deserialize(in, TxScript.Type.OUTPUT);
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void deserializeTooLargeInputScript() throws Exception {
    BitcoinInputStream in = bitcoinStream("fd 02 09");  // 521 as var int
    TxScript.deserialize(in, TxScript.Type.INPUT);
  }

  @Test
  public void serdeserConsistency() throws Exception {
    TxScript txScript = new TxScript(randomBytes(168));
    TxScript deserializedTxScript = TxScript.deserialize(bitcoinStream(txScript.serialize()), TxScript.Type.INPUT);
    assertEquals(deserializedTxScript, txScript);
  }

  @Test
  public void getSizeInBytes() throws Exception {
    byte[] scriptBytes = Arrays.copyOfRange(SERIALIZED_SCRIPT, 1, SERIALIZED_SCRIPT.length);
    TxScript txScript = new TxScript(scriptBytes);
    assertEquals(txScript.getSizeInBytes(), SERIALIZED_SCRIPT.length);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new TxScript(SERIALIZED_SCRIPT).toString().contains("TxScript{"));
  }

}
