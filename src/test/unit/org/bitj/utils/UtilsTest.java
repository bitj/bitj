package org.bitj.utils;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UtilsTest extends BaseTest {

  @Test
  public void currentUnixTimestamp() throws Exception {
    long asOfWriting = 1404149532L;  // ~ June 30, 2014
    long tenYears = 10L * 365 * 24 * 3600;
    long timestamp = Utils.currentUnixTimestamp();
    assertTrue(timestamp > asOfWriting - tenYears);
    assertTrue(timestamp < asOfWriting + tenYears);
  }

  @Test
  public void getBytesASCII() throws Exception {
    byte[] bts = Utils.getBytesASCII("Adam Nowak 0!");
    assertEquals(bts, bytes("41 64 61 6D 20 4E 6F 77 61 6B 20 30 21"));
  }

  @Test
  public void getBytesUTF8() throws Exception {
    byte[] bts = Utils.getBytesUTF8("Adam Nowak ąćęłńóśżź 0!");
    assertEquals(bts, bytes("41 64 61 6D 20 4E 6F 77 61 6B 20 C4 85 C4 87 C4 99 C5 82 C5 84 C3 B3 C5 9B C5 BC C5 BA 20 30 21"));
  }

}
