package org.bitj;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AmountTest {

  @Test
  public void satoshi() throws Exception {
    assertEquals(new Amount(314000000L).satoshi(), 314000000L);
    assertEquals(new Amount(-314000000L).satoshi(), -314000000L);
    assertEquals(new Amount(0).satoshi(), 0);
  }

  @Test
  public void coins() throws Exception {
    assertEquals(new Amount(314000000L).coins(), 3);
    assertEquals(new Amount(-314000000L).coins(), -3);
    assertEquals(new Amount(100000000-1).coins(), 0);
  }

  @Test
  public void remainder() throws Exception {
    assertEquals(new Amount(314000000L).remainder(), 14000000L);     //  3.14 BTC
    assertEquals(new Amount(-314000000L).remainder(), -14000000L);   // -3.14 BTC
    assertEquals(new Amount(99999999L).remainder(),  99999999L);     // almost 1 BTC
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertFormatted( 100000000L,  "1");
    assertFormatted(         1L,  "0.00000001");
    assertFormatted( 110000000L,  "1.1");
    assertFormatted(  99999999L,  "0.99999999");
    assertFormatted( 100000001L,  "1.00000001");
    assertFormatted(2100000001L, "21.00000001");

    assertFormatted( -100000000L,  "-1");
    assertFormatted(         -1L,  "-0.00000001");
    assertFormatted( -110000000L,  "-1.1");
    assertFormatted(  -99999999L,  "-0.99999999");
    assertFormatted( -100000001L,  "-1.00000001");
    assertFormatted(-2100000001L, "-21.00000001");
  }

  private void assertFormatted(long satoshi, String formatted) {
    assertEquals(new Amount(satoshi).toString(), formatted);
  }

}
