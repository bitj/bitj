package org.bitj.wire.objects;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.bitj.Sha256Hash;
import org.bitj.utils.Crypto;
import org.bitj.utils.Utils;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;
import org.bitj.wire.messages.VersionMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;

public class Block {

  /**
   * Based on: https://github.com/bitcoin/bitcoin/blob/master/src/main.h#L37  # line "int MAX_BLOCK_SIZE = 1000000;"
   */
  public static final long MAX_SIZE = 1 * 1000 * 1000; // < 1MB

  /**
   * Based on: https://en.bitcoin.it/wiki/Protocol_specification#block
   */
  public static final int HEADER_SIZE_IN_BYTES = 4 + 32 + 32 + 4 + 4 + 4;

  private long version = VersionMessage.PROTOCOL_VERSION;
  private Sha256Hash prevHash;
  private Sha256Hash mrklRoot;
  private long timestamp = Utils.currentUnixTimestamp();
  private long bits = -1;
  private long nonce = -1;
  private ImmutableList<Tx> txns = new ImmutableList.Builder<Tx>().build();

  private Sha256Hash hash;
  public byte[] mrklTree;  // consecutive 32-byte hashes

  private Block() {}

  public byte[] serialize() throws IOException {
    int sizeInBytes = (int) getSizeInBytes();
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(sizeInBytes));
    serializeHeader(out);
    out.writeUnsignedVarInt(txns.size());
    for (Tx tx : txns)
      out.write(tx.serialize());
    return out.toByteArray();
  }

  private void serializeHeader(BitcoinOutputStream out) throws IOException {
    out.writeUnsignedInt32LE(version);
    out.writeSha256HashLE(prevHash);
    out.writeSha256HashLE(mrklRoot);
    out.writeUnsignedInt32LE(timestamp);
    out.writeUnsignedInt32LE(bits);
    out.writeUnsignedInt32LE(nonce);
  }

  public static Block deserialize(BitcoinInputStream in) throws IOException {
    long version = in.readUnsignedInt32LE();
    Sha256Hash prevHash = in.readSha256Hash();
    Sha256Hash mrklRoot = in.readSha256Hash();
    long timestamp = in.readUnsignedInt32LE();
    long bits = in.readUnsignedInt32LE();
    long nonce = in.readUnsignedInt32LE();
    BigInteger numberOfTx = in.readUnsignedVarInt();
    long size = HEADER_SIZE_IN_BYTES + Wire.unsignedIntVarSizeInBytes(numberOfTx.longValue()); // about 81 or 83, theoretically up to 85
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
      bits(bits).
      nonce(nonce).
      txns(txns.build()).
      get();
  }

  public long getVersion() { return version; }

  public Sha256Hash getPrevHash() { return prevHash; }

  public Sha256Hash getMrklRoot() { return mrklRoot; }

  public long getTimestamp() { return timestamp; }

  public long getBits() { return bits; }

  public long getNonce() { return nonce; }

  public ImmutableList<Tx> getTxns() { return txns; }

  public Sha256Hash getHash() {
    if (hash == null) {
      BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(HEADER_SIZE_IN_BYTES));
      try {
        serializeHeader(out);
      } catch (IOException e) {
        throw new RuntimeException(e); // cannot happen (in-memory stream)
      }
      byte[] serializedHeader = out.toByteArray();
      byte[] hashBytes = Crypto.bitcoinHash(serializedHeader);
      Wire.reverseBytesInPlace(hashBytes);
      hash = new Sha256Hash(hashBytes);
    }
    return hash;
  }

  /**
   * Returns block's serialization size in bytes.
   */
  public long getSizeInBytes() {
    return HEADER_SIZE_IN_BYTES +
      Wire.unsignedIntVarSizeInBytes(txns.size()) +
      txns.stream().mapToLong(Tx::getSizeInBytes).sum();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Block that = (Block) o;
    return Objects.equal(this.getHash(), that.getHash());
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("version", version)
      .add("prevHash", prevHash)
      .add("mrklRoot", mrklRoot)
      .add("timestamp", timestamp)
      .add("bits", bits)
      .add("nonce", nonce)
      .add("txns", txns)
      .add("hash", getHash())
      .toString();
  }

  public static class Builder {
    private Block block = new Block();

    public Builder() {}

    public Block get() {
      if (block.prevHash == null || block.mrklRoot == null || block.bits == -1 || block.nonce == -1)
        throw new IllegalArgumentException("Set all obligatory attributes");
      return block;
    }

    public Builder version(long version) { block.version = version; return this; }
    public Builder prevHash(Sha256Hash prevHash) { block.prevHash = prevHash; return this; }
    public Builder mrklRoot(Sha256Hash mrklRoot) { block.mrklRoot = mrklRoot; return this; }
    public Builder timestamp(long timestamp) { block.timestamp = timestamp; return this; }
    public Builder bits(long bits) { block.bits = bits; return this; }
    public Builder nonce(long nonce) { block.nonce = nonce; return this; }
    public Builder txns(ImmutableList<Tx> txns) { block.txns = txns; return this; }
    public Builder hash(Sha256Hash hash) { block.hash = hash; return this; }
  }

}
