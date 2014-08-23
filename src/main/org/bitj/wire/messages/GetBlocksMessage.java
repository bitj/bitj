package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

public class GetBlocksMessage extends Message {

  // 10 consecutive blocks + log(2, 1024 years * 365 days * 24 hours * 6 blocks) + genesis block
  public static final int MAX_LOCATOR_OBJECT_SIZE = 10 + 26 + 1;

  private long version = VersionMessage.PROTOCOL_VERSION;
  private ImmutableList<Sha256Hash> blockLocator;
  private Sha256Hash stopHash = Sha256Hash.ZERO;

  @Override
  public String name() {
    return "getblocks";
  }

  public GetBlocksMessage(ImmutableList<Sha256Hash> blockLocator) {
    this.blockLocator = blockLocator;
  }

  public GetBlocksMessage(long version, ImmutableList<Sha256Hash> blockLocator, Sha256Hash stopHash) {
    this.version = version;
    this.blockLocator = blockLocator;
    this.stopHash = stopHash;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeUnsignedInt32LE(version);
    out.writeUnsignedVarInt(blockLocator.size());
    for (Sha256Hash hash : blockLocator)
      out.writeSha256Hash(hash);
    out.writeSha256Hash(stopHash);
    return out.toByteArray();
  }

  public static GetBlocksMessage deserializePayload(BitcoinInputStream in) throws IOException {
    long version = in.readUnsignedInt32LE();
    BigInteger length = in.readUnsignedVarInt();
    if (length.compareTo(BigInteger.valueOf(MAX_LOCATOR_OBJECT_SIZE)) > 0)
      throw new TooMany("Locator object claims to contain " + length + " > " + MAX_LOCATOR_OBJECT_SIZE + " block hashes");
    ImmutableList.Builder<Sha256Hash> blockLocator = new ImmutableList.Builder<>();
    for (long i = 0; i < length.longValue(); i++) {
      Sha256Hash hash = in.readSha256Hash();
      blockLocator.add(hash);
    }
    Sha256Hash stopHash = in.readSha256Hash();
    return new GetBlocksMessage(version, blockLocator.build(), stopHash);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetBlocksMessage that = (GetBlocksMessage) o;
    return Objects.equals(this.version, that.version) &&
      Objects.equals(this.blockLocator, that.blockLocator) &&
      Objects.equals(this.stopHash, that.stopHash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, blockLocator, stopHash);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("version", version)
      .add("blockLocator", blockLocator)
      .add("stopHash", stopHash)
      .toString();
  }

  public static class TooMany extends ProtocolException {
    public TooMany(String s) { super(s); }
  }

}
