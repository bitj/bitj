package org.bitj.wire.objects;

import org.bitj.Amount;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Objects;

public class TxOutput {

  private Amount amount;
  private TxScript script;

  public static final long MIN_SIZE = 9;

  public TxOutput(Amount amount, TxScript script) {
    this.amount = amount;
    this.script = script;
  }

  public TxOutput(long satoshi, TxScript script) {
    this.amount = new Amount(satoshi);
    this.script = script;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(17 + 512));
    out.writeInt64LE(amount.satoshi());
    out.write(script.serialize());
    return out.toByteArray();
  }

  public static TxOutput deserialize(BitcoinInputStream in) throws IOException {
    long satoshi = in.readInt64LE();
    throwIfIllegalValue(satoshi);
    TxScript script = TxScript.deserialize(in, TxScript.Type.OUTPUT);
    return new TxOutput(new Amount(satoshi), script);
  }

  private static void throwIfIllegalValue(long satoshi) throws ProtocolException {
    if (satoshi < 0)
      throw new ProtocolException("Illegal tx output amount " + satoshi + " < 0");
    if (satoshi > Amount.MAX_SATOSHI)
      throw new ProtocolException("Illegal tx output amount " + satoshi + " > " + Amount.MAX_SATOSHI);
  }

  public long getSizeInBytes() {
    return 8 + script.getSizeInBytes();
  }

  public Amount getAmount() {
    return amount;
  }

  public long getSatoshi() {
    return amount.satoshi();
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this)
      .add("amount", amount)
      .add("script", script)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxOutput that = (TxOutput) o;
    return Objects.equals(this.getSatoshi(), that.getSatoshi()) && Objects.equals(this.script, that.script);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSatoshi(), script);
  }

}
