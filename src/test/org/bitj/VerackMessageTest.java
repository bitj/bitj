package org.bitj;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class VerackMessageTest extends BaseTest {

  @Test
  public void serializePayload() throws Exception {
    assertEquals(VerackMessage.getInstance().serializePayload(), bytes());
  }

  @Test
  public void deserializePayload() throws IOException {
    assertEquals(VerackMessage.deserializePayload(bitcoinStream()), VerackMessage.getInstance());
  }

  @Test
  public void equals() throws Exception {
    assertEquals(VerackMessage.getInstance(), VerackMessage.getInstance());
  }

  @Test
  public void hashcode() throws Exception {
    assertEquals(VerackMessage.getInstance().hashCode(), VerackMessage.getInstance().hashCode());
  }

}
