package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

import static com.google.common.base.Objects.toStringHelper;

public class GetAddrMsg extends Msg {

  private static GetAddrMsg instance;

  @Override
  public String name() {
    return "getaddr";
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return new byte[] {};
  }

  public static GetAddrMsg deserializePayload(BitcoinInputStream in) throws IOException {
    return getInstance();
  }

  @Override
  public String toString() {
    return toStringHelper(this.getClass()).toString();
  }

  private GetAddrMsg() {}

  public static synchronized GetAddrMsg getInstance() {
    if (instance == null)
      instance = new GetAddrMsg();
    return instance;
  }

}
