package org.nulleins.formats.iso8583.types;

import org.junit.Test;
import org.nulleins.formats.iso8583.types.MTI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * @author phillipsr
 */
public class TestMTI {
  @Test
  public void testGoodMTI() {
    final MTI mti = MTI.create("0200");
    assertThat(mti.toString(), is("0200"));
    assertThat(mti.getVersion(), is("ISO 8583-1:1987"));
    assertThat(mti.getMessageClass(), is("Financial Message"));
    assertThat(mti.getMessageFunction(), is("Request"));
    assertThat(mti.getMessageOrigin(), is("Acquirer"));
  }

  @Test
  public void testGoodMTIBinary() {
    final MTI mti = MTI.create(0x0200);
    assertThat(mti.toString(), is("0200"));
    assertThat(mti.getVersion(), is("ISO 8583-1:1987"));
    assertThat(mti.getMessageClass(), is("Financial Message"));
    assertThat(mti.getMessageFunction(), is("Request"));
    assertThat(mti.getMessageOrigin(), is("Acquirer"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTINonNumeric() {
    MTI.create("A200");
  }

  @Test
  public void testAcquirerReversalAdviceRepeatMessage() {
    final MTI mti = MTI.create("0421");
    assertThat(mti.toString(), is("0421"));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMTIWrongFormat() {
    MTI.create("0206"); // '6' is not allowed in final position
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTITooShort() {
    MTI.create("200");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTITooLong() {
    MTI.create("02000");
  }

}
