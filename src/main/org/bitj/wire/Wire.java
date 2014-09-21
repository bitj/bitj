package org.bitj.wire;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLongs;

import java.math.BigInteger;

import static org.bitj.utils.Utils.*;

public class Wire {

  public static byte[] int32ToBytesLE(int val) {
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24))
    };
  }

  public static byte[] int64ToBytesLE(long val) {
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24)),
      (byte) (0xFF & (val >>> 32)),
      (byte) (0xFF & (val >>> 40)),
      (byte) (0xFF & (val >>> 48)),
      (byte) (0xFF & (val >>> 56)),
    };
  }

  public static byte[] unsInt16ToBytesBE(int val) {
    if (val < 0 || val > MAX_UINT_16)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_16 + "]");
    return new byte[] {
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & val)
    };
  }

  public static byte[] unsInt16ToBytesLE(int val) {
    if (val < 0 || val > MAX_UINT_16)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_16 + "]");
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8))
    };
  }

  public static byte[] unsInt32ToBytesLE(long val) {
    if (val < 0 || val > MAX_UINT_32)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_32 + "]");
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24))
    };
  }

  public static byte[] unsInt64ToBytesLE(BigInteger val) {
    if (val.signum() == -1  ||  val.compareTo(MAX_UINT_64) > 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_64 + "]");
    return reverseWithRightPadding(val.toByteArray(), 8);
  }

  public static byte[] unsInt64ToBytesLE(long val) {
    if (val < 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_64 + "]");
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24)),
      (byte) (0xFF & (val >>> 32)),
      (byte) (0xFF & (val >>> 40)),
      (byte) (0xFF & (val >>> 48)),
      (byte) (0xFF & (val >>> 56)),
    };
  }

  public static byte[] reverseWithRightPadding(byte[] inBytes, int padding) {
    byte[] outBytes = new byte[padding];
    for (int i = 0; i < inBytes.length; i++)
      outBytes[i] = inBytes[inBytes.length - 1 - i];
    return outBytes;
  }

  public static byte[] stringToVarBytes(String s) {
    byte[] sBytes = getBytesUTF8(s);
    byte[] sLen = unsIntToVarBytes(sBytes.length);
    return Bytes.concat(sLen, sBytes);
  }

  public static byte[] unsIntToVarBytes(long val) {
    if (val < 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_64 + "]");

    if (isLessThanUnsigned(val, 0xFD))
      return new byte[] { (byte) val };

    if (isLessThanOrEqualToUnsigned(val, 0xFFFFL))
      return Bytes.concat(new byte[] { (byte) 253 }, unsInt16ToBytesLE((int) val));

    if (isLessThanOrEqualToUnsigned(val, 0xFFFFFFFFL))
      return Bytes.concat(new byte[] { (byte) 254 }, unsInt32ToBytesLE(val));

    return Bytes.concat(new byte[] { (byte) 255 }, unsInt64ToBytesLE(val));
  }

  public static int unsIntVarSizeInBytes(long val) {
    if (val < 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT_64 + "]");
    if (isLessThanUnsigned(val, 0xFD))
      return 1;
    if (isLessThanOrEqualToUnsigned(val, 0xFFFFL))
      return 3;
    if (isLessThanOrEqualToUnsigned(val, 0xFFFFFFFFL))
      return 5;
    return 9;
  }

  public static boolean isLessThanOrEqualToUnsigned(long n1, long n2) {
    return UnsignedLongs.compare(n1, n2) <= 0;
  }

  public static boolean isLessThanUnsigned(long n1, long n2) {
    return UnsignedLongs.compare(n1, n2) < 0;
  }

  public static byte[] asciiStringToBytesPaddedWith0(String s, int targetLength) {
    byte[] bytes = new byte[targetLength];
    byte[] sBytes = getBytesASCII(s);
    if (sBytes.length > targetLength)
      throw new IllegalArgumentException("String " + s + " cannot be longer than its target length " + targetLength);
    System.arraycopy(sBytes, 0, bytes, 0, sBytes.length);
    return bytes;
  }

  public static void reverseBytesInPlace(byte[] bytes) {
    if (bytes.length % 2 != 0)
      throw new IllegalArgumentException("The array length must be even, is " + bytes.length);
    for (int i = 0; i < bytes.length / 2; i++) {
      int j = bytes.length - i - 1;
      byte tmp = bytes[i];
      bytes[i] = bytes[j];
      bytes[j] = tmp;
    }
  }

  public static byte[] reverseBytes(byte[] bytes) {
    if (bytes.length % 2 != 0)
      throw new IllegalArgumentException("The array length must be even, is " + bytes.length);
    byte[] reversed = new byte[bytes.length];
    for (int i = 0; i < bytes.length; i++)
      reversed[i] = bytes[bytes.length - i - 1];
    return reversed;
  }

}
