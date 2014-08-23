package org.bitj;

import org.bitj.persistance.H2Gateway;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class App {

  private static final String DB_FILE_PATH = "~/.bitj/db/db";
  private static App instance;

  private Db db;
  private Peer peer;
  private Blockchain blockchain;

  public static synchronized App getInstance() {
    if (instance == null)
      instance = new App();
    return instance;
  }

  public void run() throws SQLException, ClassNotFoundException, UnknownHostException, SocketException {
    try {
      connectToDb();
      loadBlockchain();
      createPeer();
      peer.queueJobDownloadBlockchain();
      new Thread(() -> {
        try {
          peer.connect();
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    } finally {
      disconnectFromDb();
    }
  }

  private void connectToDb() throws SQLException, ClassNotFoundException {
    db = new Db(DB_FILE_PATH);
    db.connect();
  }

  private void loadBlockchain() throws SQLException {
    H2Gateway gateway = new H2Gateway(db.getConnection());
    blockchain = new Blockchain(gateway);
    blockchain.load();
  }

  private void createPeer() throws UnknownHostException, SocketException {
    InetAddress localhost = InetAddress.getByName("127.0.0.1");
    peer = new Peer(localhost, 8333);
  }

  public Blockchain getBlockchain() {
    return blockchain;
  }

  private void disconnectFromDb() throws SQLException {
    if (db != null) db.disconnect();
  }

}
