package org.bitj.wire.messages;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import org.bitj.BaseTest;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.objects.Block;
import org.bitj.wire.objects.BlockTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HeadersMessageTest extends BaseTest {

  // Examples from https://en.bitcoin.it/wiki/Genesis_block

  public static byte[] SERIALIZED_GENESIS_BLOCK_HEADER = bytes(
    "01 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00" +
      "00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00" +
      "00 00 00 00 3B A3 ED FD  7A 7B 12 B2 7A C7 2C 3E" +
      "67 76 8F 61 7F C8 1B C3  88 8A 51 32 3A 9F B8 AA" +
      "4B 1E 5E 4A 29 AB 5F 49  FF FF 00 1D 1D AC 2B 7C" +
      "00"  // number of transactions
  );

  static HeadersMessage MESSAGE;

  static {
    try {
      ImmutableList<Block> blocks = ImmutableList.of(
        Block.deserialize(bitcoinStream(BlockTest.SERIALIZED_GENESIS_BLOCK)),  // with 1 tx
        Block.deserialize(bitcoinStream(BlockTest.SERIALIZED_GENESIS_BLOCK))   // with 1 tx
      );
      MESSAGE = new HeadersMessage(blocks);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static byte[] PAYLOAD_BYTES = Bytes.concat(
    // number of headers
    bytes(2),
    // header 1
    SERIALIZED_GENESIS_BLOCK_HEADER,
    // header 2
    SERIALIZED_GENESIS_BLOCK_HEADER
  );

  static ImmutableList<Block> EMPTY_BLOCK_LIST = new ImmutableList.Builder<Block>().build();

  @Test
  public void serializeEmpty() throws IOException {
    assertEquals(new HeadersMessage(EMPTY_BLOCK_LIST).serializePayload(), bytes(0));
  }

  @Test
  public void serializePayload() throws IOException {
    assertEquals(MESSAGE.serializePayload(), PAYLOAD_BYTES);
  }

  @Test
  public void deserializePayload_WhenEmpty() throws IOException {
    BitcoinInputStream in = bitcoinStream(bytes(0));
    assertEquals(HeadersMessage.deserializePayload(in), new HeadersMessage(EMPTY_BLOCK_LIST));
  }

  @Test(expectedExceptions = HeadersMessage.TooMany.class)
  public void deserializePayload_WhenTooManyItems() throws IOException {
    byte[] maliciousPayload = new byte[PAYLOAD_BYTES.length];
    System.arraycopy(PAYLOAD_BYTES, 0, maliciousPayload, 0, PAYLOAD_BYTES.length);
    // Set number of headers to 2001
    maliciousPayload[0] = (byte) 253;
    maliciousPayload[1] = (byte) 0xD1;
    maliciousPayload[2] = (byte) 0x07;
    HeadersMessage.deserializePayload(bitcoinStream(maliciousPayload));
  }

  @Test
  public void deserializePayload() throws IOException {
    BitcoinInputStream in = bitcoinStream(PAYLOAD_BYTES);
    assertEquals(HeadersMessage.deserializePayload(in), MESSAGE);
  }

  @Test
  public void getSizeInBytes_WhenEmpty() throws Exception {
    HeadersMessage emptyMsg = new HeadersMessage(EMPTY_BLOCK_LIST);
    assertEquals(emptyMsg.getSizeInBytes(), 1);
  }

  @Test
  public void getSizeInBytes() throws Exception {
    HeadersMessage msg = HeadersMessage.deserializePayload(bitcoinStream(PAYLOAD_BYTES));
    assertEquals(msg.getSizeInBytes(), 1 + 81 + 81);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(MESSAGE.toString().contains("HeadersMessage{"));
  }

}
