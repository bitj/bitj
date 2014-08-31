package org.bitj.persistance;

import org.bitj.wire.objects.Block;

import java.sql.SQLException;
import java.util.List;

public interface Gateway {

  public List<Block> getBlocks() throws SQLException;

}
