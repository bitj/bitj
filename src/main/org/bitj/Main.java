package org.bitj;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

  public static void main(String[] args) throws IOException, IncompatibleProtocolVersion {
    InetAddress localhost = InetAddress.getByName("127.0.0.1");
    //InetAddress localhost = InetAddress.getByName("192.168.1.66");
    Peer peer = new Peer();
    peer.connect(localhost, 8333);
  }

}
