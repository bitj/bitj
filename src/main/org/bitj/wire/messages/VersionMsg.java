package org.bitj.wire.messages;

import com.google.common.io.BaseEncoding;
import org.bitj.utils.Utils;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Date;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bitj.utils.Utils.MAX_UINT_32;

public class VersionMsg extends Msg {

  public static final int PROTOCOL_VERSION = 70002;
  public static final int MIN_PROTOCOL_VERSION = 31900;

  // Identifies protocol version being used by the node
  private int version = PROTOCOL_VERSION;

  // Bitfield of features to be enabled for this connection. Not meant to be interpreted as a number.
  private long services = 1;

  // Standard UNIX timestamp in seconds
  private long timestamp = Utils.currentUnixTimestamp();

  // Node random nonce, randomly generated every time a version packet is sent. This nonce is used to detect connections to self
  private long nonce = Utils.weakRNG().nextLong(Long.MAX_VALUE);

  public static final String USER_AGENT_NAME = "bitj";
  public static final String USER_AGENT_VERSION = "0.1-SNAPSHOT";

  private String userAgent = "/" + USER_AGENT_NAME + "/" + USER_AGENT_VERSION + "/";

  private int startHeight = 0;

  // Set to false when using bloom filtering
  private boolean relayToMeAllTransactions = true;

  private static final byte[] LOCALHOST_NET_ADDR = BaseEncoding.base16().decode("000000000000000000000000000000000000FFFF7F000001208D");

  @Override
  public String name() {
    return "version";
  }

  @Override
  public byte[] serializePayload() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(192));
    out.writeInt32LE(version);
    out.writeUnsInt64LE(services);
    out.writeUnsInt64LE(timestamp);
    out.write(LOCALHOST_NET_ADDR);
    out.write(LOCALHOST_NET_ADDR);
    out.writeUnsInt64LE(nonce);
    out.writeVarString(userAgent);
    out.writeInt32LE(startHeight);
    if (version >= 70001)
      out.write(relayToMeAllTransactions ? 1 : 0);
    return out.toByteArray();
  }

  public static VersionMsg deserializePayload(BitcoinInputStream in) throws IOException {
    int version = in.readInt32LE();
    throwIfPeerVersionTooOld(version);
    long services = in.readInt64LE();
    long timestamp = in.readInt64LE();
    in.skipFully(52);
    long nonce = in.readInt64LE();
    String userAgent = in.readVarString(MAX_STRING_LENGTH);
    int startHeight = in.readInt32LE();
    boolean relay = true;
    if (version >= 70001)
      relay = in.read() > 0;
    return new Builder()
      .version(version)
      .services(services)
      .timestamp(timestamp)
      .nonce(nonce)
      .replaceUserAgent(userAgent)
      .startHeight(startHeight)
      .relayToMeAllTransactions(relay)
      .get();
  }

  private static void throwIfPeerVersionTooOld(int version) throws Incompatible {
    if (version < MIN_PROTOCOL_VERSION)
      throw new VersionMsg.Incompatible("Peer protocol version is " + version + " < " + MIN_PROTOCOL_VERSION);
  }

  public int getVersion() {
    return version;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getNonce() {
    return nonce;
  }

  public int getStartHeight() {
    return startHeight;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public boolean isRelayToMeAllTransactions() {
    return relayToMeAllTransactions;
  }

  @Override
  public String toString() {
    return toStringHelper(this.getClass())
      .add("version", version)
      .add("services", services)
      .add("timestamp", new Date(timestamp * 1000))
      .add("nonce", nonce)
      .add("userAgent", userAgent)
      .add("startHeight", startHeight)
      .add("relay", relayToMeAllTransactions)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VersionMsg that = (VersionMsg) o;
    return Objects.equals(this.version, that.version) &&
      Objects.equals(this.services, that.services) &&
      Objects.equals(this.timestamp, that.timestamp) &&
      Objects.equals(this.nonce, that.nonce) &&
      Objects.equals(this.userAgent, that.userAgent) &&
      Objects.equals(this.startHeight, that.startHeight) &&
      Objects.equals(this.relayToMeAllTransactions, that.relayToMeAllTransactions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, services, timestamp, nonce, userAgent, startHeight, relayToMeAllTransactions);
  }

  public static class Builder {
    private VersionMsg versionMessage = new VersionMsg();

    public Builder() {}

    public VersionMsg get() { return versionMessage; }

    public Builder version(int version) {
      checkArgument(version >= 0 && version <= MAX_UINT_32);  // version int is likely to be changed to uint in bitcoin core for consistency
      versionMessage.version = version;
      return this;
    }
    public Builder services(long services) { versionMessage.services = services; return this; }
    public Builder timestamp(long timestamp) {
      checkArgument(timestamp >= 0);
      versionMessage.timestamp = timestamp;
      return this;
    }
    public Builder nonce(long nonce) { versionMessage.nonce = nonce; return this; }
    public Builder replaceUserAgent(String userAgent) { versionMessage.userAgent = checkNotNull(userAgent); return this; }
    public Builder userAgent(String postfix) { versionMessage.userAgent += "/" + checkNotNull(postfix); return this; }
    public Builder startHeight(int startHeight) {
      checkArgument(startHeight >= 0);
      versionMessage.startHeight = startHeight;
      return this;
    }
    public Builder relayToMeAllTransactions(boolean relay) { versionMessage.relayToMeAllTransactions = relay; return this; }
  }

  public static class Incompatible extends ProtocolException {
    public Incompatible(String message) {
      super(message);
    }
  }

}
