package org.bitj;

import com.google.common.primitives.Bytes;
import org.bitj.utils.Crypto;
import org.bitj.utils.SatoshiBase58;

import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

/**
 * A Bitcoin address is an identifier of 25-34 alphanumeric characters,
 * that represents a possible destination for a Bitcoin payment.
 *
 * Bitcoin address looks like "3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy" and is case-sensitive.
 *
 * A standard Bitcoin address is created by hashing the public key with SHA256 then RIPE-MD160,
 * adding a version prefix and checksum postfix and finally encoding it as "Satoshi BASE58".
 *
 * The version prefix is used to specify both the network and a way to interpret bytes inside
 * the address - as a (hash of) a public key or a script.
 */
public class Address {

  // TODO: support testnet

  private int version;   // denotes both the network and the type of an address
  private byte[] hash160Bytes;
  private String cachedEncoded;

  public Address(String encoded) {
    throwIfNull(encoded);
    byte[] versionAndDataBytes = decodeSatoshiBase58WithChecksumCheck(encoded);
    version = versionAndDataBytes[0] & 0xFF;
    throwIfUnknownVersion();
    hash160Bytes = copyOfRange(versionAndDataBytes, 1, versionAndDataBytes.length);
    throwIfInvalidLengthOfHash160();
  }

  public Address(int version, byte[] hash160Bytes) {
    this.version = version;
    throwIfUnknownVersion();
    this.hash160Bytes = hash160Bytes;
    throwIfInvalidLengthOfHash160();
  }

  public int getVersion() {
    return version;
  }

  public byte[] getHash160Bytes() {
    return hash160Bytes;
  }

  public String toString() {
    if (cachedEncoded == null) {
      byte[] versionAndDataBytes = Bytes.concat(new byte[]{(byte) version}, hash160Bytes);
      byte[] doubleHashed = Crypto.bitcoinHash(versionAndDataBytes);
      byte[] checksum = copyOfRange(doubleHashed, 0, 4);
      byte[] full = Bytes.concat(versionAndDataBytes, checksum);
      cachedEncoded = SatoshiBase58.encode(full);
    }
    return cachedEncoded;
  }

  /**
   * Uses the checksum in the last 4 bytes of the decoded data to verify the rest are correct. The checksum is
   * removed from the returned data.
   *
   * @throws IllegalArgumentException if the input is not base 58 or the checksum does not validate.
   */
  private static byte[] decodeSatoshiBase58WithChecksumCheck(String input) {
    byte tmp[] = SatoshiBase58.decode(input);
    if (tmp.length < 4)
      throw new IllegalArgumentException("Input too short");
    byte[] bytes = copyOfRange(tmp, 0, tmp.length - 4);
    byte[] expectedChecksum = copyOfRange(tmp, tmp.length - 4, tmp.length);
    tmp = Crypto.bitcoinHash(bytes);
    byte[] hash = copyOfRange(tmp, 0, 4);
    if (!Arrays.equals(expectedChecksum, hash))
      throw new IllegalArgumentException("Checksum does not validate");
    return bytes;
  }

  private void throwIfNull(String encoded) {
    if (encoded == null)
      throw new IllegalArgumentException("Cannot be null");
  }

  private void throwIfUnknownVersion() {
    // TODO: refactor version checking
    if (version != 0 && version != 5 && version != 111 && version != 196)
      throw new IllegalArgumentException("Unknown version " + version);
  }

  private void throwIfInvalidLengthOfHash160() {
    if (hash160Bytes.length != 20)
      throw new IllegalArgumentException("Expected 20 bytes, got " + hash160Bytes.length);
  }

}
