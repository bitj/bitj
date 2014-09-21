package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class GetHeadersMsgTest extends BaseTest {

  /**
   * For tests look into GetBlocksMsgTest as GetHeadersMsg is basically derived and renamed GetBlocksMsg.
   */

  @Test
  public void toStringImplemented() throws Exception {
    ImmutableList<Sha256Hash> emptyBlockLocator = new ImmutableList.Builder<Sha256Hash>().add(Sha256Hash.ZERO).build();
    GetHeadersMsg getHeadersMsg = new GetHeadersMsg(emptyBlockLocator);
    assertTrue(getHeadersMsg.toString().contains("GetHeadersMsg{"));
  }

}
