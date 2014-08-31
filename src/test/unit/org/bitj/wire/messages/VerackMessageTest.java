package org.bitj.wire.messages;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(VerackMessage.getInstance().toString().contains("VerackMessage{"));
  }

}
