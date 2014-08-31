package org.bitj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Crypto {

  /**
   * Bitcoin checksum is the first 4 bytes of the double SHA-256.
   * It is used in the network protocol for message payload check-summing.
   */
  public static byte[] bitcoinChecksum(byte[] payload) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] doubleHash = md.digest(md.digest(payload));
      return Arrays.copyOfRange(doubleHash, 0, 4);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);  // cannot happen
    }
  }

  /**
   * Bitcoin hash is double SHA-256.
   */
  public static byte[] bitcoinHash(byte[] payload) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      return md.digest(md.digest(payload));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);  // cannot happen
    }
  }

}
