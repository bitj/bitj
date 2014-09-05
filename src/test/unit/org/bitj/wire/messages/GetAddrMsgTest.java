package org.bitj.wire.messages;

import org.bitj.BaseTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetAddrMsgTest extends BaseTest {

  @Test
  public void serializePayload() throws Exception {
    assertEquals(GetAddrMsg.getInstance().serializePayload(), bytes());
  }

  @Test
  public void deserializePayload() throws IOException {
    assertEquals(GetAddrMsg.deserializePayload(bitcoinStream()), GetAddrMsg.getInstance());
  }

  @Test
  public void equals() throws Exception {
    assertEquals(GetAddrMsg.getInstance(), GetAddrMsg.getInstance());
  }

  @Test
  public void hashcode() throws Exception {
    assertEquals(GetAddrMsg.getInstance().hashCode(), GetAddrMsg.getInstance().hashCode());
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(GetAddrMsg.getInstance().toString().contains("GetAddrMsg{"));
  }

}
