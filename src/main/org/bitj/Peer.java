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

  private InetSocketAddress socketAddress;
  private Socket socket;
  private InputStream in;
  private OutputStream out;

  private VersionMsg version;

  private Queue<String> jobs = new ConcurrentLinkedQueue<>();

  private ClientState state = ClientState.NEUTRAL;

  private Logger logger = Logger.getLogger("org.bitj.eventlogger");

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
    Msg msg = Msg.deserialize(in);
    // Peer is a client
    if (msg instanceof VersionMsg)
      throw new ProtocolException("Unexpected VersionMsg after handshake: " + msg.toString());
    if (msg instanceof VerackMsg)
      throw new ProtocolException("Unexpected VerackMsg after handshake: " + msg.toString());
    if (msg instanceof InvMsg)
      if (state == ClientState.AWAITING_INV)
        handle((InvMsg) msg);
      else
        ignore(msg);
    if (msg instanceof BlockMsg)
      handle((BlockMsg) msg);
  }

  private boolean jobAvailable() {
    return jobs.size() > 0;
  }

  private void handleJob() throws IOException {
    handle(jobs.remove());
  }

  private void handle(Object job) throws IOException {
    if (job instanceof String && job.equals(DOWNLOAD_BLOCKCHAIN)) {
      //new BlockchainDownloader(in, out).start();
      new HeadersDownloader(in, out).start();
    }
  }

  private void ignore(Msg msg) {
    logger.finer("Ignoring " + msg.name());
  }

  public void queueJobDownloadBlockchain() {
    jobs.add(DOWNLOAD_BLOCKCHAIN);
  }

  private void handle(InvMsg inv) throws IOException {
    GetDataMsg getdata = new GetDataMsg(inv.getInvItems());
    getdata.serialize(out);
  }

  private void handle(BlockMsg blockMsg) throws IOException {
  }

  private void sendGetAddr() throws IOException {
    GetAddrMsg msg = GetAddrMsg.getInstance();
    msg.serialize(out);
  }

  private void receiveAddr() throws IOException {
    AddrMsg msg = (AddrMsg) Msg.deserialize(in);
  }

  private void closeSocket() {
    try {
      socket.close();
    } catch (IOException e) {
      logger.warning("Socket closed uncleanly: " + e.getMessage());
    }
  }

  public static final String DOWNLOAD_BLOCKCHAIN = "download-blockchain";

}
