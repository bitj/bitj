package org.bitj.wire;

import org.bitj.utils.Debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.Objects;
import static com.google.common.base.Objects.toStringHelper;

public class InvItem {

  public static enum Type {
    Error, Transaction, Block;

    public static Type valueOf(long value) throws ProtocolException {
      switch ((int)value) {
        case 0: return Error;
        case 1: return Transaction;
        case 2: return Block;
        default: throw new ProtocolException("Unexpected inv item type " + value);
      }
    }

    public int value() {
      return ordinal();
    }
  }

  public Type type;
  public byte[] hash;

  public InvItem(Type type, byte[] hash32bytesLong) {
    if (hash32bytesLong.length != 32)
      throw new IllegalArgumentException("Hash must have exactly 32 bytes, got " + hash32bytesLong.length);
    this.type = type;
    this.hash = hash32bytesLong;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(30));
    out.writeUnsignedInt32LE(type.value());
    out.write(hash);
    return out.toByteArray();
  }

  public static InvItem deserialize(BitcoinInputStream in) throws IOException {
    Type type = Type.valueOf(in.readUnsignedInt32LE());
    byte[] hash = in.readBytes(32);
    return new InvItem(type, hash);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("type", type)
      .add("hash", Debug.bytesToHex(hash).replaceAll(" ", ""))
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InvItem that = (InvItem) o;
    return Objects.equals(this.type, that.type) && Arrays.equals(this.hash, that.hash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, Arrays.hashCode(hash));
  }

}
