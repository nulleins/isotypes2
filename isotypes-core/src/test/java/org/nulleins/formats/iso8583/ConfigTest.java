package org.nulleins.formats.iso8583;

import org.junit.Test;
import org.nulleins.formats.iso8583.schema.MessageConfig;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.CharEncoder;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.MTI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigTest {

  private static final String CONFIG_PATH = "messageTest.conf";

  @Test
  public void configuresFactory() {
    final MessageFactory factory = MessageConfig.configure(CONFIG_PATH);
    assertThat(factory.getId(), is("bankMessages"));
    assertThat(factory.getHeader(), is("ISO015000077"));
    assertThat(factory.getDescription(), is("TestBank banking messages"));
    assertThat(factory.getBitmapType(), is(BitmapType.HEX));
    assertThat(factory.getContentType(), is(ContentType.TEXT));
    assertThat(factory.getCharset(), is(CharEncoder.ASCII));
    assertThat(factory.getMessages().size(), is(2));
  }

  @Test
  public void factoryCanCreateMessage() {
    final MessageFactory factory = MessageConfig.configure(CONFIG_PATH);
    final MTI type = MTI.create(0x0200);
    final Message message = factory.create(type);
    assertThat(message.getMTI(), is(type));
    assertThat(message.getHeader(), is("ISO015000077"));
  }
}
