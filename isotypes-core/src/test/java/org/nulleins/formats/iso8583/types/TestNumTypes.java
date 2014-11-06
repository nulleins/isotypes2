package org.nulleins.formats.iso8583.types;

import org.junit.Test;
import org.nulleins.formats.iso8583.formatters.NumberFormatter;
import org.nulleins.formats.iso8583.formatters.TypeFormatter;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.ParsePosition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;


/**
 * @author phillipsr
 */
public class TestNumTypes {
  private TypeFormatter<BigInteger> formatter = new NumberFormatter(CharEncoder.ASCII);

  @Test
  public void testParseNumeric()
      throws ParseException {
    byte[] testData = "XXXX1234567ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 5);
    BigInteger value = formatter.parse(FieldType.NUMERIC, Dimension.parse("FIXED(5)"), 5, data);

    assertThat(value, is(BigInteger.valueOf(12345)));
    assertThat(pos.getIndex(), is(9));
  }

  @Test(expected = ParseException.class)
  public void testParseNumericBad()
      throws ParseException {
    byte[] testData = "XXXX1234567ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    try {
      byte[] data = FieldParser.getBytes(testData, pos, 8);
      formatter.parse(FieldType.NUMERIC, Dimension.parse("FIXED(8)"), 8, data);
    } catch (ParseException e) {
      assertThat(e.getMessage(), is("Bad number format For input string: \"1234567Z\" for type=n [1234567Z]"));
      assertThat(pos.getIndex(), is(12));

      throw e;
    }
  }

  @Test
  public void testParseSignedNumeric()
      throws ParseException {
    byte[] testData = "XXXXD123456ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 5);
    BigInteger value = formatter.parse(FieldType.NUMSIGNED, Dimension.parse("FIXED(5)"), 5, data);

    assertThat(value, is(BigInteger.valueOf(-1234)));
    assertThat(pos.getIndex(), is(9));
  }

  @Test
  public void testFormat() {
    byte[] data = formatter.format(FieldType.NUMERIC, 123, Dimension.parse("FIXED(6)"));

    assertThat(data.length, is(6));
    assertThat(new String(data), is("000123"));
  }

  @Test
  public void testFormatXNDebit() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, -123, Dimension.parse("FIXED(5)"));

    assertThat(data.length, is(5));
    assertThat(new String(data), is("D0123"));
  }

  @Test
  public void testFormatXNDebitVar() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, -123, Dimension.parse("LLVAR(5)"));

    assertThat(data.length, is(4));
    assertThat(new String(data), is("D123"));
  }

  @Test
  public void testFormatXNCredit() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, 123, Dimension.parse("FIXED(5)"));

    assertThat(data.length, is(5));
    assertThat(new String(data), is("C0123"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatXNTooLong() {
    try {
      formatter.format(FieldType.NUMSIGNED, -123456, Dimension.parse("FIXED(5)"));
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), startsWith(
          "Field data length (7) exceeds field maximum (5)"));
      throw e;
    }
  }

  @Test
  public void testFormatExact() {
    byte[] data = formatter.format(FieldType.NUMERIC, 123456, Dimension.parse("FIXED(6)"));

    assertThat(data.length, is(6));
    assertThat(new String(data), is("123456"));
  }


  @Test
  public void testFormatNumVar() {
    byte[] testData = "123456".getBytes();
    Dimension dim = Dimension.parse("llvar(10)");

    byte[] result = formatter.format(FieldType.NUMERIC, testData, dim);
    assertThat(result, is(testData));
  }

  @Test
  public void testFormatNumFix() {
    String testData = "123456";
    Dimension dim = Dimension.parse("fixed(10)");

    String result = new String(formatter.format(FieldType.NUMERIC, testData, dim));
    assertThat(result, is("0000" + testData));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNumTooLong() {
    byte[] testData = "1234567".getBytes();
    Dimension dim = Dimension.parse("llvar(2)");

    try {
      formatter.format(FieldType.NUMERIC, testData, dim);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), startsWith(
          "Field data length (7) exceeds field maximum (2)"));
      throw e;
    }
  }

}
