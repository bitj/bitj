package org.bitj.wire.messages;

import org.bitj.wire.BitcoinInputStream;

import java.io.IOException;

import static com.google.common.base.Objects.toStringHelper;

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
    return toStringHelper(this.getClass()).toString();
  }

  private VerackMessage() {}

  public static synchronized VerackMessage getInstance() {
    if (instance == null)
      instance = new VerackMessage();
    return instance;
  }

}
