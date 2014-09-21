package org.bitj.wire.messages;

import com.google.common.collect.ImmutableSet;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.objects.InvItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.Objects;

import static com.google.common.base.Objects.ToStringHelper;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class GetDataMsg extends Msg {

  private ImmutableSet<InvItem> invItems;

  @Override
  public String name() {
    return "getdata";
  }

  public GetDataMsg(ImmutableSet<InvItem> invItems) {
    this.invItems = checkNotNull(invItems);
  }

  @Override
  public byte[] serializePayload() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(9 + invItems.size() * 36));
    out.writeUnsVarInt(invItems.size());
    for (InvItem invItem : invItems)
      out.write(invItem.serialize());
    return out.toByteArray();
  }

  public static GetDataMsg deserializePayload(BitcoinInputStream in) throws IOException {
    BigInteger count = in.readUnsVarInt();
    if (count.compareTo(MAX_INV_ITEMS) > 0)
      throw new TooMany("Peer sent " + count + " > " + MAX_INV_ITEMS + " inv items");
    ImmutableSet.Builder<InvItem> builder = new ImmutableSet.Builder<InvItem>();
    for (long i = 0; i < count.longValue(); i++)
      builder.add(InvItem.deserialize(in));
    return new GetDataMsg(builder.build());
  }

  @Override
  public String toString() {
    ToStringHelper helper = toStringHelper(this);
    for (InvItem invItem : invItems)
      helper.add("inv_item", invItem);
    return helper.toString();
  }

  public ImmutableSet<InvItem> getInvItems() {
    return invItems;
  }

  @Override
  public int hashCode() {
    return Objects.hash(invItems);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetDataMsg that = (GetDataMsg) o;
    return Objects.equals(this.invItems, that.invItems);
  }

  // TODO: check what should be the actual max
  private final static BigInteger MAX_INV_ITEMS = BigInteger.valueOf(50000);

  public static class TooMany extends ProtocolException {
    public TooMany(String s) {
      super(s);
    }
  }

}
