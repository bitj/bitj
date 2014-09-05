package org.bitj.wire.messages;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class VerackMsgTest extends BaseTest {

  @Test
  public void serializePayload() throws Exception {
    assertEquals(VerackMsg.getInstance().serializePayload(), bytes());
  }

  @Test
  public void deserializePayload() throws IOException {
    assertEquals(VerackMsg.deserializePayload(bitcoinStream()), VerackMsg.getInstance());
  }

  @Test
  public void equals() throws Exception {
    assertEquals(VerackMsg.getInstance(), VerackMsg.getInstance());
  }

  @Test
  public void hashcode() throws Exception {
    assertEquals(VerackMsg.getInstance().hashCode(), VerackMsg.getInstance().hashCode());
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(VerackMsg.getInstance().toString().contains("VerackMsg{"));
  }

}
