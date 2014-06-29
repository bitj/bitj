package org.bitj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Crypto {

  public static byte[] checksum(byte[] payload) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] doubleHash = md.digest(md.digest(payload));
      return Arrays.copyOfRange(doubleHash, 0, 4);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);  // cannot happen
    }
  }

}
