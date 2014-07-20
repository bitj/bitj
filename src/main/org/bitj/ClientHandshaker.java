package org.bitj;

import org.bitj.wire.messages.Message;
import org.bitj.wire.messages.VerackMessage;
import org.bitj.wire.messages.VersionMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;

public class ClientHandshaker {

  InputStream in;
  OutputStream out;
  VersionMessage version;

  public ClientHandshaker(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public VersionMessage handshake() throws IOException {
    sendVersion();
    receiveVersion();
    sendVerack();
    receiveVerack();
    return version;
  }

  private void sendVersion() throws IOException {
    VersionMessage myVersion = new VersionMessage.Builder().get();
    System.out.println("Sending: " + myVersion);
    myVersion.serialize(out);
  }

  private void receiveVersion() throws IOException {
    Message msg = Message.deserialize(in);
    if (!(msg instanceof VersionMessage))
      throw new ProtocolException("Expected VersionMessage, got " + msg.toString());
    version = (VersionMessage) msg;
    System.out.println("Received: " + version);
  }

  private void sendVerack() throws IOException {
    VerackMessage verack = VerackMessage.getInstance();
    System.out.println("Sending: " + verack);
    verack.serialize(out);
  }

  private void receiveVerack() throws IOException {
    Message msg = Message.deserialize(in);
    if (!(msg instanceof VerackMessage))
      throw new ProtocolException("Expected VerackMessage, got " + msg.toString());
    System.out.println("Received: " + msg);
  }

}
