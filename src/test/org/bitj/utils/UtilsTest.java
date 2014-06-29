package org.bitj.utils;

import org.bitj.BaseTest;
import org.bitj.utils.Utils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UtilsTest extends BaseTest {

  @Test
  public void currentUnixTimestamp() throws Exception {
    long asOfWriting = 1404149532L;  // ~ June 30, 2014
    long tenYears = 10L * 365 * 24 * 3600;
    long timestamp = Utils.currentUnixTimestamp();
    assertTrue(timestamp > asOfWriting - tenYears);
    assertTrue(timestamp < asOfWriting + tenYears);
  }

}
