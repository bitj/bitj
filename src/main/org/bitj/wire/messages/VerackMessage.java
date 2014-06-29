package org.bitj.wire.messages;

import com.google.common.base.Objects;
import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

public class VerackMessage extends Message {

  private static VerackMessage instance;

  @Override
  public String name() {
    return "verack";
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return new byte[] {};
  }

  public static VerackMessage deserializePayload(BitcoinInputStream in) throws IOException {
    return getInstance();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this.getClass()).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31;
  }

  private VerackMessage() {}

  public static synchronized VerackMessage getInstance() {
    if (instance == null)
      instance = new VerackMessage();
    return instance;
  }

}
