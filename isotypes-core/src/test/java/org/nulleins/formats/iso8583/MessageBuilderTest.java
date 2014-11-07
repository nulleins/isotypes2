package org.nulleins.formats.iso8583;

import org.junit.Test;
import org.nulleins.formats.iso8583.config.BankMessageConfiguration;
import org.nulleins.formats.iso8583.types.MTI;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilderTest {

  private final MessageFactory factory = BankMessageConfiguration.createMessageFactory();

  @Test
  public void builderCreatesMessage() {
    final Map<Integer, Object> fields = new HashMap<>();
    fields.put(3, "456");
    final MTI messageType = MTI.create(0x0200);
    final Message subject = Message.Builder()
        .messageTypeIndicator(messageType)
        .header("ISO015000077")
        .template(factory.getTemplate(messageType))
        .fields(fields)
        .build();
    subject.setFieldValue(2,10101);
    System.out.println(subject.describe());
  }
}
