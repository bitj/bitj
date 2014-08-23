package org.bitj;

import com.google.common.collect.ImmutableList;
import org.bitj.persistance.Gateway;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {

  private Gateway gateway;
  private List<Block> chain = new ArrayList<>();

  public Blockchain(Gateway gateway) {
    this.gateway = gateway;
  }

  public void load() throws SQLException {
    this.chain = gateway.getBlocks();
  }

  public Block getGenesisBlock() {
    return chain.get(0);
  }

  public Block getLastBlock() {
    return chain.get(getHeight() - 1);
  }

  public int getHeight() {
    return chain.size();
  }

  public ImmutableList<Sha256Hash> getDefaultBlockLocator() {
    return getBlockLocator(10);
  }

  public ImmutableList<Sha256Hash> getBlockLocator(int numberOfLastConsecutive) {
    // To create the block locator hashes, keep pushing hashes until you go back to the genesis block.
    // After pushing 10 last known hashes the step backwards doubles every loop.
    ImmutableList.Builder<Sha256Hash> builder = ImmutableList.builder();
    int step = 1, loopPass = 1;
    for (int blockIndex = getHeight()-1; blockIndex > 0; blockIndex -= step, ++loopPass) {
      if (loopPass >= numberOfLastConsecutive) step *= 2;
      Block block = chain.get(blockIndex);
      builder.add(block.hash);
    }
    builder.add(getGenesisBlock().hash);
    return builder.build();
  }

}
