package org.bitj;

import org.bitj.wire.messages.*;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class Peer {

  static enum ClientState { AWAITING_VERSION, AWAITING_VERACK }

  private static final long SLEEP_TIME = 100;

  private static Logger log = Logger.getLogger(Peer.class.getName());

  private InetSocketAddress socketAddress;
  private Socket socket;
  private InputStream in;
  private OutputStream out;

  private VersionMessage version;

  private Queue jobs = new ConcurrentLinkedQueue<String>();

  public Peer(InetAddress address, int port) throws SocketException {
    socketAddress = new InetSocketAddress(address, port);
    socket = new Socket();
    socket.setSoTimeout(10000);
    socket.setKeepAlive(true);
  }

  public void connect() throws IOException, InterruptedException {
    try {
      connectToSocket();
      handshake();
      eventLoop();
    } finally {
      closeSocket();
    }
  }

  private void connectToSocket() throws IOException {
    socket.connect(socketAddress);
    out = new BufferedOutputStream(socket.getOutputStream());
    in = new BufferedInputStream(socket.getInputStream());
  }

  private void handshake() throws IOException {
    version = new ClientHandshaker(in, out).handshake();
  }

  public void eventLoop() throws IOException, InterruptedException {
    while (true) {
      if (messageAvailable())
        handleMessage();
      if (jobAvailable())
        handleJob();
      Thread.sleep(SLEEP_TIME);
    }
  }

  private boolean messageAvailable() throws IOException {
    return in.available() > 0;
  }

  private void handleMessage() throws IOException {
    Message msg = Message.deserialize(in);
    handle(msg);
  }

  private boolean jobAvailable() {
    return jobs.size() > 0;
  }

  private void handleJob() {
    Object job = jobs.remove();
    handle(job);
  }

  private void handle(Message msg) throws ProtocolException {
    // Peer is a client
    if (msg instanceof VersionMessage)
      throw new ProtocolException("Unexpected VersionMessage after handshake: " + msg.toString());
    if (msg instanceof InvMessage)
      ignore(msg);
  }

  private void handle(Object job) {
    // TODO: implement
  }

  private void ignore(Message msg) {
    System.out.println("Ignoring: " + msg.toString());
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
