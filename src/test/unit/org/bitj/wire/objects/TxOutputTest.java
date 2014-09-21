package org.bitj.wire.objects;

import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.testng.annotations.Test;

import java.net.ProtocolException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TxOutputTest extends BaseTest {

  // Example from https://en.bitcoin.it/wiki/Protocol_specification#tx
  static byte[] SERIALIZED_OUTPUT = bytes(
    // 0.05 BTC (5000000 satoshi)
    "40 4B 4C 00 00 00 00 00" +
      // pk_script is 25 bytes long
      "19" +
      // pk_script
      "76 A9 14 1A A0 CD 1C BE  A6 E7 45 8A 7A BA D5 12" +
      "A9 D9 EA 1A FB 22 5E 88  AC"
  );

  static TxScript TX_SCRIPT = new TxScript(Arrays.copyOfRange(SERIALIZED_OUTPUT, 9, 9+25));

  @Test
  public void serialize() throws Exception {
    TxOutput txOutput = new TxOutput(5000000, TX_SCRIPT);
    byte[] serialized = txOutput.serialize();
    assertEquals(serialized, SERIALIZED_OUTPUT);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*Illegal tx output amount.*")
  public void deserialize_whenValueIsNegative() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      // -1 satoshi
      "FF FF FF FF FF FF FF FF" +
        // pk_script is 25 bytes long
        "19" +
        // pk_script
        "76 A9 14 1A A0 CD 1C BE  A6 E7 45 8A 7A BA D5 12" +
        "A9 D9 EA 1A FB 22 5E 88  AC"
    );
    TxOutput.deserialize(in);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*Illegal tx output amount.*")
  public void deserialize_whenValueIsLargerThan21MlnBTC() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      // 21_000_001 satoshi
      "01 40 07 5A F0 75 07 00" +
        // pk_script is 25 bytes long
        "19" +
        // pk_script
        "76 A9 14 1A A0 CD 1C BE  A6 E7 45 8A 7A BA D5 12" +
        "A9 D9 EA 1A FB 22 5E 88  AC"
    );
    TxOutput.deserialize(in);
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_OUTPUT);
    TxOutput txOutput = new TxOutput(5000000, TX_SCRIPT);
    assertEquals(TxOutput.deserialize(in), txOutput);
  }

  @Test
  public void serdeserConsistency() throws Exception {
    long randomSatoshi = Math.abs(rng().nextInt());
    int randomScriptLen = Math.abs(rng().nextInt(256));
    TxScript randomScript = new TxScript(randomBytes(randomScriptLen));
    TxOutput txOutput = new TxOutput(randomSatoshi, randomScript);
    TxOutput deserializedTxOutput = TxOutput.deserialize(bitcoinStream(txOutput.serialize()));
    assertEquals(deserializedTxOutput, txOutput);
  }

  @Test
  public void getSizeInBytes() throws Exception {
    TxOutput txOutput = new TxOutput(5000000, TX_SCRIPT);
    assertEquals(txOutput.getSizeInBytes(), SERIALIZED_OUTPUT.length);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(new TxOutput(1, TX_SCRIPT).toString().contains("TxOutput{"));
  }

}
