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
    Message msg = null;
    InvMessage invMsg = null;

    while (true) {

      // Send getblocks to get next part of the inventory
      GetBlocksMessage getblocks = new GetBlocksMessage(blockchain.getDefaultBlockLocator());
      System.out.println("Sending: " + getblocks);
      getblocks.serialize(out);

      // Wait for the block inv
      invMsg = getNextBlockInv();

      // Wait for all the blocks (as earlier advertised in the inv)
      int received = 0;
      int expected = invMsg.getInvItems().size();
      BlockMessage blockMsg = null;
      while (true) {
        msg = Message.deserialize(in);
        if (msg instanceof BlockMessage) {
          blockMsg = (BlockMessage) msg;
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
        msg = Message.deserialize(in);
        if (msg instanceof BlockMessage) {
          blockMsg = (BlockMessage) msg;
          System.out.println("Received: latest block / " + blockMsg.getBlock());
          break;
        } else {
          ignore(msg);
        }
      }

    } // parts of 500

  }

  private InvMessage getNextBlockInv() throws IOException {
    Message msg;
    InvMessage invMsg;
    while (true) {
      msg = Message.deserialize(in);
      if (msg instanceof InvMessage) {
        invMsg = (InvMessage) msg;
        if (invMsg.getInvItems().asList().get(0).type.equals(InvItem.Type.Block)) {
          System.out.println("Received: " + invMsg);
          GetDataMessage getdata = new GetDataMessage(invMsg.getInvItems());
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

  private void ignore(Message msg) {
    System.out.println("Ignoring: " + msg);
  }

}
