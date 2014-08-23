package org.bitj.utils;

import org.bitj.BaseTest;
import org.bitj.utils.Debug;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DebugTest extends BaseTest {

  @Test
  public void bytesToString() throws Exception {

  }

  @Test
  public void hexToBytes() throws Exception {
    assertEquals( Debug.hexToBytes("00"), bytes(0) );
    assertEquals( Debug.hexToBytes("AF"), bytes(175) );
    assertEquals( Debug.hexToBytes("af"), bytes(175) );
    assertEquals( Debug.hexToBytes("00 F9 BE B4 D9 00"), bytes(0, 249, 190, 180, 217, 0) );
  }

  @Test
  public void bytesToHex() throws Exception {
    assertEquals( Debug.bytesToHex(bytes(0)), "00" );
    assertEquals( Debug.bytesToHex(bytes(128, 255)), "80 ff" );
    assertEquals( Debug.bytesToHex(bytes(0, 249, 190, 180, 217, 0)), "00 f9 be b4 d9 00" );
  }

}
