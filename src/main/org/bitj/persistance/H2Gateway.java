package org.bitj.persistance;

import org.bitj.wire.objects.Block;
import org.bitj.Sha256Hash;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
      Block block = new Block.Builder().
        hash(new Sha256Hash(resultSet.getBytes("hash"))).
        prevHash(new Sha256Hash(resultSet.getBytes("prev_hash"))).
        mrklRoot(new Sha256Hash(resultSet.getBytes("mrkl_root"))).
        timestamp(resultSet.getTimestamp("timestamp").getTime() / 1000).
        bits(resultSet.getLong("bits")).
        nonce(resultSet.getLong("nonce")).
        get();
      blocks.add(block);
    }
    return blocks;
  }

}
