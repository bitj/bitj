package org.bitj;

import org.bitj.wire.messages.Msg;
import org.bitj.wire.messages.VerackMsg;
import org.bitj.wire.messages.VersionMsg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;

public class ClientHandshaker {

  InputStream in;
  OutputStream out;
  VersionMsg version;

  public ClientHandshaker(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public VersionMsg handshake() throws IOException {
    sendVersion();
    receiveVersion();
    sendVerack();
    receiveVerack();
    return version;
  }

  private void sendVersion() throws IOException {
    VersionMsg myVersion = new VersionMsg.Builder().get();
    myVersion.serialize(out);
  }

  private void receiveVersion() throws IOException {
    Msg msg = Msg.deserialize(in);
    if (!(msg instanceof VersionMsg))
      throw new ProtocolException("Expected VersionMsg, got " + msg.toString());
    version = (VersionMsg) msg;
  }

  private void sendVerack() throws IOException {
    VerackMsg verack = VerackMsg.getInstance();
    verack.serialize(out);
  }

  private void receiveVerack() throws IOException {
    Msg msg = Msg.deserialize(in);
    if (!(msg instanceof VerackMsg))
      throw new ProtocolException("Expected VerackMsg, got " + msg.toString());
  }

}
