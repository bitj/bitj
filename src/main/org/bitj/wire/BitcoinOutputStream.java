package org.bitj.wire;

import org.bitj.Sha256Hash;
import org.bitj.utils.Debug;

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

  public void writeInt64LE(long val) throws IOException {
    write(Wire.int64ToBytesLE(val));
  }

  public void writeUnsignedInt16BE(int val) throws IOException {
    write(Wire.unsignedInt16ToBytesBE(val));
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

  public void writeAsciiStringPaddedWith0(String s, int targetLength) throws IOException { write(Wire.asciiStringToBytesPaddedWith0(s, targetLength)); }

  public void writeIP(InetAddress ip) throws IOException {
    if (ip instanceof Inet4Address)
      write(IP4_PREFIX_FOR_IP6_NOTATION);
    write(ip.getAddress());
  }

  public void writeSha256HashLE(Sha256Hash hash) throws IOException {
    write(Wire.reverseBytes(hash.getBytes()));
  }

  public byte[] toByteArray() {
    return ((ByteArrayOutputStream) out).toByteArray();
  }

  private static final byte[] IP4_PREFIX_FOR_IP6_NOTATION =  Debug.hexToBytes("00 00 00 00 00 00 00 00 00 00 FF FF");

}
