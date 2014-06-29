package org.bitj.utils;

import org.bitj.BaseTest;
import org.bitj.utils.Crypto;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CryptoTest extends BaseTest {

  @Test
  public void checksum() throws Exception {
    assertEquals( Crypto.checksum(bytes()), bytes(0x5D, 0xF6, 0xE0, 0xE2) );
    assertEquals( Crypto.checksum(bytes(0, 1, 255)), bytes(0x0A, 0x93, 0xE5, 0xF6) );
  }

}
