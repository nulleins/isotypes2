package org.nulleins.formats.iso8583;

import org.junit.Test;
import org.nulleins.formats.iso8583.types.Bitmap;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.Dimension;
import org.nulleins.formats.iso8583.types.FieldType;
import org.nulleins.formats.iso8583.types.MTI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/** basic test of programmatic API, creating a message template, adding fields and asserting correct values
  * @author phillipsr */
public class TestMessageTemplate {
  private static final byte[] BINARY_BITMAP1 = new byte[]{(byte) 0xf2, 0x20, 0, 0, 0, 0, 0, 1};
  private static final byte[] BINARY_BITMAP2 = new byte[]{(byte) 0xc0, 0, 0, 0, 0, 0, 0, 1};
  private static final byte[] BINARY_BITMAP3 = new byte[]{0x40, 0, 0, 0, 0, 0, 0, 1};

  private static final MTI PaymentRequest = MTI.create("0200");
  private static final Dimension FIXED6 = Dimension.parse("fixed(6)");
  private static final Dimension FIXED10 = Dimension.parse("fixed(10)");

  @Test
  public void testCreateMessageTemplate() {
    final MessageTemplate template = MessageTemplate.create("ISO015000077", PaymentRequest, BitmapType.BINARY);
    assertThat(template.getMessageTypeIndicator(), is(PaymentRequest));
    assertThat(template.getHeader(), is("ISO015000077"));

    final Map<Integer, FieldTemplate> fields = new HashMap<Integer, FieldTemplate>() {{
      put( 2, FieldTemplate.Builder().number(2).type(FieldType.ALPHA).dimension("llvar(10)").name("TestField").description("Just a Test").build());
      put( 3, FieldTemplate.Builder().number(3).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("Processing Code").build());
      put( 4, FieldTemplate.Builder().number(4).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("Amount, transaction (PT - cents)").build());
      put( 7, FieldTemplate.Builder().number(7).type(FieldType.DATE).dimension(FIXED10).name("TestField").description("Transmission Date and Time").build());
      put(11, FieldTemplate.Builder().number(11).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());
    }};
    template.setFields(fields);

    template.addField(FieldTemplate.Builder().number(64).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());
    template.addField(FieldTemplate.Builder().number(66).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());
    template.addField(FieldTemplate.Builder().number(128).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());
    template.addField(FieldTemplate.Builder().number(130).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());
    template.addField(FieldTemplate.Builder().number(192).type(FieldType.NUMERIC).dimension(FIXED6).name("TestField").description("System Trace Audit Number").build());

    // E220000000000001 becomes F220000000000001 as secondary bitmap now set
    assertThat(template.getBitmap().asHex(Bitmap.Id.PRIMARY), is("F220000000000001"));
    assertThat(template.getBitmap().asHex(Bitmap.Id.SECONDARY), is("C000000000000001"));
    assertThat(template.getBitmap().asHex(Bitmap.Id.TERTIARY), is("4000000000000001"));

    assertThat(Arrays.equals(BINARY_BITMAP1, template.getBitmap().asBinary(Bitmap.Id.PRIMARY)), is(true));
    assertThat(Arrays.equals(BINARY_BITMAP2, template.getBitmap().asBinary(Bitmap.Id.SECONDARY)), is(true));
    assertThat(Arrays.equals(BINARY_BITMAP3, template.getBitmap().asBinary(Bitmap.Id.TERTIARY)), is(true));
  }

}
