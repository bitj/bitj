package org.bitj;

import org.testng.annotations.Test;

import java.math.BigInteger;

import static org.testng.Assert.assertEquals;

public class WireTest extends BaseTest {

  // int32ToBytesLE

  @Test
  public void int32ToBytesLE() throws Exception {
    assertEquals( Wire.int32ToBytesLE(0),        bytes(0, 0, 0, 0) );
    assertEquals( Wire.int32ToBytesLE(1),        bytes(1, 0, 0, 0) );
    assertEquals( Wire.int32ToBytesLE(256),      bytes(0, 1, 0, 0) );
    assertEquals( Wire.int32ToBytesLE(65536),    bytes(0, 0, 1, 0) );
    assertEquals( Wire.int32ToBytesLE(Integer.MAX_VALUE), bytes(255, 255, 255, 127) );
    assertEquals( Wire.int32ToBytesLE(Integer.MIN_VALUE), bytes(0, 0, 0, 128) );
    assertEquals( Wire.int32ToBytesLE(-1), bytes(255, 255, 255, 255) );
  }

  // unsignedInt16ToBytesLE

  @Test
  public void unsignedInt16ToBytesLE() throws Exception {
    assertEquals( Wire.unsignedInt16ToBytesLE(0),     bytes(0, 0) );
    assertEquals( Wire.unsignedInt16ToBytesLE(1),     bytes(1, 0) );
    assertEquals( Wire.unsignedInt16ToBytesLE(256),   bytes(0, 1) );
    assertEquals( Wire.unsignedInt16ToBytesLE(65534), bytes(254, 255) );
    assertEquals( Wire.unsignedInt16ToBytesLE(65535), bytes(255, 255) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt16ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsignedInt16ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt16ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsignedInt16ToBytesLE(65536);
  }

  // unsignedInt32ToBytesLE

  @Test
  public void unsignedInt32ToBytesLE() throws Exception {
    assertEquals( Wire.unsignedInt32ToBytesLE(0),        bytes(0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt32ToBytesLE(1),        bytes(1, 0, 0, 0) );
    assertEquals( Wire.unsignedInt32ToBytesLE(256),      bytes(0, 1, 0, 0) );
    assertEquals( Wire.unsignedInt32ToBytesLE(65536),    bytes(0, 0, 1, 0) );
    assertEquals( Wire.unsignedInt32ToBytesLE(16777216), bytes(0, 0, 0, 1) );
    assertEquals( Wire.unsignedInt32ToBytesLE(65534),    bytes(254, 255, 0, 0) );
    assertEquals( Wire.unsignedInt32ToBytesLE(4294967295L), bytes(255, 255, 255, 255) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt32ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsignedInt32ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt32ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsignedInt32ToBytesLE(4294967296L);
  }

  // unsignedInt64ToBytesLE

  @Test
  public void unsignedInt64ToBytesLE() throws Exception {
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("0")),             bytes(0, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("1")),             bytes(1, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("256")),           bytes(0, 1, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("65536")),         bytes(0, 0, 1, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("16777216")),      bytes(0, 0, 0, 1, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("4294967296")),    bytes(0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("1099511627776")), bytes(0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("281474976710656")), bytes(0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("72057594037927936")), bytes(0, 0, 0, 0, 0, 0, 0, 1) );
    assertEquals( Wire.unsignedInt64ToBytesLE(new BigInteger("72057594037927937")), bytes(1, 0, 0, 0, 0, 0, 0, 1) );

    assertEquals( Wire.unsignedInt64ToBytesLE(0),               bytes(0, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(1),               bytes(1, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(256),             bytes(0, 1, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(65536),           bytes(0, 0, 1, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(16777216),        bytes(0, 0, 0, 1, 0, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(4294967296L),     bytes(0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(1099511627776L),  bytes(0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(281474976710656L),bytes(0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsignedInt64ToBytesLE(Long.MAX_VALUE),  bytes(255, 255, 255, 255, 255, 255, 255, 127) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt64ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsignedInt64ToBytesLE(new BigInteger("-1"));
    Wire.unsignedInt64ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsignedInt64ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsignedInt64ToBytesLE(new BigInteger("18446744073709551616"));
  }

  // unsignedIntToVarBytes

  @Test
  public void unsignedInt64ToVarBytes() throws Exception {
    // 1 byte (uint8_t)
    assertEquals( Wire.unsignedIntToVarBytes(0),               bytes(0) );
    assertEquals( Wire.unsignedIntToVarBytes(128),             bytes(128) );
    assertEquals( Wire.unsignedIntToVarBytes(252),             bytes(252) );

    // 3 bytes (253 + uint16_t)
    assertEquals( Wire.unsignedIntToVarBytes(253),             bytes(253, 253, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(254),             bytes(253, 254, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(255),             bytes(253, 255, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(256),             bytes(253, 0, 1) );
    assertEquals( Wire.unsignedIntToVarBytes(65535),           bytes(253, 255, 255) );

    // 5 bytes (254 + uint32_t)
    assertEquals( Wire.unsignedIntToVarBytes(65536),           bytes(254, 0, 0, 1, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(65537),           bytes(254, 1, 0, 1, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(16777216),        bytes(254, 0, 0, 0, 1) );
    assertEquals( Wire.unsignedIntToVarBytes(16777217),        bytes(254, 1, 0, 0, 1) );
    assertEquals( Wire.unsignedIntToVarBytes(4294967295L),     bytes(254, 255, 255, 255, 255) );
    assertEquals( Wire.unsignedIntToVarBytes(2147483647L),     bytes(254, 255, 255, 255, 127) );

    // 9 bytes (255 + uint64_t)
    assertEquals( Wire.unsignedIntToVarBytes(4294967296L),          bytes(255, 0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(4294967297L),          bytes(255, 1, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(1099511627776L),       bytes(255, 0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(281474976710656L),     bytes(255, 0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsignedIntToVarBytes(72057594037927936L),   bytes(255, 0, 0, 0, 0, 0, 0, 0, 1) );
    assertEquals( Wire.unsignedIntToVarBytes(9223372036854775807L), bytes(255, 255, 255, 255, 255, 255, 255, 255, 127) );
  }

  // stringToVarBytes

  @Test
  public void stringToVarBytes() throws Exception {
    assertEquals( Wire.stringToVarBytes(""), bytes(0) );
    assertEquals( Wire.stringToVarBytes("a"), bytes(1, 97) );
    assertEquals( Wire.stringToVarBytes("Adam"), bytes(4, 65, 100, 97, 109) );
    assertEquals( Wire.stringToVarBytes("ą"), bytes(2, 0xC4, 0x85) );
    assertEquals( Wire.stringToVarBytes("/bitj/0.1.2/ćęłŚŻŹ!"), bytes(25, 47, 98, 105, 116, 106, 47, 48, 46, 49, 46, 50, 47, 196, 135, 196, 153, 197, 130, 197, 154, 197, 187, 197, 185, 33) );
  }

  // reverseWithRightPadding

  @Test
  public void reverseWithRightPadding() {
    assertEquals( Wire.reverseWithRightPadding(bytes(0), 1),              bytes(0) );
    assertEquals( Wire.reverseWithRightPadding(bytes(1, 2), 2),           bytes(2, 1) );
    assertEquals( Wire.reverseWithRightPadding(bytes(130, 129, 128), 3),  bytes(128, 129, 130) );
    assertEquals( Wire.reverseWithRightPadding(bytes(5, 10, 15), 8),      bytes(15, 10, 5, 0, 0, 0, 0, 0) );
  }

  // asciiStringToBytesPaddedWith0

  @Test
  public void asciiStringToBytesPaddedWith0() throws Exception {
    assertEquals( Wire.asciiStringToBytesPaddedWith0("", 0), bytes() );
    assertEquals( Wire.asciiStringToBytesPaddedWith0("", 3), bytes(0, 0, 0) );
    assertEquals( Wire.asciiStringToBytesPaddedWith0("version", 7), bytes(118, 101, 114, 115, 105, 111, 110) );
    assertEquals( Wire.asciiStringToBytesPaddedWith0("version", 12), bytes(118, 101, 114, 115, 105, 111, 110, 0, 0, 0, 0, 0) );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void asciiStringToBytesPaddedWith0_WhenStringBytesLongerThanTargetLength() throws Exception {
    Wire.asciiStringToBytesPaddedWith0("abc", 2);
  }

}
