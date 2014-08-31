package org.bitj.wire.objects;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import org.bitj.BaseTest;
import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.Wire;
import org.testng.annotations.Test;

import java.net.ProtocolException;
import java.time.Instant;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TxTest extends BaseTest {

  // Example from https://en.bitcoin.it/wiki/Protocol_specification#tx
  static byte[] SERIALIZED = bytes(
    "                         01 00 00 00 01 6D BD DB" +
      "08 5B 1D 8A F7 51 84 F0  BC 01 FA D5 8D 12 66 E9" +
      "B6 3B 50 88 19 90 E4 B4  0D 6A EE 36 29 00 00 00" +
      "00 8B 48 30 45 02 21 00  F3 58 1E 19 72 AE 8A C7" +
      "C7 36 7A 7A 25 3B C1 13  52 23 AD B9 A4 68 BB 3A" +
      "59 23 3F 45 BC 57 83 80  02 20 59 AF 01 CA 17 D0" +
      "0E 41 83 7A 1D 58 E9 7A  A3 1B AE 58 4E DE C2 8D" +
      "35 BD 96 92 36 90 91 3B  AE 9A 01 41 04 9C 02 BF" +
      "C9 7E F2 36 CE 6D 8F E5  D9 40 13 C7 21 E9 15 98" +
      "2A CD 2B 12 B6 5D 9B 7D  59 E2 0A 84 20 05 F8 FC" +
      "4E 02 53 2E 87 3D 37 B9  6F 09 D6 D4 51 1A DA 8F" +
      "14 04 2F 46 61 4A 4C 70  C0 F1 4B EF F5 FF FF FF" +
      "FF 02 40 4B 4C 00 00 00  00 00 19 76 A9 14 1A A0" +
      "CD 1C BE A6 E7 45 8A 7A  BA D5 12 A9 D9 EA 1A FB" +
      "22 5E 88 AC 80 FA E9 C7  00 00 00 00 19 76 A9 14" +
      "0E AB 5B EA 43 6A 04 84  CF AB 12 48 5E FD A0 B7" +
      "8B 4E CC 52 88 AC 00 00  00 00"
  );

  static ImmutableList<TxInput> EMPTY_INPUTS = new ImmutableList.Builder<TxInput>().build();
  static ImmutableList<TxOutput> EMPTY_OUTPUTS = new ImmutableList.Builder<TxOutput>().build();
  static TxInput TX_INPUT = new TxInput(TxInputTest.TX_OUTPUT_POINTER, TxInputTest.TX_SCRIPT);
  static TxOutput TX_OUTPUT = new TxOutput(1, TxOutputTest.TX_SCRIPT);

  @Test
  public void serializeEmpty() throws Exception {
    Tx tx = new Tx(2, EMPTY_INPUTS, EMPTY_OUTPUTS, 257);
    byte[] serialized = tx.serialize();
    assertEquals(serialized, bytes(
      "02 00 00 00" +  // version
        "00" +           // 0 inputs
        "00" +           // 0 outputs
        "01 01 00 00"    // nLockTime
    ));
  }

  @Test
  public void serialize() throws Exception {
    ImmutableList.Builder<TxInput> inputs = new ImmutableList.Builder<>();
    inputs.add(TX_INPUT);

    ImmutableList.Builder<TxOutput> outputs = new ImmutableList.Builder<>();
    outputs.add(TX_OUTPUT);

    Tx tx = new Tx(inputs.build(), outputs.build());
    byte[] serialized = tx.serialize();

    // We only check some data here because inputs and outputs serialization
    // is already tested in TxInputTest and TxOutputTest
    assertEquals(serialized.length, 224);
    assertEquals(serialized[0], 1); // version byte 1
    assertEquals(serialized[1], 0); // version byte 2
    assertEquals(serialized[2], 0); // version byte 3
    assertEquals(serialized[3], 0); // version byte 4
    assertEquals(serialized[4], 1); // number of inputs
    assertEquals(serialized[220], 0); // locktime byte 1
    assertEquals(serialized[221], 0); // locktime byte 2
    assertEquals(serialized[222], 0); // locktime byte 3
    assertEquals(serialized[223], 0); // locktime byte 4
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*version.*")
  public void deserialize_unexpectedVersion() throws Exception {
    BitcoinInputStream in = bitcoinStream(messVersion(SERIALIZED));
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*0 inputs is illegal")
  public void deserialize_zeroInputs() throws Exception {
    BitcoinInputStream in = bitcoinStream(messInputs(SERIALIZED));
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*0 outputs is illegal")
  public void deserialize_zeroOutputs() throws Exception {
    BitcoinInputStream in = bitcoinStream(messOutputs(SERIALIZED));
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*> 100000")
  public void deserialize_txSizeOverLimitInInputs() throws Exception {
    int inputSize = TxInputTest.SERIALIZED_INPUT.length; // 180
    int times = 100_000 / inputSize + 1;
    assertEquals(times, 556); // sanity check in case SERIALIZED_INPUT example changed
    byte[][] arrayOfArrays = new byte[times][inputSize];
    for (int i = 0; i < times; i++)
      System.arraycopy(TxInputTest.SERIALIZED_INPUT, 0, arrayOfArrays[i], 0, inputSize);
    byte[] header = bytes("01 00 00 00" /*version*/ + "fd 2c 02" /*556 inputs*/);
    byte[] inputs = Bytes.concat(arrayOfArrays);
    byte[] serializedOver100k = Bytes.concat(header, inputs);
    BitcoinInputStream in = bitcoinStream(serializedOver100k);
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*> 100000")
  public void deserialize_txSizeOverLimitInOutputs() throws Exception {
    int inputSize = TxInputTest.SERIALIZED_INPUT.length; // 180
    int times = 100_000 / inputSize;  // just below 100_000
    assertEquals(times, 555); // sanity check in case SERIALIZED_INPUT example changed
    byte[][] arrayOfArrays = new byte[times][inputSize];
    for (int i = 0; i < times; i++)
      System.arraycopy(TxInputTest.SERIALIZED_INPUT, 0, arrayOfArrays[i], 0, inputSize);
    byte[] header = bytes("01 00 00 00" /*version*/ + "fd 2b 02" /*555 inputs*/);
    byte[] inputs = Bytes.concat(arrayOfArrays);
    byte[] outputs = Bytes.concat(new byte[] {3}, TxOutputTest.SERIALIZED_OUTPUT, TxOutputTest.SERIALIZED_OUTPUT, TxOutputTest.SERIALIZED_OUTPUT);
    byte[] serializedOver100k = Bytes.concat(header, inputs, outputs);
    BitcoinInputStream in = bitcoinStream(serializedOver100k);
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = Tx.TooMany.class, expectedExceptionsMessageRegExp = ".*inputs.*")
  public void deserialize_tooManyClaimedInputs() throws Exception {
    BitcoinInputStream in = bitcoinStream("01 00 00 00" /*version*/ + "fd 00 0a" /*lots of inputs*/);
    Tx.deserialize(in);
  }

  @Test(expectedExceptions = Tx.TooMany.class, expectedExceptionsMessageRegExp = ".*outputs.*")
  public void deserialize_tooManyClaimedOutputs() throws Exception {
    BitcoinInputStream in = bitcoinStream(
      "01 00 00 00" + // version
        "01" + Debug.bytesToHex(TxInputTest.SERIALIZED_INPUT) + // 1 input
        "fd 00 2c" // lots of outputs
    );
    Tx.deserialize(in);
  }

  @Test
  public void deserialize() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED);
    Tx tx = Tx.deserialize(in);

    assertEquals(tx.getVersion(), 1);

    assertEquals(tx.getInputs().size(), 1);
    assertEquals(tx.getInputs().get(0).getSequence(), Wire.MAX_UINT_32);

    assertEquals(tx.getOutputs().size(), 2);
    assertEquals(tx.getOutputs().get(0).getValue(), 5000000L);
    assertEquals(tx.getOutputs().get(1).getValue(), 3354000000L);

    assertEquals(tx.getUnlockAfter(), 0);
  }

  @Test
  public void sizeInBytesOfEmptyTx() throws Exception {
    // empty tx still has version, locktime and numbers of inputs/outputs, thus 10 bytes
    Tx tx = new Tx(EMPTY_INPUTS, EMPTY_OUTPUTS);
    assertEquals(tx.getSizeInBytes(), 10);
  }

  @Test
  public void sizeInBytesOfTypicalDeserializedTx() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED);
    Tx tx = Tx.deserialize(in);
    assertEquals(tx.getSizeInBytes(), 258);
  }

  @Test
  public void sizeInBytesOfTypicalConstructedTx() throws Exception {
    ImmutableList.Builder<TxInput> inputs = new ImmutableList.Builder<>();
    inputs.add(TX_INPUT);
    ImmutableList.Builder<TxOutput> outputs = new ImmutableList.Builder<>();
    outputs.add(TX_OUTPUT);
    Tx tx = new Tx(inputs.build(), outputs.build());
    assertEquals(tx.getSizeInBytes(), 224);
  }

  @Test
  public void constructionInitializesUnlockAfterCorrectly() throws Exception {
    Instant threshold = Instant.parse("1985-11-05T00:53:20Z");
    Tx tx = new Tx(1, EMPTY_INPUTS, EMPTY_OUTPUTS, threshold);
    assertEquals(tx.getUnlockAfter(), 500_000_000);
  }

  @Test
  public void equalsImplemented() throws Exception {
    ImmutableList.Builder<TxInput> inputs = new ImmutableList.Builder<>();
    inputs.add(TX_INPUT);
    ImmutableList.Builder<TxOutput> outputs = new ImmutableList.Builder<>();
    outputs.add(TX_OUTPUT);
    Tx tx = new Tx(inputs.build(), outputs.build());
    Tx tx2 = new Tx(inputs.build(), outputs.build());
    assertEquals(tx, tx2);
  }

  @Test
  public void toStringImplemented() throws Exception {
    Tx tx = new Tx(EMPTY_INPUTS, EMPTY_OUTPUTS);
    assertTrue(tx.toString().contains("Tx{"));
  }

  private byte[] messVersion(byte[] serialized) {
    byte[] copy = serialized.clone();
    copy[0] = 2;
    return copy;
  }

  private byte[] messInputs(byte[] serialized) {
    byte[] copy = serialized.clone();
    copy[4] = 0;
    return copy;
  }

  private byte[] messOutputs(byte[] serialized) {
    byte[] copy = serialized.clone();
    copy[185] = 0;
    return copy;
  }

}
