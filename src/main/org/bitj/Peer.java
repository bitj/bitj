package org.bitj;

import org.bitj.wire.messages.*;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * THIS IS MOSTLY THROW-AWAY CODE USED TO TEST AND TRY MESSAGE SERIALIZATION "IN PRACTICE" (ON A TRUE CONNECTION)
 */
public class Peer {

  static enum ClientState { NEUTRAL, AWAITING_VERSION, AWAITING_VERACK, AWAITING_INV }

  private static final long SLEEP_TIME = 100;

  private static Logger log = Logger.getLogger(Peer.class.getName());

  private InetSocketAddress socketAddress;
  private Socket socket;
  private InputStream in;
  private OutputStream out;

  private VersionMessage version;

  private Queue<String> jobs = new ConcurrentLinkedQueue<>();

  private ClientState state = ClientState.NEUTRAL;

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
      if (state == ClientState.NEUTRAL && jobAvailable())
        handleJob();
      //Thread.sleep(SLEEP_TIME);
    }
  }

  private boolean messageAvailable() throws IOException {
    return in.available() > 0;
  }

  private void handleMessage() throws IOException {
    Message msg = Message.deserialize(in);
    // Peer is a client
    if (msg instanceof VersionMessage)
      throw new ProtocolException("Unexpected VersionMessage after handshake: " + msg.toString());
    if (msg instanceof VerackMessage)
      throw new ProtocolException("Unexpected VerackMessage after handshake: " + msg.toString());
    if (msg instanceof InvMessage)
      if (state == ClientState.AWAITING_INV)
        handle((InvMessage) msg);
      else
        ignore(msg);
    if (msg instanceof BlockMessage)
      handle((BlockMessage) msg);
  }

  private boolean jobAvailable() {
    return jobs.size() > 0;
  }

  private void handleJob() throws IOException {
    handle(jobs.remove());
  }

  private void handle(Object job) throws IOException {
    if (job instanceof String && job.equals(DOWNLOAD_BLOCKCHAIN)) {
      new BlockchainDownloader(in, out).start();
    }
  }

  private void ignore(Message msg) {
    System.out.println("Ignoring: " + msg.toString());
  }

  public void queueJobDownloadBlockchain() {
    jobs.add(DOWNLOAD_BLOCKCHAIN);
  }

  private void handle(InvMessage inv) throws IOException {
    GetDataMessage getdata = new GetDataMessage(inv.getInvItems());
    System.out.println("Sending: " + getdata);
    getdata.serialize(out);
  }

  private void handle(BlockMessage blockMsg) throws IOException {
    System.out.println(blockMsg.getBlock().getNonce());
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

  public static final String DOWNLOAD_BLOCKCHAIN = "download-blockchain";

}
