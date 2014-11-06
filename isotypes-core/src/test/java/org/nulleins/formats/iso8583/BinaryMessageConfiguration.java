package org.nulleins.formats.iso8583;

import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.MTI;

public class BinaryMessageConfiguration {

  public static MessageFactory createMessageFactory() {
    final MessageFactory result = MessageFactory.Builder()
        .id("binaryMessageSet")
        .contentType(ContentType.BCD)
        .bitmapType(BitmapType.BINARY)
        .build();

    final MessageTemplate requestMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0200), BitmapType.BINARY);
    result.addMessage(requestMessageTemplate);
    requestMessageTemplate.addField(FieldTemplate.Builder().number(2).name("cardNumber").description("Payment Card Number").dimension("llvar(40)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(3).name("processingCode").description("Processing Code").dimension("fixed(6)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(4).name("amount").description("Amount, transaction (cents)").dimension("fixed(12)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date and Time").dimension("fixed(10)").type("date").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(11).name("stan").description("System Trace Audit Number").dimension("fixed(6)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(12).name("transTimeLocal").description("Time, local transaction").dimension("fixed(6)").type("time").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(13).name("transDateLocal").description("Date, local transaction").dimension("fixed(4)").type("date").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(32).name("acquierID").description("Acquiring Institution ID").dimension("llvar(4)").type("n").defaultValue("0000").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(37).name("rrn").description("Retrieval Reference Number").dimension("fixed(12)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(41).name("cardTermId").description("Card Acceptor Terminal ID").dimension("fixed(16)").type("ans").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(43).name("cardTermName").description("Card Acceptor Terminal Name").dimension("fixed(40)").type("ans").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(48).name("msisdn").description("Additional Data (MSISDN)").dimension("llvar(14)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(49).name("currencyCode").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build());
    requestMessageTemplate.addField(FieldTemplate.Builder().number(90).name("originalData").description("Original data elements").dimension("lllvar(999)").type("xn").build());

    final MessageTemplate reversalMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0400), BitmapType.BINARY);
    result.addMessage(reversalMessageTemplate);

    reversalMessageTemplate.addField(FieldTemplate.Builder().number(2).name("cardNumber").description("Payment Card Number").dimension("llvar(2)").type("n").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date and Time").dimension("fixed(10)").type("date").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(12).name("transTimeLocal").description("Time, local transaction").dimension("fixed(6)").type("time").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(28).name("transDateLocal").description("Date, local transaction").dimension("fixed(4)").type("date").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(32).name("acquierID").description("Acquiring Institution ID").dimension("llvar(2)").type("n").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(39).name("rrn").description("Retrieval Reference Number").dimension("fixed(12)").type("n").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(41).name("cardTermId").description("Card Acceptor Terminal ID").dimension("fixed(6)").type("ans").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(42).name("cardTermName").description("Card Acceptor Terminal Name").dimension("fixed(40)").type("ans").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(50).name("msisdn").description("Additional Data (MSISDN)").dimension("lllvar(3)").type("n").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(53).name("currencyCode2").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build());
    reversalMessageTemplate.addField(FieldTemplate.Builder().number(62).name("currencyCode3").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build());

    final MessageTemplate txAdviceMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0220), BitmapType.BINARY);
    result.addMessage(txAdviceMessageTemplate);

    txAdviceMessageTemplate.addField(FieldTemplate.Builder().number(2).name("cardNumber").description("Payment Card Number").dimension("llvar(20)").type("n").build());
    txAdviceMessageTemplate.addField(FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date/Time").dimension("fixed(10)").type("date").build());
    txAdviceMessageTemplate.addField(FieldTemplate.Builder().number(22).name("posEntryMode").description("Date, local transaction").dimension("fixed(12)").type("an").build());
    txAdviceMessageTemplate.addField(FieldTemplate.Builder().number(63).name("privateResv").description("Private, reserved").dimension("lllvar(120)").type("an").build());

    return result;
  }

}
