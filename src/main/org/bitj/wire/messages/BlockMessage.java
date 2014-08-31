package org.bitj.wire.messages;

import org.bitj.wire.objects.Block;
import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Date;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

public class BlockMessage extends Message {

  private Block block;

  @Override
  public String name() {
    return "block";
  }

  public BlockMessage(Block block) {
    this.block = block;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return block.serialize();
  }

  public static BlockMessage deserializePayload(BitcoinInputStream in) throws IOException {
    return new BlockMessage(Block.deserialize(in));
  }

  public Block getBlock() { return block; }

  @Override
  public String toString() {
    return block.toString().replace("Block{", "BlockMessage{");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockMessage that = (BlockMessage) o;
    return this.block.equals(that.block);
  }

  @Override
  public int hashCode() {
    return block.hashCode();
  }

}
