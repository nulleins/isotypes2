package org.nulleins.formats.iso8583;

import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.CharEncoder;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.MTI;

public class CharMessageConfiguration {

  public static MessageFactory createMessageFactory() {
    final MessageFactory result = MessageFactory.Builder()
        .id("charMessageSet")
        .contentType(ContentType.TEXT)
        .bitmapType(BitmapType.HEX)
        .header("ISO015000077")
        .charset(new CharEncoder("cp1047"))
        .build();

    final MessageTemplate requestMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0200), BitmapType.HEX);
    result.addMessage(requestMessageTemplate);
    requestMessageTemplate.setName("Acquirer Payment Request");

    requestMessageTemplate.addField(FieldTemplate.Builder().number(2).name("cardNumber").description("Payment Card Number").dimension("llvar(40)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(3).name("processingCode").description("Processing Code").dimension("fixed(6)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(4).name("amount").description("Amount, transaction (cents)").dimension("fixed(12)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date and Time").dimension("fixed(10)").type("date").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(11).name("stan").description("System Trace Audit Number").dimension("fixed(6)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(12).name("transTimeLocal").description("Time, local transaction").dimension("fixed(6)").type("time").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(13).name("transDateLocal").description("Date, local transaction").dimension("fixed(4)").type("date").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(32).name("acquierID").description("Acquiring Institution ID").dimension("llvar(4)").type("n").defaultValue("0000").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(37).name("extReference").description("Retrieval Reference Number").dimension("fixed(12)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(41).name("cardTermId").description("Card Acceptor Terminal ID").dimension("fixed(16)").type("ans").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(43).name("cardTermName").description("Card Acceptor Terminal Name").dimension("fixed(40)").type("ans").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(48).name("msisdn").description("Additional Data (MSISDN)").dimension("llvar(14)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(49).name("currencyCode").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(90).name("originalData").description("Original data elements").dimension("lllvar(4)").type("xn").build());
    
    return result;
  }

}
