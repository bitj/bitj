package org.bitj;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class BitcoinOutputStream extends FilterOutputStream {

  public BitcoinOutputStream(OutputStream out) {
    super(out);
  }

  public void writeInt32LE(int val) throws IOException {
    write(Wire.int32ToBytesLE(val));
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

  public void writeUnsignedIntVar(long val) throws IOException {
    write(Wire.unsignedIntToVarBytes(val));
  }

  public void writeAsciiStringPaddedWith0(String s, int targetLength) throws IOException {
    write(Wire.asciiStringToBytesPaddedWith0(s, targetLength));
  }

  public byte[] toByteArray() {
    return ((ByteArrayOutputStream) out).toByteArray();
  }

}
