package org.bitj.persistance;

import org.bitj.Block;
import org.bitj.Sha256Hash;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class H2Gateway implements Gateway {

  private Connection conn;

  public H2Gateway(Connection conn) {
    this.conn = conn;
  }

  @Override
  public List<Block> getBlocks() throws SQLException {
    ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM blocks ORDER BY timestamp;");
    List<Block> blocks = new ArrayList<>();
    while (resultSet.next()) {
      Block block = new Block();
      block.hash = new Sha256Hash(resultSet.getBytes("hash"));
      block.prevHash = new Sha256Hash(resultSet.getBytes("prev_hash"));
      block.mrklRoot = new Sha256Hash(resultSet.getBytes("mrkl_root"));
      block.timestamp = resultSet.getTimestamp("timestamp").getTime() / 1000;
      block.target = resultSet.getBytes("target");
      block.nonce = resultSet.getLong("nonce");
      block.mrklTree = resultSet.getBytes("mrkl_tree");
      blocks.add(block);
    }
    return blocks;
  }

}
