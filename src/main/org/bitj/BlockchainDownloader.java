package org.bitj;

import org.bitj.wire.messages.*;
import org.bitj.wire.objects.InvItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * THIS IS A THROW-AWAY HACK USED TO EDUCATE MYSELF ABOUT BASICS OF THE INITIAL BLOCKCHAIN DOWNLOAD
 */
public class BlockchainDownloader {

  private InputStream in;
  private OutputStream out;

  public BlockchainDownloader(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
  }

  public void start() throws IOException {
    System.out.println("Downloading blockchain...");
    Blockchain blockchain = App.getInstance().getBlockchain();
    Msg msg = null;
    InvMsg invMsg = null;

    while (true) {

      // Send getblocks to get next part of the inventory
      GetBlocksMsg getblocks = new GetBlocksMsg(blockchain.getDefaultBlockLocator());
      System.out.println("Sending: " + getblocks);
      getblocks.serialize(out);

      // Wait for the block inv
      invMsg = getNextBlockInv();

      // Wait for all the blocks (as earlier advertised in the inv)
      int received = 0;
      int expected = invMsg.getInvItems().size();
      BlockMsg blockMsg = null;
      while (true) {
        msg = Msg.deserialize(in);
        if (msg instanceof BlockMsg) {
          blockMsg = (BlockMsg) msg;
          blockchain.append(blockMsg.getBlock());
          received += 1;
          System.out.println("Received: block " + (blockchain.getHeight()-1) + " / " + blockMsg.getBlock());
          if (received == expected)
            break;
        } else {
          ignore(msg);
        }
      }

      // get inv of single latest block
      invMsg = getNextBlockInv();

      // get single latest block (orphaned at this point)
      while (true) {
        msg = Msg.deserialize(in);
        if (msg instanceof BlockMsg) {
          blockMsg = (BlockMsg) msg;
          System.out.println("Received: latest block / " + blockMsg.getBlock());
          break;
        } else {
          ignore(msg);
        }
      }

    } // parts of 500

  }

  private InvMsg getNextBlockInv() throws IOException {
    Msg msg;
    InvMsg invMsg;
    while (true) {
      msg = Msg.deserialize(in);
      if (msg instanceof InvMsg) {
        invMsg = (InvMsg) msg;
        if (invMsg.getInvItems().asList().get(0).type.equals(InvItem.Type.Block)) {
          System.out.println("Received: " + invMsg);
          GetDataMsg getdata = new GetDataMsg(invMsg.getInvItems());
          System.out.println("Sending: " + getdata);
          getdata.serialize(out);
          return invMsg;
        } else {
          ignore(msg);
        }
      } else {
        ignore(msg);
      }
    }
  }

  private void ignore(Msg msg) {
    System.out.println("Ignoring: " + msg);
  }

}
