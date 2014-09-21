package org.bitj.wire.objects;

import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;
import org.bitj.wire.Wire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TxScript {

  /**
   * Based on:
   * https://github.com/bitcoin/bitcoin/blob/master/src/script.cpp#L312  # line "if (script.size() > 10000)"
   * https://bitcointalk.org/index.php?topic=263750.0
   */
  public static final long MAX_SIZE = 10000;

  /**
   * Based on:
   * https://github.com/bitcoin/bitcoin/blob/master/src/script.h#L26  # line "static const unsigned int MAX_SCRIPT_ELEMENT_SIZE = 520; // bytes"
   * https://github.com/bitcoin/bitcoin/blob/master/src/main.cpp#L572 # line "remember the 520 byte limit on redeemScript size"
   * https://bitcointalk.org/index.php?topic=615250.0
   */
  public static final long MAX_SIZE_IN_INPUT = 520;

  private byte[] bytes;

  public TxScript(byte[] bytes) {
    checkNotNull(bytes);
    checkArgument(bytes.length <= MAX_SIZE);
    this.bytes = bytes;
  }

  public byte[] serialize() throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(new ByteArrayOutputStream(128)); // TODO: how long is script typically?
    out.writeUnsVarInt(bytes.length);
    out.write(bytes);
    return out.toByteArray();
  }

  public static TxScript deserialize(BitcoinInputStream in, Type type) throws IOException {
    BigInteger scriptSize = in.readUnsVarInt();
    throwIfScriptSizeIsInvalid(scriptSize, type);
    byte[] bytes = in.readBytes(scriptSize.intValue());
    return new TxScript(bytes);
  }

  private static void throwIfScriptSizeIsInvalid(BigInteger scriptSize, Type type) throws ProtocolException {
    if (scriptSize.equals(BigInteger.ZERO))
      throw new ProtocolException("Tx script of 0 bytes is illegal");
    BigInteger maxSize = (type == Type.INPUT  ?  BigInteger.valueOf(MAX_SIZE_IN_INPUT)  :  BigInteger.valueOf(MAX_SIZE));
    if (scriptSize.compareTo(maxSize) > 0)
      throw new ProtocolException("Claimed tx script size too large " + scriptSize + " > " + MAX_SIZE);
  }

  public byte[] getBytes() {
    return bytes;
  }

  public long getSizeInBytes() {
    return Wire.unsIntVarSizeInBytes(bytes.length) + bytes.length;
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this)
      .add("bytes", Debug.bytesToHex(bytes))
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxScript that = (TxScript) o;
    return Arrays.equals(this.bytes, that.bytes);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  public static enum Type {
    INPUT, OUTPUT
  }

}
