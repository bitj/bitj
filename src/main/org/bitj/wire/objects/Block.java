package org.bitj.wire.objects;

import com.google.common.collect.ImmutableList;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bitj.utils.Utils.MAX_UINT_32;

import org.bitj.Sha256Hash;
import org.bitj.utils.Crypto;
import org.bitj.utils.Utils;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.Objects;

public class Block {

  /**
   * Based on: https://github.com/bitcoin/bitcoin/blob/master/src/main.h#L37  # line "int MAX_BLOCK_SIZE = 1000000;"
   */
  public static final long MAX_SIZE = 1 * 1000 * 1000; // < 1MB

  /**
   * Based on: https://en.bitcoin.it/wiki/Protocol_specification#block
   */
  public static final int HEADER_SIZE_IN_BYTES = 4 + 32 + 32 + 4 + 4 + 4;

  public static final int SUPPORTED_VERSION = 2;

  // serialized
  private long version = SUPPORTED_VERSION;
  private Sha256Hash prevHash;
  private Sha256Hash mrklRoot;
  private long timestamp = Utils.currentUnixTimestamp();
  private long compactTarget = -1;
  private long nonce = -1;
  private ImmutableList<Tx> txns = new ImmutableList.Builder<Tx>().build();

  private Sha256Hash cachedHash;

  private Block() {}

  public byte[] serialize() throws IOException {
    int sizeInBytes = (int) getSizeInBytes();
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(sizeInBytes));
    serializeHeader(out);
    out.writeUnsVarInt(txns.size());
    for (Tx tx : txns)
      out.write(tx.serialize());
    return out.toByteArray();
  }

  public void serializeHeader(BitcoinOutputStream out) throws IOException {
    out.writeUnsInt32LE(version);
    out.writeSha256HashLE(prevHash);
    out.writeSha256HashLE(mrklRoot);
    out.writeUnsInt32LE(timestamp);
    out.writeUnsInt32LE(compactTarget);
    out.writeUnsInt32LE(nonce);
  }

  public static Block deserialize(BitcoinInputStream in) throws IOException {
    long version = in.readUnsInt32LE();
    Sha256Hash prevHash = in.readSha256HashLE();
    Sha256Hash mrklRoot = in.readSha256HashLE();
    long timestamp = in.readUnsInt32LE();
    long compactTarget = in.readUnsInt32LE();
    long nonce = in.readUnsInt32LE();
    BigInteger numberOfTx = in.readUnsVarInt();
    long size = HEADER_SIZE_IN_BYTES + Wire.unsIntVarSizeInBytes(numberOfTx.longValue()); // about 81 or 83, theoretically up to 85
    ImmutableList.Builder<Tx> txns = new ImmutableList.Builder<>();
    for (long i = 0; i < numberOfTx.longValue(); i++) {
      Tx tx = Tx.deserialize(in);
      txns.add(tx);
      size += tx.getSizeInBytes();
      if (size > MAX_SIZE) // Based on https://github.com/bitcoin/bitcoin/blob/master/src/main.cpp#L2247   # line "GetSerializeSize(block, SER_NETWORK, PROTOCOL_VERSION) > MAX_BLOCK_SIZE"
        throw new ProtocolException("Block too large " + size + " > " + MAX_SIZE);
    }
    return new Builder().
      version(version).
      prevHash(prevHash).
      mrklRoot(mrklRoot).
      timestamp(timestamp).
      compactTarget(compactTarget).
      nonce(nonce).
      txns(txns.build()).
      get();
  }

  public long getVersion() { return version; }

  public Sha256Hash getPrevHash() { return prevHash; }

  public Sha256Hash getMrklRoot() { return mrklRoot; }

  public long getUnixTimestamp() { return timestamp; }

  public long getCompactTarget() { return compactTarget; }

  public long getNonce() { return nonce; }

  public ImmutableList<Tx> getTxns() { return txns; }

  public Sha256Hash getHash() {
    if (cachedHash == null) {
      BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(HEADER_SIZE_IN_BYTES));
      try {
        serializeHeader(out);
      } catch (IOException e) {
        throw new RuntimeException(e); // cannot happen (in-memory stream)
      }
      byte[] serializedHeader = out.toByteArray();
      byte[] hashBytes = Crypto.bitcoinHash(serializedHeader);
      Wire.reverseBytesInPlace(hashBytes);
      cachedHash = new Sha256Hash(hashBytes);
    }
    return cachedHash;
  }

  /**
   * Returns block's serialization size in bytes.
   */
  public long getSizeInBytes() {
    return HEADER_SIZE_IN_BYTES +
      Wire.unsIntVarSizeInBytes(txns.size()) +
      txns.stream().mapToLong(Tx::getSizeInBytes).sum();
  }

  public BigDecimal getDifficulty() {
    return Utils.getDifficulty(compactTarget);
  }

  public BigInteger getTarget() {
    return Utils.getTarget(compactTarget);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("version", version)
      .add("prevHash", prevHash)
      .add("mrklRoot", mrklRoot)
      .add("timestamp", timestamp)
      .add("compactTarget", compactTarget)
      .add("nonce", nonce)
      .add("txns", txns)
      .add("cachedHash", getHash())
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Block that = (Block) o;
    return Objects.equals(this.timestamp, that.timestamp) &&
      Objects.equals(this.version, that.version) &&
      Objects.equals(this.prevHash, that.prevHash) &&
      Objects.equals(this.mrklRoot, that.mrklRoot) &&
      Objects.equals(this.compactTarget, that.compactTarget) &&
      Objects.equals(this.nonce, that.nonce);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, version, prevHash, mrklRoot, compactTarget, nonce);
  }

  public static class Builder {
    private Block block = new Block();

    public Builder() {}

    public Block get() {
      if (block.prevHash == null || block.mrklRoot == null || block.compactTarget == -1 || block.nonce == -1)
        throw new IllegalArgumentException("Set all obligatory attributes");
      return block;
    }

    public Builder version(long version) {
      checkArgument(version >= 0 && version <= MAX_UINT_32);
      block.version = version;
      return this;
    }
    public Builder prevHash(Sha256Hash prevHash) { block.prevHash = checkNotNull(prevHash); return this; }
    public Builder mrklRoot(Sha256Hash mrklRoot) { block.mrklRoot = checkNotNull(mrklRoot); return this; }
    public Builder timestamp(long timestamp) {
      checkArgument(timestamp >= 0 && timestamp <= MAX_UINT_32);
      block.timestamp = timestamp;
      return this;
    }
    public Builder compactTarget(long bits) {
      checkArgument(bits >= 0 && bits <= MAX_UINT_32, "Bits cannot be " + bits);
      block.compactTarget = bits;
      return this;
    }
    public Builder nonce(long nonce) { block.nonce = nonce; return this; }
    public Builder txns(ImmutableList<Tx> txns) { block.txns = checkNotNull(txns); return this; }
    public Builder hash(Sha256Hash hash) { block.cachedHash = hash; return this; }
  }

}
