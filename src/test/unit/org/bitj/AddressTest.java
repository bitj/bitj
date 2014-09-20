package org.bitj;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AddressTest extends BaseTest {

  /**
   * Illegal adddresses.
   */

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void nullPassed() throws Exception {
    new Address(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void emptyString() throws Exception {
    new Address(" ");
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Illegal char.*")
  public void invalidSyntax() throws Exception {
    new Address("12≈j2Rufk8GG8QMnfdHtoAYt8YCaYaNSjL");
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Illegal char.*")
  public void nonSatoshiBase58() throws Exception {
    new Address("12Ij2Rufk8GG8QMnfdHtoAYt8YCaYaNSjL");
                // ^ the "I" is illegal in Satoshi BASE58
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Unknown version 255.*")
  public void unknownVersionPrefix_althoughValidChecksum() throws Exception {
    // this has a version prefix of *255* which is illegal in Bitcoin
    new Address("2mduY77v8o8RgfvusucEAT8xVdAu7s64Aqq");
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*got 1.*")
  public void tooShort_althoughValidChecksum() throws Exception {
    // this has only 1 byte instead of 20
    new Address("135ADBrc");
  }

  /**
   * Instantiating different kinds of addresses.
   */

  // To create p2sh tests the following address generator has been used: http://maraoz.github.io/multipaper

  @Test
  public void constructs_mainnet_p2pubkey_addressFromEncodedString() throws Exception {
    // Example from blockexplorer: http://blockexplorer.com/address/12ij2Rufk8GG8QMnfdHtoAYt8YCaYaNSjL
    assertDecodedAs("12ij2Rufk8GG8QMnfdHtoAYt8YCaYaNSjL", 0, "12dc42b84d223cbfdc26bfc7e02d7173413f27ea");
    // Example from forum: https://bitcointalk.org/index.php?topic=90982.0
    assertDecodedAs("1111111111111111111114oLvT2", 0, "0000000000000000000000000000000000000000");
    // Example from: https://en.bitcoin.it/wiki/Technical_background_of_Bitcoin_addresses
    assertDecodedAs("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM", 0, "010966776006953D5567439E5E39F86A0D273BEE");
    // Example from bitcoinj AddressTest
    assertDecodedAs("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL", 0, "4a22c3c4cbb31e4d03b15550636762bda0baf85a");
  }

  @Test
  public void constructs_mainnet_p2sh_addressFromEncodedString() throws Exception {
    // Example from blockexplorer: http://blockexplorer.com/address/3CZX2FWjdrBkRbnVuC5V1WMcvCm9mZFbFG
    assertDecodedAs("3CZX2FWjdrBkRbnVuC5V1WMcvCm9mZFbFG", 5, "773e5896b30637db65e46326c6c553942d20bf0f");
    // Example from blockexplorer: http://blockexplorer.com/address/3QpeeHbjNcQoifUaA2pugX5ij6Yyk6vmx5
    assertDecodedAs("3QpeeHbjNcQoifUaA2pugX5ij6Yyk6vmx5", 5, "fdbc74f2db1613f748249e53c0706c544364609c");
  }

  @Test
  public void constructs_testnet_p2pubkey_addressFromEncodedString() throws Exception {
    // Example from blockexplorer: http://blockexplorer.com/testnet/address/mpTp4oQhYSV8iSTBYny2h9tbnGUAL5VMp1
    assertDecodedAs("mpTp4oQhYSV8iSTBYny2h9tbnGUAL5VMp1", 111, "622103eec2803903b205372af9d95ee95efc8814");
    // Example from bitcoinj AddressTest
    assertDecodedAs("n4eA2nbYqErp7H6jebchxAN59DmNpksexv", 111, "fda79a24e50ff70ff42f7d89585da5bd19d9e5cc");
  }

  @Test
  public void constructs_testnet_p2sh_addressFromEncodedString() throws Exception {
    // Example from blockexplorer: http://explorer.chain.com/addresses/2NE4s6dR6AnGAkDGmLH8bYjuYxcdf26GSLE
    assertDecodedAs("2NE4s6dR6AnGAkDGmLH8bYjuYxcdf26GSLE", 196, "E4650B11C3123A84081BFE843905310A6849615C");
    // Example from blockexplorer: http://explorer.chain.com/addresses/2NBKcCHEsE12xTv1ug3TNAuFBarUTn4DkWL
    assertDecodedAs("2NBKcCHEsE12xTv1ug3TNAuFBarUTn4DkWL", 196, "C646771D798891BED4F36D28BA825DE865D44654");
  }

  private void assertDecodedAs(String encoded, int version, String hex) {
    Address address = new Address(encoded);
    assertEquals(address.getVersion(), version);
    assertEquals(address.getHash160Bytes(), bytes(hex));
  }

  /**
   * Encoding different kinds of addresses to human readable form.
   */

  @Test
  public void toString_mainnet_p2pubkey() throws Exception {
    assertEncodedAs(0, "12dc42b84d223cbfdc26bfc7e02d7173413f27ea", "12ij2Rufk8GG8QMnfdHtoAYt8YCaYaNSjL");
    assertEncodedAs(0, "0000000000000000000000000000000000000000", "1111111111111111111114oLvT2");
    assertEncodedAs(0, "010966776006953D5567439E5E39F86A0D273BEE", "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM");
    assertEncodedAs(0, "4a22c3c4cbb31e4d03b15550636762bda0baf85a", "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
  }

  @Test
  public void toString_mainnet_p2sh() throws Exception {
    assertEncodedAs(5, "773e5896b30637db65e46326c6c553942d20bf0f", "3CZX2FWjdrBkRbnVuC5V1WMcvCm9mZFbFG");
    assertEncodedAs(5, "fdbc74f2db1613f748249e53c0706c544364609c", "3QpeeHbjNcQoifUaA2pugX5ij6Yyk6vmx5");
  }

  @Test
  public void toString_testnet_p2pubkey() throws Exception {
    assertEncodedAs(111, "622103eec2803903b205372af9d95ee95efc8814", "mpTp4oQhYSV8iSTBYny2h9tbnGUAL5VMp1");
    assertEncodedAs(111, "fda79a24e50ff70ff42f7d89585da5bd19d9e5cc", "n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
  }

  @Test
  public void toString_testnet_p2sh() throws Exception {
    assertEncodedAs(196, "E4650B11C3123A84081BFE843905310A6849615C", "2NE4s6dR6AnGAkDGmLH8bYjuYxcdf26GSLE");
    assertEncodedAs(196, "C646771D798891BED4F36D28BA825DE865D44654", "2NBKcCHEsE12xTv1ug3TNAuFBarUTn4DkWL");
  }

  private void assertEncodedAs(int version, String hex160, String encoded) {
    Address address = new Address(version, bytes(hex160));
    assertEquals(address.toString(), encoded);
  }

}
