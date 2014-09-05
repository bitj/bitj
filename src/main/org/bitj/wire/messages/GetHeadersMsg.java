package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.Sha256Hash;
import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Asks for the block headers.
 *
 * This message is really a special case of GetBlocksMsg where asker does not want transactions,
 * only the blocks themselves ("headers").
 */
public class GetHeadersMsg extends GetBlocksMsg {

  @Override
  public String name() {
    return "getheaders";
  }

  public GetHeadersMsg(ImmutableList<Sha256Hash> blockLocator) {
    super(blockLocator);
  }

  public GetHeadersMsg(long version, ImmutableList<Sha256Hash> blockLocator, Sha256Hash stopHash) {
    super(version, blockLocator, stopHash);
  }

  public static GetHeadersMsg deserializePayload(BitcoinInputStream in) throws IOException {
    GetBlocksMsg getBlocks = GetBlocksMsg.deserializePayload(in);
    return new GetHeadersMsg(getBlocks.version, getBlocks.blockLocator, getBlocks.stopHash);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetHeadersMsg that = (GetHeadersMsg) o;
    return Objects.equals(this.version, that.version) &&
      Objects.equals(this.blockLocator, that.blockLocator) &&
      Objects.equals(this.stopHash, that.stopHash);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("version", version)
      .add("blockLocator", blockLocator)
      .add("stopHash", stopHash)
      .toString();
  }

}
