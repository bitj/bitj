package org.bitj.utils;

import java.io.UnsupportedEncodingException;
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

}
