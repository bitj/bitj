package org.bitj.wire.objects;

import org.bitj.Amount;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Objects;

public class TxOutput {

  private long value;
  private TxScript script;

  public static final long MIN_SIZE = 9;

  public TxOutput(long value, TxScript script) {
    this.value = value;
    this.script = script;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(17 + 512));
    out.writeInt64LE(value);
    out.write(script.serialize());
    return out.toByteArray();
  }

  public static TxOutput deserialize(BitcoinInputStream in) throws IOException {
    long satoshi = in.readInt64LE();
    throwIfIllegalValue(satoshi);
    TxScript script = TxScript.deserialize(in, TxScript.Type.OUTPUT);
    return new TxOutput(satoshi, script);
  }

  private static void throwIfIllegalValue(long satoshi) throws ProtocolException {
    if (satoshi < 0)
      throw new ProtocolException("Illegal tx output value " + satoshi + " < 0");
    if (satoshi > Amount.MAX_SATOSHI)
      throw new ProtocolException("Illegal tx output value " + satoshi + " > " + Amount.MAX_SATOSHI);
  }

  public long getSizeInBytes() {
    return 8 + script.getSizeInBytes();
  }

  public long getValue() {
    return value;
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this)
      .add("value", value)
      .add("script", script)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxOutput that = (TxOutput) o;
    return Objects.equals(this.value, that.value) && Objects.equals(this.script, that.script);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, script);
  }

}
