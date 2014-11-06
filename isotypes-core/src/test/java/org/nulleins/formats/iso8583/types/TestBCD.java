package org.nulleins.formats.iso8583.types;

import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * @author phillipsr
 */
public class TestBCD {
  @Test
  public void testLength() {
    int length = 999;
    byte[] data = BCD.valueOf(length);
    assertThat(data, is(new byte[]{(byte) 0x09, (byte) 0x99}));

    length = 15;
    data = BCD.valueOf(length);
    assertThat(data, is(new byte[]{0x15}));
  }

  @Test
  public void testStringConvert() {
    String test = "1232199";
    byte[] data = BCD.valueOf(test);
    assertThat(data, is(new byte[]{0x01, 0x23, 0x21, (byte) 0x99}));
  }

  @Test
  public void testStringConvert2() {
    String test = "102";
    byte[] data = BCD.valueOf(test);
    assertThat(data, is(new byte[]{0x01, 0x02}));
  }

  @Test
  public void testStringConvertPadded() {
    String test = "001010";
    byte[] data = BCD.valueOf(test);
    assertThat(data, is(new byte[]{0x00, 0x10, 0x10}));
  }

  @Test
  public void testStringJustification() {
    byte[] data = new byte[]{0x08, 0x40};
    String result = BCD.toString(data);
    assertThat(result, is("0840"));
  }

  @Test
  public void testPaddingInteger() {
    int target = 123; // will need two bytes
    byte[] data = BCD.valueOf(target);
    assertThat(data.length, is(2));
    assertThat(data[0], is((byte)0x01));
    assertThat(data[1], is((byte)0x23));
  }

  @Test
  public void testPaddingLong() {
    long target = 5432818929192L; // 13 digits will need seven bytes
    byte[] data = BCD.valueOf(target);

    assertThat(data,
        is(new byte[]{(byte) 0x05, (byte) 0x43, (byte) 0x28,
            (byte) 0x18, (byte) 0x92, (byte) 0x91, (byte) 0x92}));
  }

  @Test
  public void testPaddingBigInteger() {
    BigInteger target = BigInteger.valueOf(5432818929192L); // 13 digits will need seven bytes
    byte[] data = BCD.valueOf(target);

    assertThat(data,
        is(new byte[]{(byte) 0x05, (byte) 0x43, (byte) 0x28,
            (byte) 0x18, (byte) 0x92, (byte) 0x91, (byte) 0x92}));
  }

  @Test
  public void testPaddingString() {
    String target = "123"; // will need two bytes
    byte[] data = BCD.valueOf(target);
    assertThat(data, is(new byte[]{(byte) 0x01, (byte) 0x23}));
  }

}
