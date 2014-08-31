package org.bitj;

import org.bitj.utils.Debug;

import java.math.BigInteger;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Wraps byte[32] to make equals and hashcode work correctly. Also, ensures byte array length.
 */
public class Sha256Hash implements Comparable<Sha256Hash> {

  public static final Sha256Hash ZERO = new Sha256Hash(new byte[32]);
  public static final Sha256Hash ONE = new Sha256Hash(Debug.hexToBytes("FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF"));

  private byte[] bytes;

  public Sha256Hash(byte[] bytes) {
    checkArgument(bytes.length == 32);
    this.bytes = bytes;
  }

  public Sha256Hash(String hex) {
    checkArgument(hex.replaceAll("\\s", "").length() == 64);
    this.bytes = Debug.hexToBytes(hex);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Sha256Hash that = (Sha256Hash) o;
    return Arrays.equals(this.bytes, that.bytes);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  @Override
  public String toString() {
    return Debug.bytesToHex(bytes).replace(" ", "");
  }

  public BigInteger toBigInteger() {
    return new BigInteger(1, bytes);
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public int compareTo(Sha256Hash that) {
    int thisCode = this.hashCode();
    int thatCode = that.hashCode();
    return thisCode > thatCode ? 1 : (thisCode == thatCode ? 0 : -1);
  }

}
