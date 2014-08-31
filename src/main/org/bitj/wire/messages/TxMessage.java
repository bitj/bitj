package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.objects.Tx;

import java.io.IOException;

public class TxMessage extends Message {

  private Tx tx;

  @Override
  public String name() {
    return "tx";
  }

  public TxMessage(Tx tx) {
    this.tx = tx;
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return tx.serialize();
  }

  public static TxMessage deserializePayload(BitcoinInputStream in) throws IOException {
    return new TxMessage(Tx.deserialize(in));
  }

  @Override
  public String toString() {
    return tx.toString().replace("Tx{", "TxMessage{");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TxMessage that = (TxMessage) o;
    return this.tx.equals(that.tx);
  }

  @Override
  public int hashCode() {
    return tx.hashCode();
  }

}
