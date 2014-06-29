package org.bitj.wire.messages;

import org.bitj.BaseTest;
import org.bitj.wire.messages.GetAddrMessage;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetAddrMessageTest extends BaseTest {

  @Test
  public void serializePayload() throws Exception {
    assertEquals(GetAddrMessage.getInstance().serializePayload(), bytes());
  }

  @Test
  public void deserializePayload() throws IOException {
    assertEquals(GetAddrMessage.deserializePayload(bitcoinStream()), GetAddrMessage.getInstance());
  }

  @Test
  public void equals() throws Exception {
    assertEquals(GetAddrMessage.getInstance(), GetAddrMessage.getInstance());
  }

  @Test
  public void hashcode() throws Exception {
    assertEquals(GetAddrMessage.getInstance().hashCode(), GetAddrMessage.getInstance().hashCode());
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(GetAddrMessage.getInstance().toString().contains("GetAddrMessage{"));
  }

}
