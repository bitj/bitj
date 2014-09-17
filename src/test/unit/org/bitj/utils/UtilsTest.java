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

  @Test
  public void padLeft() throws Exception {
    // TODO: test other cases
    assertEquals(Utils.padLeft(bytes(1, 2, 3), 5), bytes(0, 0, 1, 2, 3));
  }

  @Test
  public void getDifficulty() throws Exception {
    assertEquals(Utils.getDifficulty(0x1d00ffffL).doubleValue(), 1, 0);
    assertEquals(Utils.getDifficulty(0x1b0404cbL).doubleValue(), 16307.420938523983, 0.0000000001);
  }

  @Test
  public void getPaddedTarget_forMaxTargetPossible() throws Exception {
    // Example from:
    // https://en.bitcoin.it/wiki/Difficulty#How_is_difficulty_calculated.3F_What_is_the_difference_between_bdiff_and_pdiff
    assertEquals(Utils.getTargetAs32Bytes(0x1d00ffffL), bytes("00000000FFFF0000000000000000000000000000000000000000000000000000"));
  }

  @Test
  public void getPaddedTarget() throws Exception {
    // TODO: more tests
    // Example from:
    // https://en.bitcoin.it/wiki/Difficulty#How_is_difficulty_stored_in_blocks
    assertEquals(Utils.getTargetAs32Bytes(0x1b0404cbL), bytes("00000000000404CB000000000000000000000000000000000000000000000000"));
  }

}
