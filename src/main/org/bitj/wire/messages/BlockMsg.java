package org.bitj.wire.messages;

import org.bitj.wire.objects.Block;
import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

import static com.google.common.base.Objects.toStringHelper;

public class BlockMsg extends Msg {

  private Block block;

  @Override
  public String name() {
    return "block";
  }

  public BlockMsg(Block block) {
    this.block = block;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return block.serialize();
  }

  public static BlockMsg deserializePayload(BitcoinInputStream in) throws IOException {
    return new BlockMsg(Block.deserialize(in));
  }

  public Block getBlock() { return block; }

  @Override
  public String toString() {
    return block.toString().replace("Block{", "BlockMsg{");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockMsg that = (BlockMsg) o;
    return this.block.equals(that.block);
  }

  @Override
  public int hashCode() {
    return block.hashCode();
  }

}
