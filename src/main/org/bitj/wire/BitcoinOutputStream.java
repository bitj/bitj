package org.bitj.wire;

import org.bitj.Sha256Hash;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;

public class BitcoinOutputStream extends FilterOutputStream {

  public BitcoinOutputStream(OutputStream out) {
    super(out);
  }

  public void writeInt32LE(int val) throws IOException {
    write(Wire.int32ToBytesLE(val));
  }

  public void writeUnsignedInt16BE(int val) throws IOException {
    write(Wire.unsignedInt16ToBytesBE(val));
  }

  public void writeUnsignedInt16LE(int val) throws IOException {
    write(Wire.unsignedInt16ToBytesLE(val));
  }

  public void writeUnsignedInt32LE(long val) throws IOException {
    write(Wire.unsignedInt32ToBytesLE(val));
  }

  public void writeUnsignedInt64LE(BigInteger val) throws IOException {
    write(Wire.unsignedInt64ToBytesLE(val));
  }

  public void writeUnsignedInt64LE(long val) throws IOException {
    write(Wire.unsignedInt64ToBytesLE(val));
  }

  public void writeVarString(String s) throws IOException {
    write(Wire.stringToVarBytes(s));
  }

  public void writeUnsignedVarInt(long val) throws IOException {
    write(Wire.unsignedIntToVarBytes(val));
  }

  public void writeAsciiStringPaddedWith0(String s, int targetLength) throws IOException {
    write(Wire.asciiStringToBytesPaddedWith0(s, targetLength));
  }

  public void writeIP(InetAddress ip) throws IOException {
    if (ip instanceof Inet4Address)
      write(IP4_PREFIX_FOR_IP6_NOTATION);
    write(ip.getAddress());
  }

  public void writeSha256Hash(Sha256Hash hash) throws IOException {
    write(Wire.reverseBytes(hash.getBytes()));
  }

  public byte[] toByteArray() {
    return ((ByteArrayOutputStream) out).toByteArray();
  }

  private static final byte[] IP4_PREFIX_FOR_IP6_NOTATION = new byte[] {
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF
  };

}
