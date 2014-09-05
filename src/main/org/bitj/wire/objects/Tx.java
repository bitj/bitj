package org.bitj.wire.objects;

import com.google.common.collect.ImmutableList;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.time.Instant;
import java.util.Objects;

public class Tx {

  /*
  Transaction standardness rules:
  - TODO: The transaction must be finalized: either its locktime must be in the past (or less than or equal to the current block height), or all of its sequence numbers must be 0xffffffff.
  - TODO: Each of the transaction’s inputs must be smaller than 500 bytes.
  - TODO: The transaction must not include any outputs which receive fewer than the defined minimum number of satoshis, currently 546.
  */

  /**
   * Based on:
   * https://github.com/bitcoin/bitcoin/blob/master/src/core.h#L217   # line "static const int CURRENT_VERSION=1;"
   */
  public static final long VERSION = 1;

  /**
   * Below this value unlockAfter is interpreted as a block number, otherwise as a timestamp. Based on:
   * https://github.com/bitcoin/bitcoin/blob/master/src/main.h#L63  # line "static const unsigned int LOCKTIME_THRESHOLD = 500000000;"
   */
  public static final int UNLOCK_SEMANTIC_THRESHOLD = 500_000_000; // 1985-11-05 00:53:20 UTC

  private long version = VERSION;
  private ImmutableList<TxInput> inputs;
  private ImmutableList<TxOutput> outputs;
  private long unlockAfter = 0;

  /**
   * Based on:
   * https://github.com/bitcoin/bitcoin/blob/master/src/main.h#L43  # line "static const unsigned int MAX_STANDARD_TX_SIZE = 100000;"
   */
  public static final long MAX_SIZE = 100_000;
  public static final long MAX_NUMBER_OF_INPUTS = MAX_SIZE / TxInput.MIN_SIZE;
  public static final long MAX_NUMBER_OF_OUTPUTS = MAX_SIZE / TxOutput.MIN_SIZE;

  public Tx(ImmutableList<TxInput> inputs, ImmutableList<TxOutput> outputs) {
    this.inputs = inputs;
    this.outputs = outputs;
  }

  public Tx(long version, ImmutableList<TxInput> inputs, ImmutableList<TxOutput> outputs, long unlockAfterBlock) {
    this.version = version;
    this.inputs = inputs;
    this.outputs = outputs;
    this.unlockAfter = unlockAfterBlock;
  }

  public Tx(long version, ImmutableList<TxInput> inputs, ImmutableList<TxOutput> outputs, Instant unlockAfterTimestamp) {
    this.version = version;
    this.inputs = inputs;
    this.outputs = outputs;
    if (unlockAfterTimestamp.getEpochSecond() < UNLOCK_SEMANTIC_THRESHOLD)
      throw new IllegalArgumentException("Timestamp must be >= 1985-11-05 00:53:20 UTC");
    this.unlockAfter = unlockAfterTimestamp.getEpochSecond();
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(512)); // most tx have less then 512 bytes https://en.bitcoin.it/wiki/Maximum_transaction_rate
    out.writeUnsInt32LE(version);
    out.writeUnsVarInt(inputs.size());
    for (TxInput input : inputs)
      out.write(input.serialize());
    out.writeUnsVarInt(outputs.size());
    for (TxOutput output : outputs)
      out.write(output.serialize());
    out.writeUnsInt32LE(unlockAfter);
    return out.toByteArray();
  }

  public static Tx deserialize(BitcoinInputStream in) throws IOException {
    // version
    long version = in.readUnsInt32LE();
    throwIfUnexpectedVersion(version);
    int sizeInBytes = 4;

    // inputs
    BigInteger nOfInputs = in.readUnsVarInt();
    throwIfNumberOfInputsIsInvalid(nOfInputs);
    sizeInBytes += Wire.unsIntVarSizeInBytes(nOfInputs.longValue());
    ImmutableList.Builder<TxInput> inputs = new ImmutableList.Builder<>();
    for (long i = 0; i < nOfInputs.longValue(); i++) {
      TxInput txInput = TxInput.deserialize(in);
      sizeInBytes += txInput.getSizeInBytes();
      throwIfTooLarge(sizeInBytes);
      inputs.add(txInput);
    }

    // outputs
    BigInteger nOfOutputs = in.readUnsVarInt();
    throwIfNumberOfOutputsIsInvalid(nOfOutputs);
    sizeInBytes += Wire.unsIntVarSizeInBytes(nOfInputs.longValue());
    throwIfTooLarge(sizeInBytes);
    ImmutableList.Builder<TxOutput> outputs = new ImmutableList.Builder<>();
    for (long i = 0; i < nOfOutputs.longValue(); i++) {
      TxOutput txOutput = TxOutput.deserialize(in);
      sizeInBytes += txOutput.getSizeInBytes();
      throwIfTooLarge(sizeInBytes);
      outputs.add(txOutput);
    }

    // lock_time
    long unlockAfter = in.readUnsInt32LE();
    sizeInBytes += 4;
    throwIfTooLarge(sizeInBytes);

    return new Tx(version, inputs.build(), outputs.build(), unlockAfter);
  }

  private static void throwIfUnexpectedVersion(long version) throws ProtocolException {
    if (version < 1 || version > VERSION)
      throw new ProtocolException("Unexpected tx version " + version);
  }

  private static void throwIfNumberOfInputsIsInvalid(BigInteger nOfInputs) throws ProtocolException {
    if (nOfInputs.equals(BigInteger.ZERO))
      throw new ProtocolException("Tx with 0 inputs is illegal");
    if (nOfInputs.compareTo(BigInteger.valueOf(MAX_NUMBER_OF_INPUTS)) > 0)
      throw new TooMany("Too many claimed inputs in tx " + nOfInputs + " > " + MAX_NUMBER_OF_INPUTS);
  }

  private static void throwIfNumberOfOutputsIsInvalid(BigInteger nOfOutputs) throws ProtocolException {
    if (nOfOutputs.equals(BigInteger.ZERO))
      throw new ProtocolException("Tx with 0 outputs is illegal");
    if (nOfOutputs.compareTo(BigInteger.valueOf(MAX_NUMBER_OF_OUTPUTS)) > 0)
      throw new TooMany("Too many claimed outputs in tx " + nOfOutputs + " > " + MAX_NUMBER_OF_OUTPUTS);
  }

  private static void throwIfTooLarge(int sizeInBytes) throws ProtocolException {
    // Based on https://github.com/bitcoin/bitcoin/blob/master/src/main.cpp#L564  # line "if (sz >= MAX_STANDARD_TX_SIZE)"
    if (sizeInBytes >= MAX_SIZE)
      throw new ProtocolException("Tx too large " + sizeInBytes + " > " + MAX_SIZE);
  }

  public long getVersion() {
    return version;
  }

  public ImmutableList<TxInput> getInputs() {
    return inputs;
  }

  public ImmutableList<TxOutput> getOutputs() {
    return outputs;
  }

  public long getUnlockAfter() {
    return unlockAfter;
  }

  public long getSizeInBytes() {
    return 4L +
      Wire.unsIntVarSizeInBytes(inputs.size()) +
      inputs.stream().mapToLong(TxInput::getSizeInBytes).sum() +
      Wire.unsIntVarSizeInBytes(outputs.size()) +
      outputs.stream().mapToLong(TxOutput::getSizeInBytes).sum() +
      4;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tx that = (Tx) o;
    return Objects.equals(this.version, that.version) &&
      Objects.equals(this.inputs, that.inputs) &&
      Objects.equals(this.outputs, that.outputs) &&
      Objects.equals(this.unlockAfter, that.unlockAfter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, inputs, outputs, unlockAfter);
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this)
      .add("version", version)
      .add("inputs", inputs)
      .add("outputs", outputs)
      .add("unlockAfter", unlockAfter)
      .toString();
  }

  public static class TooMany extends ProtocolException {
    public TooMany(String s) {
      super(s);
    }
  }

}
