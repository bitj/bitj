package org.bitj.wire;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLongs;

import java.math.BigInteger;

import static org.bitj.utils.Utils.getBytesASCII;
import static org.bitj.utils.Utils.getBytesUTF8;

public class Wire {

  public static final long MAX_UINT16 = 256L * 256L - 1;
  public static final long MAX_UINT32 = 256L * 256L * 256L * 256L - 1;
  public static final BigInteger MAX_UINT64 = new BigInteger("18446744073709551615");

  public static byte[] int32ToBytesLE(int val) {
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24))
    };
  }

  public static byte[] unsignedInt16ToBytesBE(int val) {
    if (val < 0 || val > MAX_UINT16)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT16 + "]");
    return new byte[] {
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & val)
    };
  }

  public static byte[] unsignedInt16ToBytesLE(int val) {
    if (val < 0 || val > MAX_UINT16)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT16 + "]");
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8))
    };
  }

  public static byte[] unsignedInt32ToBytesLE(long val) {
    if (val < 0 || val > MAX_UINT32)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT32 + "]");
    return new byte[] {
      (byte) (0xFF & val),
      (byte) (0xFF & (val >>> 8)),
      (byte) (0xFF & (val >>> 16)),
      (byte) (0xFF & (val >>> 24))
    };
  }

  public static byte[] unsignedInt64ToBytesLE(BigInteger val) {
    if (val.signum() == -1  ||  val.compareTo(MAX_UINT64) > 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT64 + "]");
    return reverseWithRightPadding(val.toByteArray(), 8);
  }

  public static byte[] unsignedInt64ToBytesLE(long val) {
    if (val < 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT64 + "]");
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
    byte[] sLen = unsignedIntToVarBytes(sBytes.length);
    return Bytes.concat(sLen, sBytes);
  }

  public static byte[] unsignedIntToVarBytes(long val) {
    if (val < 0)
      throw new IllegalArgumentException("" + val + " must be in range [0," + MAX_UINT64 + "]");

    if (isLessThanUnsigned(val, 0xFD))
      return new byte[] { (byte) val };

    if (isLessThanOrEqualToUnsigned(val, 0xFFFFL))
      return new byte[] { (byte) 253, (byte) val, (byte) (val >>> 8)};

    if (isLessThanOrEqualToUnsigned(val, 0xFFFFFFFFL))
      return Bytes.concat(new byte[] { (byte) 254 }, unsignedInt32ToBytesLE(val));

    return Bytes.concat(new byte[] { (byte) 255 }, unsignedInt64ToBytesLE(val));
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

}
