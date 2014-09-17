package org.bitj.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

  public static long currentUnixTimestamp() {
    return System.currentTimeMillis() / 1000L;
  }

  public static byte[] getBytesUTF8(String s) {
    try {
      return s.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // cannot happen
    }
  }

  public static byte[] getBytesASCII(String s) {
    try {
      return s.getBytes("ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // cannot happen
    }
  }

  public static ThreadLocalRandom weakRNG() {
    return ThreadLocalRandom.current();
  }

  /**
   * Returns PoW difficulty (bdiff) for the target specified in a compact form known as "bits" in Bitcoin Core.
   */
  public static BigDecimal getDifficulty(long compactTarget) {
    // TODO: this is about three orders of magnitude away from overflowing
    // TODO: optimize, see https://en.bitcoin.it/wiki/Difficulty
    double maxbody = Math.log(0x00ffff);
    double scaland = Math.log(256);
    double diff = Math.exp(maxbody - Math.log(compactTarget & 0x00ffffff) + scaland * (0x1d - ((compactTarget & 0xff000000) >> 24)));
    return BigDecimal.valueOf(diff);
  }

  /**
   * Converts target from compact representation ("bits") to large integer represented as 32 bytes array.
   */
  public static byte[] getTargetAs32Bytes(long compactTarget) {
    return padLeft(getTarget(compactTarget).toByteArray(), 32);
  }

  /**
   * Converts target from compact representation ("bits") to large integer.
   */
  public static BigInteger getTarget(long compactTarget) {
    // TODO: optimize
    long last3bytes = compactTarget & 0x00_00_00_00_00_FF_FF_FFL;
    long firstByte  = (compactTarget & 0x00_00_00_00_FF_00_00_00L) >> 24;
    BigInteger last3bytesBI = BigInteger.valueOf(last3bytes);
    BigInteger firstByteBI = BigInteger.valueOf(firstByte);
    BigInteger exp = BigInteger.valueOf(8).multiply(firstByteBI.subtract(BigInteger.valueOf(3)));
    BigInteger coe = BigInteger.valueOf(2).pow(exp.intValue());
    return last3bytesBI.multiply(coe);
  }

  public static byte[] padLeft(byte[] bytes, int size) {
    if (bytes.length > size)
      throw new IllegalArgumentException("Padded size cannot be smaller than input array length.");
    if (bytes.length == size)
      return bytes;
    byte[] padded = new byte[size];
    int since = size - bytes.length;
    // TODO: use stdlib to copy?
    for (int i = 0; i < bytes.length; i++)
      padded[since + i] = bytes[i];
    return padded;
  }

}
