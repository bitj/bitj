package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

import static com.google.common.base.Objects.toStringHelper;

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
    return toStringHelper(this.getClass()).toString();
  }

  private GetAddrMessage() {}

  public static synchronized GetAddrMessage getInstance() {
    if (instance == null)
      instance = new GetAddrMessage();
    return instance;
  }

}
