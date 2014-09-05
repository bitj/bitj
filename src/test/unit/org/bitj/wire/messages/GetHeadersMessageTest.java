package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class GetHeadersMessageTest extends BaseTest {

  /**
   * For tests look into GetBlocksMessageTest as GetHeadersMessage is basically derived and renamed GetBlocksMessage.
   */

  @Test
  public void toStringImplemented() throws Exception {
    ImmutableList<Sha256Hash> emptyBlockLocator = new ImmutableList.Builder<Sha256Hash>().build();
    GetHeadersMessage getHeadersMsg = new GetHeadersMessage(emptyBlockLocator);
    assertTrue(getHeadersMsg.toString().contains("GetHeadersMessage{"));
  }

}
