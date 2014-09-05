package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.objects.Tx;

import java.io.IOException;

public class TxMsg extends Msg {

  private Tx tx;

  @Override
  public String name() {
    return "tx";
  }

  public TxMsg(Tx tx) {
    this.tx = tx;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return tx.serialize();
  }

  public static TxMsg deserializePayload(BitcoinInputStream in) throws IOException {
    return new TxMsg(Tx.deserialize(in));
  }

  @Override
  public String toString() {
    return tx.toString().replace("Tx{", "TxMsg{");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxMsg that = (TxMsg) o;
    return this.tx.equals(that.tx);
  }

  @Override
  public int hashCode() {
    return tx.hashCode();
  }

}
