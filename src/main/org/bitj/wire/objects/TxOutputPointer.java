package org.bitj.wire.objects;

import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Used in transaction inputs to reference the previous transaction output.
 * Known as TxOutPoint in Bitcoin Core.
 */
public class TxOutputPointer {

  private Sha256Hash txHash;
  private long outputIndex;

  public TxOutputPointer(Sha256Hash txHash, long outputIndex) {
    this.txHash = txHash;
    this.outputIndex = outputIndex;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(36));
    out.writeSha256HashLE(txHash);
    out.writeUnsInt32LE(outputIndex);
    return out.toByteArray();
  }

  public static TxOutputPointer deserialize(BitcoinInputStream in) throws IOException {
    Sha256Hash txHash = in.readSha256Hash();
    long outputIndex = in.readUnsInt32LE();
    return new TxOutputPointer(txHash, outputIndex);
  }

  public long getSizeInBytes() {
    return 36;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("txHash", txHash)
      .add("outputIndex", outputIndex)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxOutputPointer that = (TxOutputPointer) o;
    return Objects.equals(this.txHash, that.txHash) && Objects.equals(this.outputIndex, that.outputIndex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(txHash, outputIndex);
  }

}
