package org.bitj.wire.messages;

import org.bitj.utils.Crypto;
import org.bitj.utils.Debug;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.BitcoinOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.util.Arrays;

public abstract class Message {

  private static final byte[] MAGIC_BYTES = new byte[] { (byte) 0xF9, (byte) 0xBE, (byte) 0xB4, (byte) 0xD9 };
  public static final int MAX_STRING_LENGTH = 4 * 1024; // 4 KB
  private static final int MAX_MESSAGE_SIZE = 32 * 1014 * 1024; // 32 MB

  public void serialize(OutputStream os) throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(os);
    byte[] payload = serializePayload();
    out.write(MAGIC_BYTES);
    out.writeAsciiStringPaddedWith0(name(), 12);
    out.writeUnsignedInt32LE(payload.length);
    out.write(Crypto.bitcoinChecksum(payload));
    out.write(payload);
    out.flush();
  }

  public static Message deserialize(InputStream is) throws IOException {
    BitcoinInputStream in = new BitcoinInputStream(is);

    in.readMagic(MAGIC_BYTES);

    String messageName = in.readPaddedAsciiString(12);

    long length = in.readUnsignedInt32LE();
    throwIfTooLarge(length);

    byte[] expectedChecksum = in.readBytes(4);

    byte[] payload = in.readBytes((int)length);
    throwIfChecksumIsInvalid(payload, expectedChecksum);

    BitcoinInputStream payloadIn = new BitcoinInputStream(new ByteArrayInputStream(payload));
    return deserializePayload(payloadIn, messageName);
  }

  private static void throwIfTooLarge(long length) throws ProtocolException {
    if (length > MAX_MESSAGE_SIZE)
      throw new TooLarge("Message to large " + length + " > " + MAX_MESSAGE_SIZE);
  }

  private static void throwIfChecksumIsInvalid(byte[] payload, byte[] expectedChecksum) throws ProtocolException {
    byte[] actualChecksum = Crypto.bitcoinChecksum(payload);
    if (!Arrays.equals(actualChecksum, expectedChecksum))
      throw new InvalidChecksum("Invalid bitcoinChecksum " + Debug.bytesToHex(actualChecksum) + ", expected " + Debug.bytesToHex(expectedChecksum));
  }

  private static Message deserializePayload(BitcoinInputStream in, String messageName) throws IOException {
    if (messageName.equals("version"))
      return VersionMessage.deserializePayload(in);
    if (messageName.equals("verack"))
      return VerackMessage.deserializePayload(in);
    if (messageName.equals("inv"))
      return InvMessage.deserializePayload(in);
    if (messageName.equals("getaddr"))
      return GetAddrMessage.deserializePayload(in);
    if (messageName.equals("addr"))
      return AddrMessage.deserializePayload(in);
    if (messageName.equals("getblocks"))
      return GetBlocksMessage.deserializePayload(in);
    if (messageName.equals("getdata"))
      return GetDataMessage.deserializePayload(in);
    if (messageName.equals("tx"))
      return TxMessage.deserializePayload(in);
    if (messageName.equals("block"))
      return BlockMessage.deserializePayload(in);
    if (messageName.equals("getheaders"))
      return GetHeadersMessage.deserializePayload(in);
    if (messageName.equals("headers"))
      return HeadersMessage.deserializePayload(in);
    throw new Unrecognized("Unknown message name " + messageName);  // TODO: introduce ProtocolException
  }

  protected abstract String name();

  protected abstract byte[] serializePayload() throws IOException;

  public static class InvalidChecksum extends ProtocolException {
    public InvalidChecksum(String msg) { super(msg); }
  }

  public static class TooLarge extends ProtocolException {
    public TooLarge(String msg) { super(msg); }
  }

  public static class Unrecognized extends ProtocolException {
    public Unrecognized(String msg) { super(msg); }
  }

}
