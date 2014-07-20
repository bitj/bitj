package org.bitj;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    InetAddress localhost = InetAddress.getByName("127.0.0.1");
    Peer peer = new Peer(localhost, 8333);
    peer.connect();
  }

}
