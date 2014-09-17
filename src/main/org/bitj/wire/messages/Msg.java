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
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Msg {

  private static final byte[] MAGIC_BYTES = new byte[] { (byte) 0xF9, (byte) 0xBE, (byte) 0xB4, (byte) 0xD9 };
  public static final int MAX_STRING_LENGTH = 4 * 1024; // 4 KB
  private static final int MAX_MESSAGE_SIZE = 32 * 1014 * 1024; // 32 MB

  private static Logger logger;

  public void serialize(OutputStream os) throws IOException {
    BitcoinOutputStream out = new BitcoinOutputStream(os);
    byte[] payload = serializePayload();
    out.write(MAGIC_BYTES);
    out.writeAsciiStringPaddedWith0(name(), 12);
    out.writeUnsInt32LE(payload.length);
    out.write(Crypto.bitcoinChecksum(payload));
    out.write(payload);
    out.flush();
    logDid(this, "Send");
  }

  public static Msg deserialize(InputStream is) throws IOException {
    BitcoinInputStream in = new BitcoinInputStream(is);

    in.readMagic(MAGIC_BYTES);

    String messageName = in.readPaddedAsciiString(12);

    long length = in.readUnsInt32LE();
    throwIfTooLarge(length);

    byte[] expectedChecksum = in.readBytes(4);

    byte[] payload = in.readBytes((int)length);
    throwIfChecksumIsInvalid(payload, expectedChecksum);

    BitcoinInputStream payloadIn = new BitcoinInputStream(new ByteArrayInputStream(payload));
    Msg msg = deserializePayload(payloadIn, messageName);
    logDid(msg, "Recv");
    return msg;
  }

  private static void throwIfTooLarge(long length) throws ProtocolException {
    if (length > MAX_MESSAGE_SIZE)
      throw new TooLarge("Msg to large " + length + " > " + MAX_MESSAGE_SIZE);
  }

  private static void throwIfChecksumIsInvalid(byte[] payload, byte[] expectedChecksum) throws ProtocolException {
    byte[] actualChecksum = Crypto.bitcoinChecksum(payload);
    if (!Arrays.equals(actualChecksum, expectedChecksum))
      throw new InvalidChecksum("Invalid bitcoinChecksum " + Debug.bytesToHex(actualChecksum) + ", expected " + Debug.bytesToHex(expectedChecksum));
  }

  private static Msg deserializePayload(BitcoinInputStream in, String messageName) throws IOException {
    if (messageName.equals("version"))
      return VersionMsg.deserializePayload(in);
    if (messageName.equals("verack"))
      return VerackMsg.deserializePayload(in);
    if (messageName.equals("inv"))
      return InvMsg.deserializePayload(in);
    if (messageName.equals("getaddr"))
      return GetAddrMsg.deserializePayload(in);
    if (messageName.equals("addr"))
      return AddrMsg.deserializePayload(in);
    if (messageName.equals("getblocks"))
      return GetBlocksMsg.deserializePayload(in);
    if (messageName.equals("getdata"))
      return GetDataMsg.deserializePayload(in);
    if (messageName.equals("tx"))
      return TxMsg.deserializePayload(in);
    if (messageName.equals("block"))
      return BlockMsg.deserializePayload(in);
    if (messageName.equals("getheaders"))
      return GetHeadersMsg.deserializePayload(in);
    if (messageName.equals("headers"))
      return HeadersMsg.deserializePayload(in);
    throw new Unrecognized("Unknown message name " + messageName);  // TODO: introduce ProtocolException
  }

  public abstract String name();

  protected abstract byte[] serializePayload() throws IOException;

  private static void logDid(Msg msg, String deed) {
    if (logger().getLevel().intValue() >= Level.FINE.intValue())
      logger().fine(deed + ": " + msg.name());
    else
      logger().finer(deed + ": " + msg);
  }

  private static Logger logger() {
    if (logger == null)
      logger = Logger.getLogger("org.bitj.eventlogger");
    return logger;
  }

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
