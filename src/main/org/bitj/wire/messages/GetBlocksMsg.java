package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

public class GetBlocksMsg extends Msg {

  // 10 consecutive blocks + log(2, 1024 years * 365 days * 24 hours * 6 blocks) + genesis block
  public static final int MAX_LOCATOR_OBJECT_SIZE = 10 + 26 + 1;

  protected long version = VersionMsg.PROTOCOL_VERSION;
  protected ImmutableList<Sha256Hash> blockLocator;
  protected Sha256Hash stopHash = Sha256Hash.ZERO;

  @Override
  public String name() {
    return "getblocks";
  }

  public GetBlocksMsg(ImmutableList<Sha256Hash> blockLocator) {
    this.blockLocator = blockLocator;
  }

  public GetBlocksMsg(long version, ImmutableList<Sha256Hash> blockLocator, Sha256Hash stopHash) {
    this.version = version;
    this.blockLocator = blockLocator;
    this.stopHash = stopHash;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream());
    out.writeUnsInt32LE(version);
    out.writeUnsVarInt(blockLocator.size());
    for (Sha256Hash hash : blockLocator)
      out.writeSha256HashLE(hash);
    out.writeSha256HashLE(stopHash);
    return out.toByteArray();
  }

  public static GetBlocksMsg deserializePayload(BitcoinInputStream in) throws IOException {
    long version = in.readUnsInt32LE();
    BigInteger length = in.readUnsVarInt();
    if (length.compareTo(BigInteger.valueOf(MAX_LOCATOR_OBJECT_SIZE)) > 0)
      throw new TooMany("Locator object claims to contain " + length + " > " + MAX_LOCATOR_OBJECT_SIZE + " block hashes");
    ImmutableList.Builder<Sha256Hash> blockLocator = new ImmutableList.Builder<>();
    for (long i = 0; i < length.longValue(); i++) {
      Sha256Hash hash = in.readSha256Hash();
      blockLocator.add(hash);
    }
    Sha256Hash stopHash = in.readSha256Hash();
    return new GetBlocksMsg(version, blockLocator.build(), stopHash);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetBlocksMsg that = (GetBlocksMsg) o;
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
