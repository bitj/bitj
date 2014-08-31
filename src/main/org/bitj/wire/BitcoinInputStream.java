package org.bitj.wire;

import org.bitj.Sha256Hash;
import org.bitj.utils.Debug;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ProtocolException;

public class BitcoinInputStream extends FilterInputStream {

  public BitcoinInputStream(InputStream is) {
    super(is);
  }

  public int readUnsignedInt16BE() throws IOException {
    byte[] bytes = new byte[2];
    readFully(bytes);
    return ((0xFF & bytes[0]) << 8) + (0xFF & bytes[1]);
  }

  public int readUnsignedInt16LE() throws IOException {
    byte[] bytes = new byte[2];
    readFully(bytes);
    return ((0xFF & bytes[0])) + ((0xFF & bytes[1]) << 8);
  }

  public long readUnsignedInt32LE() throws IOException {
    byte[] bytes = new byte[4];
    readFully(bytes);
    return ((0xFFL & bytes[0])) +
      ((0xFFL & bytes[1]) << 8) +
      ((0xFFL & bytes[2]) << 16) +
      ((0xFFL & bytes[3]) << 24);
  }

  public BigInteger readUnsignedInt64LE() throws IOException {
    byte[] bytesLE = new byte[8];
    readFully(bytesLE);
    byte[] bytesBE = Wire.reverseWithRightPadding(bytesLE, 8);
    return new BigInteger(1, bytesBE);
  }

  public int readInt32LE() throws IOException {
    byte[] bytes = new byte[4];
    readFully(bytes);
    return ((0xFF & bytes[0])) +
      ((0xFF & bytes[1]) << 8) +
      ((0xFF & bytes[2]) << 16) +
      ((0xFF & bytes[3]) << 24);
  }

  public long readInt64LE() throws IOException {
    byte[] bytes = new byte[8];
    readFully(bytes);
    return ((0xFFL & bytes[0])) +
      ((0xFFL & bytes[1]) << 8) +
      ((0xFFL & bytes[2]) << 16) +
      ((0xFFL & bytes[3]) << 24) +
      ((0xFFL & bytes[4]) << 32) +
      ((0xFFL & bytes[5]) << 40) +
      ((0xFFL & bytes[6]) << 48) +
      ((0xFFL & bytes[7]) << 56);
  }

  public BigInteger readUnsignedVarInt() throws IOException {
    int b = read();
    if (b == -1)
      throw new EOFException();
    if (b < 0xFD)
      return BigInteger.valueOf(b);
    if (b == 0xFD)
      return BigInteger.valueOf(readUnsignedInt16LE());
    if (b == 0xFE)
      return BigInteger.valueOf(readUnsignedInt32LE());
    if (b == 0xFF)
      return readUnsignedInt64LE();
    throw new RuntimeException("Unexpected byte value " + b);  // cannot happen
  }

  public String readVarString(long max) throws IOException {
    BigInteger length = readUnsignedVarInt();
    if (length.compareTo(BigInteger.valueOf(max)) > 0  ||  length.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0)
      throw new ProtocolException("VarString too large " + length + " > " + max + " bytes");
    byte[] bytes = new byte[length.intValue()];
    readFully(bytes);
    return new String(bytes, "UTF-8");
  }

  public String readPaddedAsciiString(int paddedLength) throws IOException {
    if (paddedLength == 0) return "";
    byte[] bytes = new byte[paddedLength];
    readFully(bytes);
    for (int endIx = 0; endIx < paddedLength; endIx++)
      if (bytes[endIx] == 0)
        return new String(bytes, 0, endIx, "ASCII");
    return new String(bytes, "ASCII");
  }

  public void readMagic(byte[] magicMarker) throws IOException {
    int i = 0;
    while (true) {
      int b = read();
      if (b == -1)
        throw new EOFException("EOF before the [" + Debug.bytesToHex(magicMarker) + "] marker was found");
      if (b == (0xFF & magicMarker[i]))
        if (i == magicMarker.length-1)
          return; // marker found and read
        else
          i++;
      else
        i = 0;
    }
  }

  public InetAddress readIP() throws IOException {
    return InetAddress.getByAddress(readBytes(16));
  }

  public byte[] readBytes(int n) throws IOException {
    byte[] bytes = new byte[n];
    readFully(bytes);
    return bytes;
  }

  public Sha256Hash readSha256Hash() throws IOException {
    byte[] bts = readBytes(32);
    Wire.reverseBytesInPlace(bts);
    return new Sha256Hash(bts);
  }

  public void readFully(byte bytes[]) throws IOException {
    readFully(bytes, 0, bytes.length);
  }

  public void readFully(byte bytes[], int off, int len) throws IOException {
    if (off < 0 || len < 0)
      throw new IndexOutOfBoundsException();
    int n = 0;
    while (n < len) {
      int count = read(bytes, off + n, len - n);
      if (count < 0)
        throw new EOFException();
      n += count;
    }
  }

  public void skipFully(int bytesToBeSkipped) throws IOException {
    if (bytesToBeSkipped <= 0) return;
    int skipped = 0;
    while (skipped < bytesToBeSkipped) {
      long count = skip(bytesToBeSkipped - skipped);
      if (count <= 0)
        throw new EOFException();
      skipped += count;
    }
  }

}
