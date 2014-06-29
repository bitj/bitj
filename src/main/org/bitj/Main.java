package org.bitj;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

  public static void main(String[] args) throws IOException {
    InetAddress localhost = InetAddress.getByName("127.0.0.1");
    Peer peer = new Peer();
    peer.connect(localhost, 8333);
  }

}
