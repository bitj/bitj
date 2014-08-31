package org.bitj.utils;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CryptoTest extends BaseTest {

  @Test
  public void bitcoinChecksum() throws Exception {
    assertEquals(Crypto.bitcoinChecksum(bytes()), bytes("5D F6 E0 E2"));
    assertEquals(Crypto.bitcoinChecksum(bytes(0, 1, 255)), bytes("0A 93 E5 F6"));
  }

  @Test
  public void bitcoinHash() throws Exception {
    // Example from https://en.bitcoin.it/wiki/Protocol_specification#Hashes
    byte[] bytes = Utils.getBytesASCII("hello");
    assertEquals(Crypto.bitcoinHash(bytes), bytes("9595C9DF90075148EB06860365DF33584B75BFF782A510C6CD4883A419833D50"));
  }

}
