package org.nulleins.formats.iso8583;

import org.junit.Before;
import org.junit.Test;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.FieldType;
import org.nulleins.formats.iso8583.types.MTI;

import java.math.BigInteger;
import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * @author phillipsr
 */
public class TestFields {
  private MessageTemplate messageTemplate;

  @Before
  public void setup() {
    messageTemplate = MessageTemplate.create("", MTI.create(0x0200), BitmapType.HEX);
    final MessageFactory schema = new MessageFactory();
    schema.initialize();
    messageTemplate.setSchema(schema);
  }

  @Test
  public void testNumeric()
      throws ParseException {
    final FieldTemplate target = FieldTemplate.Builder()
        .number(2)
        .type(FieldType.NUMERIC)
        .dimension("fixed(6)")
        .name("test").build();
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format(128));
    assertThat(intValue, is("000128"));

    final Object readBack = target.parse(intValue.getBytes());
    assertThat(readBack, instanceOf(BigInteger.class));
    assertThat((BigInteger)readBack, is(BigInteger.valueOf(128)));

    final String biValue = new String(target.format(BigInteger.TEN.multiply(BigInteger.TEN).add(BigInteger.TEN)));
    assertThat(biValue, is("000110"));

    final String strValue = new String(target.format(726161));
    assertThat(strValue, is("726161"));
  }

  @Test
  public void testLlvar() {
    final FieldTemplate target = FieldTemplate.Builder()
        .number(2)
        .type(FieldType.ALPHANUM)
        .dimension("llvar(3)")
        .name("test").build();
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format("128"));
    assertThat(intValue, is("128"));

  }

  @Test
  public void testLllvar() {
    final FieldTemplate target = FieldTemplate.Builder()
        .number(2)
        .type(FieldType.ALPHASYMBOL)
        .dimension("lllvar(11)")
        .name("test").build();
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format("Hello World"));
    assertThat(intValue, is("Hello World"));

  }

}
