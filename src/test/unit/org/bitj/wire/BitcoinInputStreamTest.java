package org.bitj.wire;

import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.wire.messages.Msg;
import org.testng.annotations.Test;

import java.io.EOFException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ProtocolException;

import static org.testng.Assert.assertEquals;

public class BitcoinInputStreamTest extends BaseTest {

  // readMagic

  @Test(expectedExceptions = EOFException.class)
  public void readMagicMarkerNotPresentInTheStream() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 0, 66, 33, 22);
    byte[] magicMarker = bytes(99);
    in.readMagic(magicMarker);
  }

  @Test
  public void readMagic1ByteFoundAt0() throws Exception {
    BitcoinInputStream in = bitcoinStream(254, 1);
    byte[] magicMarker = bytes(254);
    in.readMagic(magicMarker);
    assertEquals(in.read(), 1);
  }

  @Test
  public void readMagic2BytesFoundAt0() throws Exception {
    BitcoinInputStream in = bitcoinStream(254, 127, 66);
    byte[] magicMarker = bytes(254, 127);
    in.readMagic(magicMarker);
    assertEquals(in.read(), 66);
  }

  @Test
  public void readMagic2BytesFoundInTheMiddleOfStream() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 0, 11, 66, 33, 22);
    byte[] magicMarker = bytes(66, 33);
    in.readMagic(magicMarker);
    assertEquals(in.read(), 22);
  }

  @Test
  public void readMagic4BytesFoundInTheMiddleOfStreamAfterPartialMarkers() throws Exception {
    BitcoinInputStream in = bitcoinStream("00 FF 7F F9 BE B4 0A 0A F9 BE B4 D9 FF 80 16");
    byte[] magicMarker = bytes("F9 BE B4 D9");
    in.readMagic(magicMarker);
    assertEquals(in.read(), 0xFF);
    assertEquals(in.read(), 0x80);
  }

  // readPaddedAsciiString

  @Test
  public void readPaddedAsciiString_Empty() throws Exception {
    String s = bitcoinStream().readPaddedAsciiString(0);
    assertEquals(s, "");

    String s2 = bitcoinStream(1, 2, 3).readPaddedAsciiString(0);
    assertEquals(s2, "");
  }

  @Test(expectedExceptions = EOFException.class)
  public void readPaddedAsciiString_WhenStreamIsShorterThanTargetLength() throws Exception {
    bitcoinStream(0, 1).readPaddedAsciiString(3);
  }

  @Test
  public void readPaddedAsciiString_WithNoPadding() throws Exception {
    BitcoinInputStream in = bitcoinStream(65, 66, 67, 255);
    String s = in.readPaddedAsciiString(3);
    assertEquals(s, "ABC");
    assertEquals(in.read(), 255);   // stream position is right after string
  }

  @Test
  public void readPaddedAsciiString() throws Exception {
    BitcoinInputStream in = bitcoinStream(126, 122, 0, 0, 255, 127);
    String s = in.readPaddedAsciiString(4);
    assertEquals(s, "~z");
    assertEquals(in.read(), 255);   // stream position is right after padding
    assertEquals(in.read(), 127);
  }

  // readUnsInt16BE

  @Test(expectedExceptions = EOFException.class)
  public void readUnsInt16BE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0).readUnsInt16BE();
  }

  @Test
  public void readUnsInt16BE() throws Exception {
    assertEquals(bitcoinStream(0, 0).readUnsInt16BE(), 0);
    assertEquals(bitcoinStream(0, 200).readUnsInt16BE(), 200);
    assertEquals(bitcoinStream(1, 0).readUnsInt16BE(), 256);
    assertEquals(bitcoinStream(255, 255).readUnsInt16BE(), 65535);
  }

  // readUnsInt16LE

  @Test(expectedExceptions = EOFException.class)
  public void readUnsInt16LE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0).readUnsInt16LE();
  }

  @Test
  public void readUnsInt16LE() throws Exception {
    assertEquals(bitcoinStream(0, 0).readUnsInt16LE(), 0);
    assertEquals(bitcoinStream(200, 0).readUnsInt16LE(), 200);
    assertEquals(bitcoinStream(0, 1).readUnsInt16LE(), 256);
    assertEquals(bitcoinStream(255, 255).readUnsInt16LE(), 65535);
  }

  // readUnsInt32LE

  @Test(expectedExceptions = EOFException.class)
  public void readUnsInt32LE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0, 0, 0).readUnsInt32LE();
  }

  @Test
  public void readUnsInt32LE() throws Exception {
    assertEquals(bitcoinStream(0, 0, 0, 0).readUnsInt32LE(), 0);
    assertEquals(bitcoinStream(200, 0, 0, 0).readUnsInt32LE(), 200);
    assertEquals(bitcoinStream(0, 1, 0, 0).readUnsInt32LE(), 256);
    assertEquals(bitcoinStream(0, 0, 1, 0).readUnsInt32LE(), 65536);
    assertEquals(bitcoinStream(0, 0, 0, 1).readUnsInt32LE(), 16777216L);
    assertEquals(bitcoinStream(255, 255, 255, 255).readUnsInt32LE(), 4294967295L);
  }

  // readUnsInt64LE

  @Test(expectedExceptions = EOFException.class)
  public void readUnsInt64LE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0, 0, 0, 0, 0, 0, 0).readUnsInt64LE();
  }

  @Test
  public void readUnsInt64LE() throws Exception {
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(0));
    assertEquals(bitcoinStream(200, 0, 0, 0, 0, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(200));
    assertEquals(bitcoinStream(0, 1, 0, 0, 0, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(256));
    assertEquals(bitcoinStream(0, 0, 1, 0, 0, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(65536));
    assertEquals(bitcoinStream(0, 0, 0, 1, 0, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(16777216L));
    assertEquals(bitcoinStream(0, 0, 0, 0, 1, 0, 0, 0).readUnsInt64LE(), BigInteger.valueOf(4294967296L));
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 1, 0, 0).readUnsInt64LE(), BigInteger.valueOf(1099511627776L));
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 1, 0).readUnsInt64LE(), BigInteger.valueOf(281474976710656L));
    assertEquals(bitcoinStream(255, 255, 255, 255, 255, 255, 255, 127).readUnsInt64LE(), BigInteger.valueOf(Long.MAX_VALUE));
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 0, 128).readUnsInt64LE(), new BigInteger("9223372036854775808"));  // 256**7 * 128
    assertEquals(bitcoinStream(255, 255, 255, 255, 255, 255, 255, 255).readUnsInt64LE(), new BigInteger("18446744073709551615")); // 256**8
  }

  // readInt32LE

  @Test(expectedExceptions = EOFException.class)
  public void readInt32LE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0, 0, 0).readInt32LE();
  }

  @Test
  public void readInt32LE() throws Exception {
    assertEquals(bitcoinStream(0, 0, 0, 0).readInt32LE(), 0);
    assertEquals(bitcoinStream(200, 0, 0, 0).readInt32LE(), 200);
    assertEquals(bitcoinStream(0, 1, 0, 0).readInt32LE(), 256);
    assertEquals(bitcoinStream(0, 0, 1, 0).readInt32LE(), 65536);
    assertEquals(bitcoinStream(0, 0, 0, 1).readInt32LE(), 16777216);
    assertEquals(bitcoinStream(0, 0, 0, 128).readInt32LE(), Integer.MIN_VALUE);
    assertEquals(bitcoinStream(255, 255, 255, 255).readInt32LE(), -1);
  }

  // readInt64LE

  @Test(expectedExceptions = EOFException.class)
  public void readInt64LE_WhenStreamEndsPrematurely() throws Exception {
    bitcoinStream(0, 0, 0, 0, 0, 0, 0).readInt64LE();
  }

  @Test
  public void readInt64LE() throws Exception {
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 0, 0).readInt64LE(), 0);
    assertEquals(bitcoinStream(200, 0, 0, 0, 0, 0, 0, 0).readInt64LE(), 200);
    assertEquals(bitcoinStream(0, 1, 0, 0, 0, 0, 0, 0).readInt64LE(), 256);
    assertEquals(bitcoinStream(0, 0, 1, 0, 0, 0, 0, 0).readInt64LE(), 65536);
    assertEquals(bitcoinStream(0, 0, 0, 1, 0, 0, 0, 0).readInt64LE(), 16777216L);
    assertEquals(bitcoinStream(0, 0, 0, 0, 1, 0, 0, 0).readInt64LE(), 4294967296L);
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 1, 0, 0).readInt64LE(), 1099511627776L);
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 1, 0).readInt64LE(), 281474976710656L);
    assertEquals(bitcoinStream(255, 255, 255, 255, 255, 255, 255, 127).readInt64LE(), Long.MAX_VALUE);
    assertEquals(bitcoinStream(255, 255, 255, 255, 255, 255, 255, 255).readInt64LE(), -1);
    assertEquals(bitcoinStream(0, 0, 0, 0, 0, 0, 0, 128).readInt64LE(), Long.MIN_VALUE);
  }

  // readUnsVarInt

  @Test
  public void readVarUnsInt() throws Exception {
    // 1 byte (uint8_t)
    assertEquals( bitcoinStream(0).readUnsVarInt(),   BigInteger.valueOf(0) );
    assertEquals( bitcoinStream(128).readUnsVarInt(), BigInteger.valueOf(128) );
    assertEquals( bitcoinStream(252).readUnsVarInt(), BigInteger.valueOf(252) );

    // 3 bytes (253 + uint16_t)
    assertEquals( bitcoinStream(253, 253, 0).readUnsVarInt(), BigInteger.valueOf(253) );
    assertEquals( bitcoinStream(253, 254, 0).readUnsVarInt(), BigInteger.valueOf(254) );
    assertEquals( bitcoinStream(253, 255, 0).readUnsVarInt(), BigInteger.valueOf(255) );
    assertEquals( bitcoinStream(253, 0, 1).readUnsVarInt(), BigInteger.valueOf(256));
    assertEquals( bitcoinStream(253, 255, 255).readUnsVarInt(), BigInteger.valueOf(65535) );

    // 5 bytes (254 + uint32_t)
    assertEquals( bitcoinStream(254, 0, 0, 1, 0).readUnsVarInt(), BigInteger.valueOf(65536) );
    assertEquals( bitcoinStream(254, 1, 0, 1, 0).readUnsVarInt(), BigInteger.valueOf(65537) );
    assertEquals( bitcoinStream(254, 0, 0, 0, 1).readUnsVarInt(), BigInteger.valueOf(16777216) );
    assertEquals( bitcoinStream(254, 1, 0, 0, 1).readUnsVarInt(), BigInteger.valueOf(16777217) );
    assertEquals( bitcoinStream(254, 255, 255, 255, 255).readUnsVarInt(), BigInteger.valueOf(4294967295L) );

    // 9 bytes (255 + uint64_t)
    assertEquals( bitcoinStream(255, 0, 0, 0, 0, 1, 0, 0, 0).readUnsVarInt(), BigInteger.valueOf(4294967296L) );
    assertEquals( bitcoinStream(255, 1, 0, 0, 0, 1, 0, 0, 0).readUnsVarInt(), BigInteger.valueOf(4294967297L) );
    assertEquals( bitcoinStream(255, 0, 0, 0, 0, 0, 1, 0, 0).readUnsVarInt(), BigInteger.valueOf(1099511627776L) );
    assertEquals( bitcoinStream(255, 0, 0, 0, 0, 0, 0, 1, 0).readUnsVarInt(), BigInteger.valueOf(281474976710656L) );
    assertEquals( bitcoinStream(255, 0, 0, 0, 0, 0, 0, 0, 1).readUnsVarInt(), BigInteger.valueOf(72057594037927936L) );
    assertEquals( bitcoinStream(255, 255, 255, 255, 255, 255, 255, 255, 127).readUnsVarInt(), BigInteger.valueOf(9223372036854775807L) );
    assertEquals( bitcoinStream(255, 0, 0, 0, 0, 0, 0, 0, 128).readUnsVarInt(), new BigInteger("9223372036854775808"));  // 256**7 * 128
    assertEquals( bitcoinStream(255, 255, 255, 255, 255, 255, 255, 255, 255).readUnsVarInt(), new BigInteger("18446744073709551615") ); // 256**8
  }

  // readVarString

  @Test(expectedExceptions = EOFException.class)
  public void readVarString_EmptyStream() throws Exception {
    bitcoinStream().readVarString(Msg.MAX_STRING_LENGTH);
  }

  @Test(expectedExceptions = EOFException.class)
  public void readVarString_StreamTooShort() throws Exception {
    bitcoinStream(3, 1, 2).readVarString(Msg.MAX_STRING_LENGTH);
  }

  @Test(expectedExceptions = ProtocolException.class)
  public void readVarString_ClaimedStringLengthToLarge() throws Exception {
    bitcoinStream(128, 1, 2).readVarString(127);
  }

  @Test
  public void readVarString_SmallString() throws Exception {
    BitcoinInputStream in = bitcoinStream(25, 47, 98, 105, 116, 106, 47, 48, 46, 49, 46, 50, 47, 196, 135, 196, 153, 197, 130, 197, 154, 197, 187, 197, 185, 33);
    assertEquals( in.readVarString(Msg.MAX_STRING_LENGTH), "/bitj/0.1.2/ćęłŚŻŹ!" );
  }

  // readIP

  @Test
  public void readIP_localhostIP4() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 127, 0, 0, 1);
    assertEquals(in.readIP(), InetAddress.getByName("127.0.0.1"));
  }

  @Test
  public void readIP_publicIP4() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF, 0xFF, 10, 0, 0, 1);
    assertEquals(in.readIP(), InetAddress.getByName("10.0.0.1"));
  }

  @Test
  public void readIP_publicIP6() throws Exception {
    BitcoinInputStream in = bitcoinStream("0000 0db8 0000 0000 0000 ff00 0042 8329");
    assertEquals(in.readIP(), InetAddress.getByName("0000:0db8:0000::0000:ff00:0042:8329"));
  }

  // readSha256HashLE

  @Test
  public void readSha256Hash() throws Exception {
    BitcoinInputStream in = bitcoinStream("00000000000000001ad3eb4971697d07256b5c049bfcf80a1a2c6815bc9b3463FF");
    assertEquals(in.readSha256HashLE(), new Sha256Hash("63349bbc15682c1a0af8fc9b045c6b25077d697149ebd31a0000000000000000"));
    assertEquals(in.read(), 255); // next byte after SHA is available
  }

  // readFully

  @Test(expectedExceptions = EOFException.class)
  public void readFully_WhenStreamIsTooShort() throws Exception {
    byte[] bytes = new byte[2];
    bitcoinStream(1).readFully(bytes);
  }

  @Test
  public void readFully() throws Exception {
    byte[] bytes = new byte[6];
    BitcoinInputStream in = bitcoinStream(0, 1, 2, 3, 4, 5, 6, 7, 8);
    in.readFully(bytes, 1, 4);
    assertEquals(bytes[1], 0);   // read from the specified index
    assertEquals(bytes[4], 3);   // read the specified number of bytes
    assertEquals(bytes[5], 0);   // didn't read too much
    assertEquals(in.read(), 4);  // didn't read too much
  }

  // skipFully

  @Test(expectedExceptions = EOFException.class)
  public void skipFully_WhenStreamIsTooShort() throws Exception {
    bitcoinStream(1).skipFully(2);
  }

  @Test
  public void skipFully_When0() throws Exception {
    bitcoinStream().skipFully(0); // does not throw
  }

  @Test
  public void skipFully() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 1, 2, 3, 4, 5, 6, 7, 8);
    in.skipFully(4);
    assertEquals(in.read(), 4);   // 0-3 were skipped
  }

  // readBytes (mostly delegates)

  @Test(expectedExceptions = EOFException.class)
  public void readBytes_WhenStreamIsTooShort() throws Exception {
    bitcoinStream(1).readBytes(2);
  }

  @Test
  public void readBytes() throws Exception {
    BitcoinInputStream in = bitcoinStream(0, 1, 2, 3, 4, 5, 6, 7, 8);
    byte[] bytes = in.readBytes(4);
    assertEquals(bytes[0], 0);
    assertEquals(bytes[3], 3);
    assertEquals(in.read(), 4);  // didn't read too much
  }

}
