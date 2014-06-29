package org.bitj.wire.messages;

import com.google.common.base.Objects;
import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

public class GetAddrMessage extends Message {

  private static GetAddrMessage instance;

  @Override
  public String name() {
    return "getaddr";
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return new byte[] {};
  }

  public static GetAddrMessage deserializePayload(BitcoinInputStream in) throws IOException {
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

  private GetAddrMessage() {}

  public static synchronized GetAddrMessage getInstance() {
    if (instance == null)
      instance = new GetAddrMessage();
    return instance;
  }

}