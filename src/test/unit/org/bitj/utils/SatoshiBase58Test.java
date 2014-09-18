package org.bitj.utils;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SatoshiBase58Test extends BaseTest {

  @Test
  public void encode() throws Exception {
    assertEncoded("", "");
    assertEncoded("61", "2g");
    assertEncoded("626262", "a3gV");
    assertEncoded("636363", "aPEr");
    assertEncoded("73696d706c792061206c6f6e6720737472696e67", "2cFupjhnEsSn59qHXstmK2ffpLv2");
    assertEncoded("00eb15231dfceb60925886b67d065299925915aeb172c06647", "1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L");
    assertEncoded("516b6fcd0f", "ABnLTmg");
    assertEncoded("bf4f89001e670274dd", "3SEo3LWLoPntC");
    assertEncoded("572e4794", "3EFU7m");
    assertEncoded("ecac89cad93923c02321", "EJDM8drfXA6uyA");
    assertEncoded("10c8511e", "Rt5zm");
    assertEncoded("00000000000000000000", "1111111111");
    assertEncoded("002F07577E7C3A2124FDCB7C42633956FB12A05FD85BAF2D84", "15HfZ8d5NmPyB3CZfnFKatV9mzcR9DR92j");
    assertEncoded("0027D0D3B5DF3D76DBFCE226AF56EAE71E154FA6F64D6783FD", "14dXX33isaUsQQcbWa7qqJAQFQwurw1u3z");
  }

  @Test
  public void decode() throws Exception {
    assertDecoded("", "");
    assertDecoded("2g", "61");
    assertDecoded("a3gV", "626262");
    assertDecoded("aPEr", "636363");
    assertDecoded("2cFupjhnEsSn59qHXstmK2ffpLv2", "73696d706c792061206c6f6e6720737472696e67");
    assertDecoded("1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L", "00eb15231dfceb60925886b67d065299925915aeb172c06647");
    assertDecoded("ABnLTmg", "516b6fcd0f");
    assertDecoded("3SEo3LWLoPntC", "bf4f89001e670274dd");
    assertDecoded("3EFU7m", "572e4794");
    assertDecoded("EJDM8drfXA6uyA", "ecac89cad93923c02321");
    assertDecoded("Rt5zm", "10c8511e");
    assertDecoded("1111111111", "00000000000000000000");
    assertDecoded("15HfZ8d5NmPyB3CZfnFKatV9mzcR9DR92j", "002F07577E7C3A2124FDCB7C42633956FB12A05FD85BAF2D84");
    assertDecoded("14dXX33isaUsQQcbWa7qqJAQFQwurw1u3z", "0027D0D3B5DF3D76DBFCE226AF56EAE71E154FA6F64D6783FD");
  }

  private void assertEncoded(String hex, String encoded) {
    assertEquals(SatoshiBase58.encode(bytes(hex)), encoded);
  }

  private void assertDecoded(String encoded, String hex) {
    assertEquals(SatoshiBase58.decode(encoded), bytes(hex));
  }

}
