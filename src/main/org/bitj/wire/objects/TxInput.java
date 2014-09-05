package org.bitj.wire.objects;

import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

public class TxInput {

  private TxOutputPointer prevOutput;
  private TxScript script;
  private long sequence = Wire.MAX_UINT_32; // http://bitcoin.stackexchange.com/questions/2025/what-is-txins-sequence

  public static final long MIN_SIZE = 42; // assuming script length of 1 byte

  public TxInput(TxOutputPointer prevOutput, TxScript script) {
    this.prevOutput = prevOutput;
    this.script = script;
  }

  public TxInput(TxOutputPointer prevOutput, TxScript script, long sequence) {
    this.prevOutput = prevOutput;
    this.script = script;
    this.sequence = sequence;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(1024)); // most are ~512 bytes
    out.write(prevOutput.serialize());
    out.write(script.serialize());
    out.writeUnsInt32LE(sequence);
    return out.toByteArray();
  }

  public static TxInput deserialize(BitcoinInputStream in) throws IOException {
    TxOutputPointer prevOutput = TxOutputPointer.deserialize(in);
    TxScript script = TxScript.deserialize(in, TxScript.Type.INPUT);
    long sequence = in.readUnsInt32LE();
    return new TxInput(prevOutput, script, sequence);
  }

  public long getSizeInBytes() {
    return prevOutput.getSizeInBytes() + script.getSizeInBytes() + 4;
  }

  public long getSequence() {
    return sequence;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("prevOutput", prevOutput)
      .add("script", script)
      .add("sequence", sequence)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxInput that = (TxInput) o;
    return Objects.equals(this.prevOutput, that.prevOutput) && Objects.equals(this.script, that.script) && Objects.equals(this.sequence, that.sequence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prevOutput, script, sequence);
  }

}
