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
 * This message is really a special case of GetBlocksMessage where asker does not want transactions,
 * only the blocks themselves ("headers").
 */
public class GetHeadersMessage extends GetBlocksMessage {

  @Override
  public String name() {
    return "getheaders";
  }

  public GetHeadersMessage(ImmutableList<Sha256Hash> blockLocator) {
    super(blockLocator);
  }

  public GetHeadersMessage(long version, ImmutableList<Sha256Hash> blockLocator, Sha256Hash stopHash) {
    super(version, blockLocator, stopHash);
  }

  public static GetHeadersMessage deserializePayload(BitcoinInputStream in) throws IOException {
    GetBlocksMessage getBlocks = GetBlocksMessage.deserializePayload(in);
    return new GetHeadersMessage(getBlocks.version, getBlocks.blockLocator, getBlocks.stopHash);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetHeadersMessage that = (GetHeadersMessage) o;
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
