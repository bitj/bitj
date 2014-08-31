package org.bitj.wire.messages;

import com.google.common.collect.ImmutableSet;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.objects.PeerAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.Objects;

import static com.google.common.base.Objects.ToStringHelper;
import static com.google.common.base.Objects.toStringHelper;

public class AddrMessage extends Message {

  private ImmutableSet<PeerAddress> peers;

  @Override
  public String name() {
    return "addr";
  }

  public AddrMessage(ImmutableSet<PeerAddress> peers) {
    this.peers = peers;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(9 + peers.size() * 30));
    out.writeUnsignedVarInt(peers.size());
    for (PeerAddress peerAddress : peers)
      out.write(peerAddress.serialize());
    return out.toByteArray();
  }

  public static AddrMessage deserializePayload(BitcoinInputStream in) throws IOException {
    BigInteger count = in.readUnsignedVarInt();
    if (count.compareTo(MAX_ADDRESSES) > 0)
      throw new TooMany("Peer sent " + count + " > " + MAX_ADDRESSES + " addresses");
    ImmutableSet.Builder<PeerAddress> builder = new ImmutableSet.Builder<PeerAddress>();
    for (long i = 0; i < count.longValue(); i++)
      builder.add(PeerAddress.deserialize(in));
    return new AddrMessage(builder.build());
  }

  @Override
  public String toString() {
    ToStringHelper helper = toStringHelper(this);
    peers.forEach(peerAddress -> helper.add("net_addr", peerAddress));
    return helper.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AddrMessage that = (AddrMessage) o;
    return Objects.equals(this.peers, that.peers);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(peers);
  }

  public ImmutableSet<PeerAddress> getPeers() {
    return peers;
  }

  // TODO: check what should be the actual max
  private final static BigInteger MAX_ADDRESSES = BigInteger.valueOf(1000);

  public static class TooMany extends ProtocolException {
    public TooMany(String s) {
      super(s);
    }
  }

}
