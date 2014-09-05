package org.bitj.wire;

import org.bitj.BaseTest;
import org.bitj.utils.Debug;
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

  // int64ToBytesLE

  @Test
  public void int64ToBytesLE() throws Exception {
    assertEquals( Wire.int64ToBytesLE(0),               bytes(0, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(1),               bytes(1, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(256),             bytes(0, 1, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(65536),           bytes(0, 0, 1, 0, 0, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(16777216),        bytes(0, 0, 0, 1, 0, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(4294967296L),     bytes(0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(1099511627776L),  bytes(0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.int64ToBytesLE(281474976710656L),bytes(0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.int64ToBytesLE(Long.MAX_VALUE),  bytes(255, 255, 255, 255, 255, 255, 255, 127) );
    assertEquals( Wire.int64ToBytesLE(Long.MIN_VALUE),  bytes(0, 0, 0, 0, 0, 0, 0, 128) );
    assertEquals( Wire.int64ToBytesLE(Long.MIN_VALUE + 1),  bytes(1, 0, 0, 0, 0, 0, 0, 128) );
    assertEquals( Wire.int64ToBytesLE(-1),              bytes(255, 255, 255, 255, 255, 255, 255, 255) );
  }

  // unsInt16ToBytesBE

  @Test
  public void unsInt16ToBytesBE() throws Exception {
    assertEquals( Wire.unsInt16ToBytesBE(0),     bytes(0, 0) );
    assertEquals( Wire.unsInt16ToBytesBE(1),     bytes(0, 1) );
    assertEquals( Wire.unsInt16ToBytesBE(256),   bytes(1, 0) );
    assertEquals( Wire.unsInt16ToBytesBE(65534), bytes(255, 254) );
    assertEquals( Wire.unsInt16ToBytesBE(65535), bytes(255, 255) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt16ToBytesBE_WhenValueIsToSmall() throws Exception {
    Wire.unsInt16ToBytesBE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt16ToBytesBE_WhenValueToLarge() throws Exception {
    Wire.unsInt16ToBytesBE(65536);
  }

  // unsInt16ToBytesLE

  @Test
  public void unsInt16ToBytesLE() throws Exception {
    assertEquals( Wire.unsInt16ToBytesLE(0),     bytes(0, 0) );
    assertEquals( Wire.unsInt16ToBytesLE(1),     bytes(1, 0) );
    assertEquals( Wire.unsInt16ToBytesLE(256),   bytes(0, 1) );
    assertEquals( Wire.unsInt16ToBytesLE(65534), bytes(254, 255) );
    assertEquals( Wire.unsInt16ToBytesLE(65535), bytes(255, 255) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt16ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsInt16ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt16ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsInt16ToBytesLE(65536);
  }

  // unsInt32ToBytesLE

  @Test
  public void unsInt32ToBytesLE() throws Exception {
    assertEquals( Wire.unsInt32ToBytesLE(0),        bytes(0, 0, 0, 0) );
    assertEquals( Wire.unsInt32ToBytesLE(1),        bytes(1, 0, 0, 0) );
    assertEquals( Wire.unsInt32ToBytesLE(256),      bytes(0, 1, 0, 0) );
    assertEquals( Wire.unsInt32ToBytesLE(65536),    bytes(0, 0, 1, 0) );
    assertEquals( Wire.unsInt32ToBytesLE(16777216), bytes(0, 0, 0, 1) );
    assertEquals( Wire.unsInt32ToBytesLE(65534),    bytes(254, 255, 0, 0) );
    assertEquals( Wire.unsInt32ToBytesLE(4294967295L), bytes(255, 255, 255, 255) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt32ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsInt32ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt32ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsInt32ToBytesLE(4294967296L);
  }

  // unsInt64ToBytesLE

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void unsInt64ToBytesLE_WhenArgumentIsNegative() throws Exception {
    Wire.unsInt64ToBytesLE(-1);
  }

  @Test
  public void unsInt64ToBytesLE() throws Exception {
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("0")),             bytes(0, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("1")),             bytes(1, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("256")),           bytes(0, 1, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("65536")),         bytes(0, 0, 1, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("16777216")),      bytes(0, 0, 0, 1, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("4294967296")),    bytes(0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("1099511627776")), bytes(0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("281474976710656")), bytes(0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("72057594037927936")), bytes(0, 0, 0, 0, 0, 0, 0, 1) );
    assertEquals( Wire.unsInt64ToBytesLE(new BigInteger("72057594037927937")), bytes(1, 0, 0, 0, 0, 0, 0, 1) );

    assertEquals( Wire.unsInt64ToBytesLE(0),               bytes(0, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(1),               bytes(1, 0, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(256),             bytes(0, 1, 0, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(65536),           bytes(0, 0, 1, 0, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(16777216),        bytes(0, 0, 0, 1, 0, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(4294967296L),     bytes(0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(1099511627776L),  bytes(0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(281474976710656L),bytes(0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsInt64ToBytesLE(Long.MAX_VALUE),  bytes(255, 255, 255, 255, 255, 255, 255, 127) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt64ToBytesLE_WhenValueIsToSmall() throws Exception {
    Wire.unsInt64ToBytesLE(new BigInteger("-1"));
    Wire.unsInt64ToBytesLE(-1);
  }

  @Test( expectedExceptions = IllegalArgumentException.class )
  public void unsInt64ToBytesLE_WhenValueToLarge() throws Exception {
    Wire.unsInt64ToBytesLE(new BigInteger("18446744073709551616"));
  }

  // unsIntToVarBytes

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void unsInt64ToVarBytes_WhenArgumentIsNegative() throws Exception {
    Wire.unsIntToVarBytes(-1);
  }

  @Test
  public void unsInt64ToVarBytes() throws Exception {
    // 1 byte (uint8_t)
    assertEquals( Wire.unsIntToVarBytes(0),               bytes(0) );
    assertEquals( Wire.unsIntToVarBytes(128),             bytes(128) );
    assertEquals( Wire.unsIntToVarBytes(252),             bytes(252) );

    // 3 bytes (253 + uint16_t)
    assertEquals( Wire.unsIntToVarBytes(253),             bytes(253, 253, 0) );
    assertEquals( Wire.unsIntToVarBytes(254),             bytes(253, 254, 0) );
    assertEquals( Wire.unsIntToVarBytes(255),             bytes(253, 255, 0) );
    assertEquals( Wire.unsIntToVarBytes(256),             bytes(253, 0, 1) );
    assertEquals( Wire.unsIntToVarBytes(65535),           bytes(253, 255, 255) );

    // 5 bytes (254 + uint32_t)
    assertEquals( Wire.unsIntToVarBytes(65536),           bytes(254, 0, 0, 1, 0) );
    assertEquals( Wire.unsIntToVarBytes(65537),           bytes(254, 1, 0, 1, 0) );
    assertEquals( Wire.unsIntToVarBytes(16777216),        bytes(254, 0, 0, 0, 1) );
    assertEquals( Wire.unsIntToVarBytes(16777217),        bytes(254, 1, 0, 0, 1) );
    assertEquals( Wire.unsIntToVarBytes(4294967295L),     bytes(254, 255, 255, 255, 255) );
    assertEquals( Wire.unsIntToVarBytes(2147483647L),     bytes(254, 255, 255, 255, 127) );

    // 9 bytes (255 + uint64_t)
    assertEquals( Wire.unsIntToVarBytes(4294967296L),          bytes(255, 0, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsIntToVarBytes(4294967297L),          bytes(255, 1, 0, 0, 0, 1, 0, 0, 0) );
    assertEquals( Wire.unsIntToVarBytes(1099511627776L),       bytes(255, 0, 0, 0, 0, 0, 1, 0, 0) );
    assertEquals( Wire.unsIntToVarBytes(281474976710656L),     bytes(255, 0, 0, 0, 0, 0, 0, 1, 0) );
    assertEquals( Wire.unsIntToVarBytes(72057594037927936L),   bytes(255, 0, 0, 0, 0, 0, 0, 0, 1) );
    assertEquals( Wire.unsIntToVarBytes(9223372036854775807L), bytes(255, 255, 255, 255, 255, 255, 255, 255, 127) );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void unsIntVarSizeInBytes_WhenArgumentIsNegative() throws Exception {
    Wire.unsIntVarSizeInBytes(-1);
  }

  @Test
  public void unsIntVarSizeInBytes() throws Exception {
    System.out.println(Debug.bytesToHex(Wire.unsIntToVarBytes(556)));
    assertEquals( Wire.unsIntVarSizeInBytes(0), 1 );
    assertEquals( Wire.unsIntVarSizeInBytes(252), 1 );
    assertEquals( Wire.unsIntVarSizeInBytes(253), 3 );
    assertEquals( Wire.unsIntVarSizeInBytes(65535), 3 );
    assertEquals( Wire.unsIntVarSizeInBytes(65536), 5 );
    assertEquals( Wire.unsIntVarSizeInBytes(4294967295L), 5 );
    assertEquals( Wire.unsIntVarSizeInBytes(4294967296L), 9 );
    assertEquals( Wire.unsIntVarSizeInBytes(9223372036854775807L), 9 );
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

  @Test
  public void reverseBytesInPlace() {
    byte[] bts = bytes(0, 255, 127, 128);
    Wire.reverseBytesInPlace(bts);
    assertEquals(bts, bytes(128, 127, 255, 0));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void reverseBytesInPlace_WhenGotEvenArray() {
    Wire.reverseBytesInPlace(bytes(0, 255, 127, 128, 33));
  }

  @Test
  public void reverseBytes() {
    assertEquals(Wire.reverseBytes(bytes(0, 255, 127, 128)), bytes(128, 127, 255, 0));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void reverseBytes_WhenGotEvenArray() {
    Wire.reverseBytes(bytes(0, 255, 127, 128, 33));
  }

}
