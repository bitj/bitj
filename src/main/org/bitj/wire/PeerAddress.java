package org.bitj.wire;

import com.google.common.base.Objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class PeerAddress {

  private long timestampOfTheLastMessage;
  private long services;
  private InetAddress ip;
  private int port = 0;

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(30));
    out.writeUnsignedInt32LE(timestampOfTheLastMessage);
    out.writeUnsignedInt64LE(services);
    out.writeIP(ip);
    out.writeUnsignedInt16BE(port);
    return out.toByteArray();
  }

  public static PeerAddress deserialize(BitcoinInputStream in) throws IOException {
    PeerAddress addr = new PeerAddress();
    addr.timestampOfTheLastMessage = in.readUnsignedInt32LE();
    addr.services = in.readInt64LE();
    addr.ip = in.readIP();
    addr.port = in.readUnsignedInt16BE();
    return addr;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("ip", ip)
      .add("port", port)
      .add("timestampOfTheLastMessage", timestampOfTheLastMessage)
      .add("services", services)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PeerAddress that = (PeerAddress) o;
    return Objects.equal(this.ip, that.ip) &&
      Objects.equal(this.timestampOfTheLastMessage, that.timestampOfTheLastMessage) &&
      Objects.equal(this.services, that.services) &&
      Objects.equal(this.port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(ip, timestampOfTheLastMessage, services, port);
  }

  public long getTimestampOfTheLastMessage() {
    return timestampOfTheLastMessage;
  }

  public long getServices() {
    return services;
  }

  public InetAddress getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public void setTimestampOfTheLastMessage(long timestampOfTheLastMessage) {
    this.timestampOfTheLastMessage = timestampOfTheLastMessage;
  }

  public static class Builder {
    private PeerAddress peerAddress = new PeerAddress();

    public Builder() {}

    public PeerAddress get() { return peerAddress; }

    public Builder timestampOfTheLastMessage(long timestamp) { peerAddress.timestampOfTheLastMessage = timestamp; return this; }
    public Builder services(long services) { peerAddress.services = services; return this; }
    public Builder ip(InetAddress ip) { peerAddress.ip = ip; return this; }
    public Builder port(int port) { peerAddress.port = port; return this; }
  }

}
