package org.bitj.wire;

import org.bitj.Sha256Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Objects;
import static com.google.common.base.Objects.toStringHelper;

public class InvItem {

  public Type type;
  public Sha256Hash hash;

  public InvItem(Type type, Sha256Hash hash) {
    this.type = type;
    this.hash = hash;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(30));
    out.writeUnsignedInt32LE(type.value());
    out.writeSha256Hash(hash);
    return out.toByteArray();
  }

  public static InvItem deserialize(BitcoinInputStream in) throws IOException {
    Type type = Type.valueOf(in.readUnsignedInt32LE());
    Sha256Hash hash = in.readSha256Hash();
    return new InvItem(type, hash);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("type", type)
      .add("hash", hash)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InvItem that = (InvItem) o;
    return Objects.equals(this.type, that.type) && Objects.equals(this.hash, that.hash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, hash);
  }

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

}
