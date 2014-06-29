package org.bitj;

import java.net.ProtocolException;

public class VarStringTooLarge extends ProtocolException {

  public VarStringTooLarge(String msg) {
    super(msg);
  }

}
