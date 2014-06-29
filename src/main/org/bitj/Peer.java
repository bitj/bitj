package org.bitj;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Peer {

  private static Logger log = Logger.getLogger(Peer.class.getName());

  private Socket socket;
  private OutputStream out;
  private InputStream in;

  private VersionMessage version;

  public Peer() throws SocketException {
    createSocket();
  }

  public void connect(InetAddress address, int port) throws IOException, IncompatibleProtocolVersion {
    try {
      socket.connect(new InetSocketAddress(address, port));
      out = new BufferedOutputStream(socket.getOutputStream());
      in = new BufferedInputStream(socket.getInputStream());
      sendVersion();
      receiveVersion();
      sendVerack();
      receiveVerack();
    } finally {
      closeSocket();
    }
  }

  private void sendVersion() throws IOException {
    VersionMessage myVersion = new VersionMessage.Builder().get();
    System.out.println("Sending: " + myVersion);
    myVersion.serialize(out);
  }

  private void receiveVersion() throws IOException, IncompatibleProtocolVersion {
    Message message = Message.deserialize(in);
    if (!(message instanceof VersionMessage))
      throw new ProtocolException("Expected VersionMessage, got " + message.toString());
    version = (VersionMessage) message;
    System.out.println("Received: " + version);
  }

  private void sendVerack() throws IOException {
    VerackMessage verack = VerackMessage.getInstance();
    System.out.println("Sending: " + verack);
    verack.serialize(out);
  }

  private void receiveVerack() throws IOException {
    Message message = Message.deserialize(in);
    if (!(message instanceof VerackMessage))
      throw new ProtocolException("Expected VerackMessage, got " + message.toString());
    System.out.println("Received: " + message);
  }

  private void createSocket() throws SocketException {
    socket = new Socket();
    socket.setSoTimeout(1000);
    socket.setKeepAlive(true);
  }

  private void closeSocket() {
    try {
      socket.close();
    } catch (IOException e) {
      log.warning("Socket closed unclean: " + e.getMessage());
    }
  }

}
