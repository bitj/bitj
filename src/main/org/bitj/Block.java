package org.bitj;

public class Block {

  public Sha256Hash hash;
  public Sha256Hash prevHash;
  public Sha256Hash mrklRoot;
  public long timestamp;
  public byte[] target;
  public long nonce;
  public byte[] mrklTree;  // consecutive 32-byte hashes

  public Block() {}

  public Block(Sha256Hash hash) {
    this.hash = hash;
  }

}
