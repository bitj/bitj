package org.bitj;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.testng.Assert.assertEquals;

public class BitcoinOutputStreamTest extends BaseTest {

  // !!! BitcoinOutputStream delegates all calls to the Wire. See the WireTest for tests !!!

  @Test
  public void toByteArray() throws Exception {
    byte[] expectedBytes = bytes(255, 100, 0, 173);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    byteOut.write(expectedBytes);
    BitcoinOutputStream out = new BitcoinOutputStream(byteOut);
    assertEquals(out.toByteArray(), expectedBytes);
  }

}
