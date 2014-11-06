package org.nulleins.formats.iso8583;

import org.nulleins.formats.iso8583.formatters.special.AddAmountsFormatter;
import org.nulleins.formats.iso8583.formatters.special.CardAcceptorLocationFormatter;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.MTI;

import static java.util.Arrays.asList;

public class BankMessageConfiguration {

  /*
  <iso:message).type("0200").name("Transaction Request">
  */
  public static MessageFactory createMessageFactory() {

    final MessageFactory result = MessageFactory.Builder()
        .id("messageSet")
        .contentType(ContentType.TEXT)
        .bitmapType(BitmapType.HEX)
        .build();
    result.addFormatter("AAf", new AddAmountsFormatter());
    result.addFormatter("CALf", new CardAcceptorLocationFormatter());
    result.setAutoGeneratorFactory(new AutoGeneratorFactory(new StanGenerator(1, 999)));

    final MessageTemplate requestMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0200), BitmapType.HEX);
    requestMessageTemplate.setName("Transaction Request");
    result.addMessage(requestMessageTemplate);
    requestMessageTemplate.setFields(asList(
        FieldTemplate.Builder().number(2).name("accountNumber").description("Primary Account Number").dimension("llvar(19)").type("n").build(),
        FieldTemplate.Builder().number(3).name("processingCode").description("Processing Code").dimension("fixed(6)").type("n").build(),
        FieldTemplate.Builder().number(4).name("amount").description("Amount, transaction (cents)").dimension("fixed(12)").type("n").build(),
        FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date and Time").dimension("fixed(10)").type("date").build(),
        FieldTemplate.Builder().number(11).name("stan").description("System Trace Audit Number").dimension("fixed(6)").type("n").build(),
        FieldTemplate.Builder().number(12).name("transTimeLocal").description("Time, local transaction").dimension("fixed(6)").type("time").build(),
        FieldTemplate.Builder().number(13).name("transDateLocal").description("Date, local transaction").dimension("fixed(4)").type("date").build(),
        FieldTemplate.Builder().number(17).name("captureDate").description("Date, capture").dimension("fixed(4)").type("date").build(),
        FieldTemplate.Builder().number(28).name("transactionFee").description("Amount, transaction fee").dimension("fixed(9)").type("xn").build(),
        FieldTemplate.Builder().number(30).name("processingFee").description("Amount, tx processing fee").dimension("fixed(9)").type("xn").build(),
        FieldTemplate.Builder().number(32).name("acquierID").description("Acquiring Institution ID").dimension("llvar(11)").type("n").build(),
        FieldTemplate.Builder().number(33).name("forwarderID").description("Forwarding Institution ID").dimension("llvar(11)").type("n").build(),
        FieldTemplate.Builder().number(37).name("rrn").description("Retrieval Reference Number").dimension("fixed(12)").type("anp").build(),
        FieldTemplate.Builder().number(41).name("cardTermId").description("Card Acceptor Terminal ID").dimension("fixed(8)").type("ans").build(),
        FieldTemplate.Builder().number(42).name("cardAcceptorId").description("Card Acceptor ID Code").dimension("fixed(15)").type("ans").build(),
        FieldTemplate.Builder().number(43).name("cardAcceptorLoc").description("Card Acceptor Location Name").dimension("fixed(40)").type("ans").build(),
        FieldTemplate.Builder().number(49).name("currencyCode").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build(),
        FieldTemplate.Builder().number(60).name("adviceCode").description("Advice/reason code").dimension("lllvar(999)").type("an").build()));

    final MessageTemplate responseMessageTemplate = MessageTemplate.create("ISO015000077", MTI.create(0x0210), BitmapType.HEX);
    result.addMessage(responseMessageTemplate);
    responseMessageTemplate.setFields(asList(
        FieldTemplate.Builder().number(2).name("accountNumber").description("Primary Account Number").dimension("llvar(19)").type("n").build(),
        FieldTemplate.Builder().number(3).name("processingCode").description("Processing Code").dimension("fixed(6)").type("n").build(),
        FieldTemplate.Builder().number(4).name("amount").description("Amount, transaction (cents)").dimension("fixed(12)").type("n").build(),
        FieldTemplate.Builder().number(7).name("transDateTime").description("Transmission Date and Time").dimension("fixed(10)").type("date").build(),
        FieldTemplate.Builder().number(11).name("stan").description("System Trace Audit Number").dimension("fixed(6)").type("n").build(),
        FieldTemplate.Builder().number(12).name("transTimeLocal").description("Time, local transaction").dimension("fixed(6)").type("time").build(),
        FieldTemplate.Builder().number(13).name("transDateLocal").description("Date, local transaction").dimension("fixed(4)").type("date").build(),
        FieldTemplate.Builder().number(17).name("captureDate").description("Date, capture").dimension("fixed(4)").type("date").build(),
        FieldTemplate.Builder().number(28).name("transactionFee").description("Amount, transaction fee").dimension("fixed(9)").type("xn").build(),
        FieldTemplate.Builder().number(30).name("processingFee").description("Amount, tx processing fee").dimension("fixed(9)").type("xn").build(),
        FieldTemplate.Builder().number(32).name("acquierID").description("Acquiring Institution ID").dimension("llvar(11)").type("n").build(),
        FieldTemplate.Builder().number(33).name("forwarderID").description("Forwarding Institution ID").dimension("llvar(11)").type("n").build(),
        FieldTemplate.Builder().number(37).name("rrn").description("Retrieval Reference Number").dimension("fixed(12)").type("an").build(),
        FieldTemplate.Builder().number(41).name("cardTermId").description("Card Acceptor Terminal ID").dimension("fixed(8)").type("ans").build(),
        FieldTemplate.Builder().number(42).name("cardAcceptorId").description("Card Acceptor ID Code").dimension("fixed(15)").type("ans").build(),
        FieldTemplate.Builder().number(43).name("cardAcceptorLoc").description("Card Acceptor Location Name").dimension("fixed(40)").type("CALf").build(),
        FieldTemplate.Builder().number(49).name("currencyCode").description("Currency Code, Transaction").dimension("fixed(3)").type("n").build(),
        FieldTemplate.Builder().number(54).name("addAmounts").description("Additional Amounts").dimension("lllvar(120)").type("AAf").build(),
        FieldTemplate.Builder().number(60).name("adviceCode").description("Advice/reason code").dimension("lllvar(120)").type("an").build(),
        FieldTemplate.Builder().number(102).name("accountId1").description("Account Identification 1").dimension("llvar(28)").type("ans").build()));

    return result;
  }

}
