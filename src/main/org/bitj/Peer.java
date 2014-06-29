package org.bitj;

import org.bitj.wire.messages.*;

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

  private void createSocket() throws SocketException {
    socket = new Socket();
    socket.setSoTimeout(10000);
    socket.setKeepAlive(true);
  }

  public void connect(InetAddress address, int port) throws IOException {
    try {
      socket.connect(new InetSocketAddress(address, port));
      out = new BufferedOutputStream(socket.getOutputStream());
      in = new BufferedInputStream(socket.getInputStream());
      sendVersion();
      receiveVersion();
      sendVerack();
      receiveVerack();
      sendGetAddr();
      receiveAddr();
    } finally {
      closeSocket();
    }
  }

  private void sendVersion() throws IOException {
    VersionMessage myVersion = new VersionMessage.Builder().get();
    System.out.println("Sending: " + myVersion);
    myVersion.serialize(out);
  }

  private void receiveVersion() throws IOException {
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

  private void sendGetAddr() throws IOException {
    GetAddrMessage msg = GetAddrMessage.getInstance();
    System.out.println("Sending: " + msg);
    msg.serialize(out);
  }

  private void receiveAddr() throws IOException {
    AddrMessage msg = (AddrMessage) Message.deserialize(in);
    System.out.println(msg);
  }

  private void closeSocket() {
    try {
      socket.close();
    } catch (IOException e) {
      log.warning("Socket closed unclean: " + e.getMessage());
    }
  }

}
