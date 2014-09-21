package org.bitj;

import com.google.common.base.Objects;

public class Amount {

  public static final long MAX_COINS = 21_000_000L;  // 21 million bitcoins
  public static final long SATOSHI_PER_COIN = 100_000_000;
  public static final long MAX_SATOSHI = MAX_COINS * SATOSHI_PER_COIN;

  private long satoshi;

  public Amount(long satoshi) {
    this.satoshi = satoshi;
  }

  public long satoshi() {
    return satoshi;
  }

  public long coins() {
    return satoshi / SATOSHI_PER_COIN;
  }

  public long remainder() {
    return satoshi % SATOSHI_PER_COIN;
  }

  @Override
  public String toString() {
    long coins = Math.abs(coins());
    long rem = Math.abs(remainder());
    String sign = satoshi >= 0 ? "" : "-";
    if (rem == 0)
      return sign + String.valueOf(coins);
    String s = sign + coins + "." + String.format("%08d", rem);
    return s.replaceFirst("0*\\Z", ""); // remove trailing zeroes ie 3.14000000 => 3.14
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Amount that = (Amount) o;
    return this.satoshi == that.satoshi;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(satoshi);
  }

  public boolean isLegalTxOutputValue() {
    return satoshi >= 0 && satoshi <= MAX_SATOSHI;
  }

}
