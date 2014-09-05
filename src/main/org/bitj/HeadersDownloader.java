package org.bitj;

import org.bitj.wire.messages.*;
import org.bitj.wire.objects.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * THIS IS A THROW-AWAY HACK USED TO EDUCATE MYSELF ABOUT BASICS OF THE INITIAL BLOCKCHAIN DOWNLOAD
 */
public class HeadersDownloader {

  private InputStream in;
  private OutputStream out;

  public HeadersDownloader(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public void start() throws IOException {
    System.out.println("Downloading block headers...");
    Blockchain blockchain = App.getInstance().getBlockchain();
    Msg msg;

    long currentHeight = blockchain.getHeight();
    long prevHeight;
    do {
      prevHeight = currentHeight;

      // Send getheaders to get the next batch of block headers
      GetHeadersMsg getHeaders = new GetHeadersMsg(blockchain.getDefaultBlockLocator());
      //System.out.println("Sending: " + getHeaders);
      getHeaders.serialize(out);

      // Expect the headers message containing 2000 block headers (except the last iteration)
      do {
        msg = Msg.deserialize(in);
        if (msg instanceof HeadersMsg) {
          HeadersMsg headersMsg = (HeadersMsg) msg;
          List<Block> blocks = headersMsg.getBlocks();
          blockchain.append(blocks);
          currentHeight = blockchain.getHeight();
          System.out.println("Blockchain height: " + currentHeight);
        } else {
          ignore(msg);
        }
      } while (!(msg instanceof HeadersMsg));

    } while (prevHeight < currentHeight);

  }

  private void ignore(Msg msg) {
    System.out.println("Ignoring: " + msg);
  }

}
