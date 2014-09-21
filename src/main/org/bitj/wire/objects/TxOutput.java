package org.bitj.wire.objects;

import org.bitj.Amount;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TxOutput {

  private Amount amount;
  private TxScript script;

  public static final long MIN_SIZE = 9;

  public TxOutput(Amount amount, TxScript script) {
    checkNotNull(amount);
    checkArgument(amount.isLegalTxOutputValue());
    this.amount = checkNotNull(amount);
    this.script = checkNotNull(script);
  }

  public TxOutput(long satoshi, TxScript script) {
    this.amount = new Amount(satoshi);
    checkArgument(amount.isLegalTxOutputValue());
    this.script = checkNotNull(script);
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(17 + 512));
    out.writeInt64LE(amount.satoshi());
    out.write(script.serialize());
    return out.toByteArray();
  }

  public static TxOutput deserialize(BitcoinInputStream in) throws IOException {
    Amount amount = new Amount(in.readInt64LE());
    throwIfIllegal(amount);
    TxScript script = TxScript.deserialize(in, TxScript.Type.OUTPUT);
    return new TxOutput(amount, script);
  }

  private static void throwIfIllegal(Amount amount) throws ProtocolException {
    if (!amount.isLegalTxOutputValue())
      throw new ProtocolException("Illegal tx output amount " + amount + "BTC");
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
