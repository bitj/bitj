package org.bitj.wire.objects;

import com.google.common.collect.ImmutableList;
import org.bitj.BaseTest;
import org.bitj.Sha256Hash;
import org.bitj.utils.JarFileLoader;
import org.bitj.wire.BitcoinInputStream;
import org.bitj.wire.Wire;
import org.testng.annotations.Test;

import java.net.ProtocolException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BlockTest extends BaseTest {

  // Examples from https://en.bitcoin.it/wiki/Genesis_block

  public static byte[] SERIALIZED_GENESIS_BLOCK = bytes(
    "01 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00" +
    "00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00" +
    "00 00 00 00 3B A3 ED FD  7A 7B 12 B2 7A C7 2C 3E" +
    "67 76 8F 61 7F C8 1B C3  88 8A 51 32 3A 9F B8 AA" +
    "4B 1E 5E 4A 29 AB 5F 49  FF FF 00 1D 1D AC 2B 7C" +
    "01 01 00 00 00 01 00 00  00 00 00 00 00 00 00 00" +
    "00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00" +
    "00 00 00 00 00 00 FF FF  FF FF 4D 04 FF FF 00 1D" +
    "01 04 45 54 68 65 20 54  69 6D 65 73 20 30 33 2F" +
    "4A 61 6E 2F 32 30 30 39  20 43 68 61 6E 63 65 6C" +
    "6C 6F 72 20 6F 6E 20 62  72 69 6E 6B 20 6F 66 20" +
    "73 65 63 6F 6E 64 20 62  61 69 6C 6F 75 74 20 66" +
    "6F 72 20 62 61 6E 6B 73  FF FF FF FF 01 00 F2 05" +
    "2A 01 00 00 00 43 41 04  67 8A FD B0 FE 55 48 27" +
    "19 67 F1 A6 71 30 B7 10  5C D6 A8 28 E0 39 09 A6" +
    "79 62 E0 EA 1F 61 DE B6  49 F6 BC 3F 4C EF 38 C4" +
    "F3 55 04 E5 1E C1 12 DE  5C 38 4D F7 BA 0B 8D 57" +
    "8A 4C 70 2B 6B F1 1D 5F  AC 00 00 00 00"
  );

  public static final String GENESIS_BLOCK_HASH = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";

  public static final byte[] SERIALIZED_GENESIS_COINBASE_TX = bytes("01000000010000000000000000000000000000000000000000000000000000000000000000FFFFFFFF4D04FFFF001D0104455468652054696D65732030332F4A616E2F32303039204368616E63656C6C6F72206F6E206272696E6B206F66207365636F6E64206261696C6F757420666F722062616E6B73FFFFFFFF0100F2052A01000000434104678AFDB0FE5548271967F1A67130B7105CD6A828E03909A67962E0EA1F61DEB649F6BC3F4CEF38C4F35504E51EC112DE5C384DF7BA0B8D578A4C702B6BF11D5FAC00000000");

  @Test
  public void serializeEmpty() throws Exception {
    Block empty = new Block.Builder().
      version(2).
      prevHash(Sha256Hash.ZERO).
      mrklRoot(Sha256Hash.ONE).
      timestamp(Wire.MAX_UINT_32).
      compactTarget(Wire.MAX_UINT_32 - 1).
      nonce(Wire.MAX_UINT_32 - 2).
      get();
    byte[] serialized = empty.serialize();
    assertEquals(serialized, bytes(
      "02 00 00 00" +  // version
      "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" + // prevHash
      "FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF" + // mrklRoot
      "FF FF FF FF" +  // timestamp
      "FE FF FF FF" +  // compactTarget
      "FD FF FF FF" +  // nonce
      "00" // no transactions (this is illegal but serialization itself supports this)
    ));
  }

  @Test
  public void serializeGenesis() throws Exception {
    Tx genesisCoinbaseTx = Tx.deserialize(bitcoinStream(SERIALIZED_GENESIS_COINBASE_TX));
    ImmutableList<Tx> txns = new ImmutableList.Builder<Tx>().add(genesisCoinbaseTx).build();
    Block genesis = new Block.Builder().
      version(1).
      prevHash(Sha256Hash.ZERO).
      mrklRoot(new Sha256Hash(reversedBytes("3BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A"))).
      timestamp(1231006505L).  // 2009-01-03 19:15:05 +0100 | 29AB5F49 LE
      compactTarget(486604799L).        // FFFF001D LE -> 1D00FFFF BE
      nonce(2083236893L).      // 1DAC2B7C LE -> 7C2BAc1D BE
      txns(txns).
      get();
    byte[] serialized = genesis.serialize();
    assertEquals(serialized, SERIALIZED_GENESIS_BLOCK);
  }

  @Test(expectedExceptions = ProtocolException.class, enabled = true)
  public void deserializeTooLarge() throws Exception {
//    This code can be used to generate artificial block over 1MB
//
//    int txLen = SERIALIZED_GENESIS_COINBASE_TX.length;
//    int times = 1_000_000 / txLen + 1; // minimal number of txns to match/cross the 1MB threshold
//    assertEquals(times, 4902); // sanity check
//    byte[][] arrayOfArrays = new byte[times][txLen];
//    for (int i = 0; i < times; i++)
//      System.arraycopy(SERIALIZED_GENESIS_COINBASE_TX, 0, arrayOfArrays[i], 0, txLen);
//    byte[] header = bytes("01000000 0000000000000000000000000000000000000000000000000000000000000000 3BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A 29AB5F49 FFFF001D 1DAC2B7C  FD2613" /*4902 txns*/ );
//    byte[] txns = Bytes.concat(arrayOfArrays);
//    byte[] serializedOver1MB = Bytes.concat(header, txns);
//    Files.write(FileSystems.getDefault().getPath("/home/qertoip/Projects/bitj/src/test/resources/wire/block_over_1MB.bin"), serializedOver1MB);

    byte[] serializedOver1MB = JarFileLoader.readBinaryFileFromJar("/wire/block_over_1MB.bin");
    BitcoinInputStream in = bitcoinStream(serializedOver1MB);
    Block.deserialize(in);
  }

  @Test
  public void deserializeGenesis() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_GENESIS_BLOCK);
    Block genesis = Block.deserialize(in);
    assertEquals(genesis.getVersion(), 1);
    assertEquals(genesis.getPrevHash(), Sha256Hash.ZERO);
    assertEquals(genesis.getMrklRoot(), new Sha256Hash(reversedBytes("3BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A")));
    assertEquals(genesis.getUnixTimestamp(), 1231006505L); // 2009-01-03 19:15:05 +0100 | 29AB5F49 LE
    assertEquals(genesis.getCompactTarget(), 486604799L);  // FFFF001D LE -> 1D00FFFF BE
    assertEquals(genesis.getNonce(), 2083236893L);         // 1DAC2B7C LE -> 7C2BAc1D BE
    ImmutableList<Tx> txns = genesis.getTxns();
    assertEquals(txns.size(), 1);
    Tx genesisCoinbaseTx = Tx.deserialize(bitcoinStream(SERIALIZED_GENESIS_COINBASE_TX));
    assertEquals(txns.get(0), genesisCoinbaseTx);
  }

  @Test
  public void calculatesHashCorrectly() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_GENESIS_BLOCK);
    Block genesis = Block.deserialize(in);
    assertEquals(genesis.getHash(), new Sha256Hash(GENESIS_BLOCK_HASH));
  }

  @Test
  public void sizeInBytesOfEmptyBlock() throws Exception {
    assertEquals(newEmptyBlock().getSizeInBytes(), 81);
  }

  @Test
  public void sizeInBytesOfGenesisBlock() throws Exception {
    BitcoinInputStream in = bitcoinStream(SERIALIZED_GENESIS_BLOCK);
    Block genesis = Block.deserialize(in);
    assertEquals(genesis.getSizeInBytes(), 285);
  }

  @Test
  public void toStringImplemented() throws Exception {
    assertTrue(newEmptyBlock().toString().contains("Block{"));
  }

  private Block newEmptyBlock() {
    return new Block.Builder().prevHash(Sha256Hash.ZERO).mrklRoot(Sha256Hash.ZERO).compactTarget(0).nonce(0).get();
  }

}
