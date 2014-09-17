package org.bitj;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bitj.persistance.Gateway;
import org.bitj.wire.objects.Block;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class BlockchainTest extends BaseTest {

  static Sha256Hash HASH0 = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000000");
  static Sha256Hash HASH1 = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000001");
  static Sha256Hash HASH2 = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000002");
  static Sha256Hash HASH3 = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000003");
  static Sha256Hash HASH4 = new Sha256Hash("0000000000000000000000000000000000000000000000000000000000000004");

  @Test
  public void returnsBlockLocatorWithSpecifiedConsecutiveBlocks() throws Exception {
    Gateway gatewayStub = new Gateway() {
      @Override
      public List<Block> getBlocks() throws SQLException {
        return Lists.newArrayList(blockWith(HASH0), blockWith(HASH1), blockWith(HASH2), blockWith(HASH3), blockWith(HASH4));
      }
    };
    Blockchain blockchain = new Blockchain(gatewayStub);
    blockchain.load();
    ImmutableList<Sha256Hash> blockLocator = blockchain.getBlockLocator(2);
    assertEquals(blockLocator.size(), 4);
    assertEquals(blockLocator.get(0), HASH4);
    assertEquals(blockLocator.get(1), HASH3);
    assertEquals(blockLocator.get(2), HASH1);
    assertEquals(blockLocator.get(3), HASH0);
  }

  private Block blockWith(Sha256Hash hash) {
    return new Block.Builder().hash(hash).prevHash(Sha256Hash.ZERO).mrklRoot(Sha256Hash.ZERO).compactTarget(0).nonce(0).get();
  }

}
