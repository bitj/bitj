package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

import static com.google.common.base.Objects.toStringHelper;

public class VerackMsg extends Msg {

  private static VerackMsg instance;

  @Override
  public String name() {
    return "verack";
  }

  @Override
  public byte[] serializePayload() throws IOException {
    return new byte[] {};
  }

  public static VerackMsg deserializePayload(BitcoinInputStream in) throws IOException {
    return getInstance();
  }

  @Override
  public String toString() {
    return toStringHelper(this.getClass()).toString();
  }

  private VerackMsg() {}

  public static synchronized VerackMsg getInstance() {
    if (instance == null)
      instance = new VerackMsg();
    return instance;
  }

}
