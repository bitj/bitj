package org.bitj.wire.messages;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;
import org.bitj.wire.objects.Block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.List;

public class HeadersMsg extends Msg {

  public static final int MAX_HEADERS = 2000;
  private static final int HEADER_SIZE = 81;     // this is only valid in context of the 'headers' message

  private ImmutableList<Block> blocks;

  @Override
  public String name() {
    return "headers";
  }

  public HeadersMsg(ImmutableList<Block> blocks) {
    this.blocks = blocks;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    int sizeInBytes = (int) getSizeInBytes();
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(sizeInBytes));
    out.writeUnsVarInt(blocks.size());
    for (Block block : blocks) {
      block.serializeHeader(out);
      out.writeUnsVarInt(0); // always 0 transactions in header
    }
    return out.toByteArray();
  }

  public static HeadersMsg deserializePayload(BitcoinInputStream in) throws IOException {
    BigInteger count = in.readUnsVarInt();
    throwIfToMany(count);
    ImmutableList.Builder<Block> blocks = new ImmutableList.Builder<>();
    for (long i = 0; i < count.longValue(); i++) {
      Block block = Block.deserialize(in);
      blocks.add(block);
    }
    return new HeadersMsg(blocks.build());
  }

  private static void throwIfToMany(BigInteger headers) throws HeadersMsg.TooMany {
    if (headers.compareTo(BigInteger.valueOf(MAX_HEADERS)) > 0)
      throw new HeadersMsg.TooMany("Headers message claims to contain " + headers + " > 2000 headers");
  }

  public List<Block> getBlocks() { return blocks; }

  public long getSizeInBytes() {
    return Wire.unsIntVarSizeInBytes(blocks.size()) /*count*/ + blocks.size() * HEADER_SIZE /*headers*/;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("blocks", blocks).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeadersMsg that = (HeadersMsg) o;
    return this.blocks.equals(that.blocks);
  }

  @Override
  public int hashCode() {
    return blocks.hashCode();
  }

  public static class TooMany extends ProtocolException {
    public TooMany(String s) { super(s); }
  }

}
