package org.bitj;

public class Debug {

  public static String toString(byte[] bytes) {
    StringBuilder sb = new StringBuilder("[");
    for (byte b : bytes) {
      sb.append(0xff & b);
      sb.append(",");
    }
    sb.replace(sb.length()-1, sb.length(), "]");
    return sb.toString();
  }

  public static byte[] hexToBytes(String s) {
    s = s.replaceAll("[^a-fA-F0-9]", "");
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2)
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
    return data;
  }

  public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for (byte b : bytes)
      sb.append(String.format("%02X ", b));
    return sb.substring(0, sb.length()-1);  // trims trailing space
  }

}
