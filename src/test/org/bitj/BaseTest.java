package org.bitj;

import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BaseTest {

  public static byte[] bytes(int... iBytes) {
    byte[] bBytes = new byte[iBytes.length];
    for (int i = 0; i < iBytes.length; i++) {
      int iByte = iBytes[i];
      if (iByte < 0 || iByte > 255)
        throw new IllegalArgumentException("Is not a byte: " + iByte);
      bBytes[i] = (byte) iByte;
    }
    return bBytes;
  }

  public static BitcoinInputStream bitcoinStream(int... iBytes) {
    return new BitcoinInputStream(byteStream(iBytes));
  }

  public static BitcoinInputStream bitcoinStream(byte[] bytes) {
    return new BitcoinInputStream(new ByteArrayInputStream(bytes));
  }

  public static BitcoinInputStream bitcoinStream(String hexes) {
    return bitcoinStream(Debug.hexToBytes(hexes));
  }

  public static ByteArrayInputStream byteStream(int... iBytes) {
    return new ByteArrayInputStream(bytes(iBytes));
  }

  public static byte[] serializeToBytes(Message message) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    message.serialize(os);
    return os.toByteArray();
  }

}
