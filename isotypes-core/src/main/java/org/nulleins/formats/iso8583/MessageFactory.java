package org.nulleins.formats.iso8583;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import org.apache.commons.beanutils.PropertyUtils;
import org.nulleins.formats.iso8583.formatters.TypeFormatter;
import org.nulleins.formats.iso8583.formatters.TypeFormatters;
import org.nulleins.formats.iso8583.io.BCDMessageWriter;
import org.nulleins.formats.iso8583.io.CharMessageWriter;
import org.nulleins.formats.iso8583.io.MessageWriter;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.CharEncoder;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.MTI;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;


/** ISO8583 Message factory, configured with a f of message templates (a schema),
 * capable to creating and parsing ISO8583 messages
 * <p/>
 * Usually configured via an XML specification (<code>&lt;iso:schema&gt;</code>), it may also be
 * created by setting the following fields and calling <code>initialize()</code>:
 * <dl>
 * <dt>header</dt><dd>Text header to prepend to messages, e.g., ISO015000077</dd>
 * <dt>messages</dt><dd>A map of MTI:MessageTemplates, defining the messages
 * understood by this factory instance</dd>
 * <dt>contentType</dt><dd>Enumeration specifying the content type of messages
 * created or parsed (one of BCD, ASCII, EBCDIC)</dd>
 * <dt>bitmapType</dt><dd>Type of bitmap to be used, one of BINARY, HEX</dd>
 * </dl>
 * @author phillipsr */
public class MessageFactory {
  private final Map<MTI, MessageTemplate> messages = new HashMap<MTI, MessageTemplate>();
  private BitmapType bitmapType = BitmapType.HEX;
  private ContentType contentType = ContentType.TEXT;
  private CharEncoder charset = CharEncoder.ASCII;
  private String header = "";
  private String description;
  private String id;
  private boolean strict = Boolean.TRUE;
  private TypeFormatters formatters;
  private MessageParser parser;

  private Optional<AutoGeneratorFactory> autoGenerator = Optional.absent();

  @PostConstruct
  public void initialize() {
    if (messages == null) {
      throw new IllegalStateException("Factory has no message definitions: cannot be initialized");
    }
    if (formatters == null) {
      formatters = new TypeFormatters(charset);
    }
    if (parser == null) {
      parser = MessageParser.create(header, messages, contentType, charset, bitmapType);
    }
  }

  public boolean isStrict() {
    return strict;
  }

  public void setStrict(final boolean strict) {
    this.strict = strict;
  }

  /** @return the default bitmap type used in this factory */
  public BitmapType getBitmapType() {
    return bitmapType;
  }

  /** Set the bitmap type, one of BINARY or HEX
   * @param bitmapType
   * @throws IllegalArgumentException if bitmapType is null */
  public void setBitmapType(final BitmapType bitmapType) {
    Preconditions.checkNotNull(bitmapType, "bitmapType may not be null");
    this.bitmapType = bitmapType;
  }

  /** Answer with the default message context type used in this factory */
  public ContentType getContentType() {
    return contentType;
  }

  /** Set the message (numeric) content type, one of ASCII, EBCDIC or BCD
   * @param contentType
   * @throws IllegalArgumentException if the content type is null */
  public void setContentType(final ContentType contentType) {
    Preconditions.checkNotNull(contentType, "contentType cannot not be null, must be one of: " + Arrays.toString(ContentType.values()));
    this.contentType = contentType;
  }

  public CharEncoder getCharset() {
    return charset;
  }

  public void setCharset(final CharEncoder charset) {
    Preconditions.checkNotNull(charset, "charset cannot be null");
    this.charset = charset;
  }

  /** @return the header field value used (can be null) */
  public String getHeader() {
    return header;
  }

  /** Set the value of the header field, prepended to messages generated by,
   * and expected at the start of messages parsed by this factory
   * @param header field value (can be null or empty) */
  public void setHeader(final String header) {
    this.header = header;
  }

  /** @return the text desc of this factory: not used in message creatio */
  public String getDescription() {
    return description;
  }

  /** Set the desc of this factory; this is for documentary purposes, and is
   * usually set from with the iso:schema XML element
   * @param description */
  public void setDescription(final String description) {
    this.description = description;
  }

  /** @return the Spring bean ID */
  public String getId() {
    return id;
  }

  /** Set the Spring bean ID for this factory; typically only used
   * for referencing the factory bean for Spring usage
   * @param id */
  public void setId(final String id) {
    this.id = id;
  }

  /** Set Auto-generator to use for the automatic generation of field values;
   * this is an optional dependency, and will only be used of fields are
   * defined in the schema with an 'autogen' property.
   * </p>
   * The usual way to set this field is via Spring IoC */
  public void setAutoGeneratorFactory(final AutoGeneratorFactory autoGenerator) {
    this.autoGenerator = Optional.of(autoGenerator);
  }

  /** @return the ISO8583 messages defined in this factory's schema */
  public Collection<MessageTemplate> getMessages() {
    return messages.values();
  }

  /** Add a message to this factory's schema
   * @param message */
  public void addMessage(final MessageTemplate message) {
    message.setSchema(this);
    this.messages.put(message.getMessageTypeIndicator(), message);
  }

  /** @return a string representation of this message factory */
  @Override
  public String toString() {
    return "MessageFactory id=" + getId()
        + " desc='" + getDescription() + "'"
        + " header=" + getHeader()
        + " contentType=" + getContentType()
        + " charset=" + getCharset()
        + " bitmapType=" + getBitmapType()
        + (messages != null ? (" messages# " + messages.size()) : "");
  }

  /** @return a new ISO8583 message instance of the type requested, setting the field values
   * from the supplied parameter map, keyed by field f, matching
   * <code>&lt;iso:message&gt;</code> configuration for this message type
   * @param type   type of message to create
   * @param params field value to include in message, indexed by field f
   * @throws IllegalArgumentException - if the supplied MTI is null */
  public Message createByNumbers(final MTI type, final Map<Integer, Object> params) {
    final Message result = new Message(type, header);
    result.setFields(params);
    result.setTemplate(messages.get(type));
    return result;
  }

  /** Write a message to the supplied <code>output</code> stream
   * @param message
   * @param output
   * @throws java.io.IOException
   * @see #writeFromNumberMap(org.nulleins.formats.iso8583.types.MTI, java.util.Map, java.io.OutputStream) */
  public void writeToStream(final Message message, final OutputStream output) throws IOException {
    writeFromNumberMap(message.getMTI(), message.getFields(), output);
  }

  /** Create a message for the type and parameters specified and write it to the <code>output</code> stream
   * @param type   of the message to be written
   * @param params map of field # to field value (maybe updated if autogen or default required)
   * @param output stream to write formatted ISO8583 message onto
   * @throws java.io.IOException      if writing to the output stream fails for any reason
   * @throws IllegalArgumentException if the type supplied is not defined in this factory's schema,
   *                                  the output stream is null or null/empty message parameters have been supplied */
  public void writeFromNumberMap(final MTI type, final Map<Integer, Object> params, final OutputStream output)
      throws IOException {
    Preconditions.checkArgument(messages.containsKey(type), "Message not defined for MTI=" + type);
    Preconditions.checkNotNull(output, "Output stream cannot be null");
    Preconditions.checkArgument(params != null && !params.isEmpty(), "Message parameters are required");

    final MessageTemplate template = messages.get(type);
    final MessageWriter writer = getOutputWriter(contentType, charset);
    final DataOutputStream dos = getDataOutputStream(output);

    writer.appendHeader(header, dos);
    writer.appendMTI(type, dos);
    writer.appendBitmap(template.getBitmap(), bitmapType, dos);

    // Iterate over the fields in order of field f,
    // appending the field's data to the output stream
    for (final Integer key : new TreeSet<>(template.getFields().keySet())) {
      final FieldTemplate value1 = template.getFields().get(key);
      final Object key1 = params.get(key);
      final Object value = writeField(key1, writer, dos, value1);
      // update parameter map with possibly autogen'd/default value, for consistency
      params.put(key, value);
    }

    dos.flush();
  }

  private Object writeField(final Object param, final MessageWriter writer, final DataOutputStream dos, final FieldTemplate field)
      throws IOException {
    Object data = param;
    if (data == null && !field.isOptional()) {
      // first, try to autogen, and then fall back to default (if any)
      final String autogen = field.getAutogen();
      if (autogen != null && !autogen.isEmpty()) {
        if (!autoGenerator.isPresent()) {
          throw new IllegalStateException(
              "Message requires AutoGen field, but the (optional) AutoGenerator has not been set in the MessageFactory");
        }
        data = autoGenerator.get().generate(autogen, field);
      }
      if (data == null) {
        data = field.getDefaultValue();
      }
      if (data == null) {
        throw new MessageException("Value is <null> for field: " + field);
      }
    }
    if (data != null) {
      writer.appendField(field, data, dos);
    }
    return data;
  }

  /** @return the supplied output stream wrapped in a DataOutputStream, if required
   * @param output */
  private DataOutputStream getDataOutputStream(final OutputStream output) {
    if (output instanceof DataOutputStream) {
      return (DataOutputStream) output;
    }
    return new DataOutputStream(output);
  }

  /** @return the appropriate message writer for the supplied content type
   * @param contentType
   * @param charset
   *
   * @throws MessageException if no output writer is defined for the context type supplied */
  private MessageWriter getOutputWriter(final ContentType contentType, final CharEncoder charset) {
    switch (contentType) {
      case TEXT:
        return new CharMessageWriter(charset);
      case BCD:
        return new BCDMessageWriter(charset);
      default:
        throw new MessageException("No MessageWriter defined for content=" + contentType);
    }
  }

  /** @return a new message instance of the specified <code>type</code>, setting the field
   * values from properties of the <code>bean</code>, as named in the Message template
   * 'name' field
   * @param type (MTI) of ISO message to create
   * @param bean holding value to populate message fields
   *
   * @throws IllegalArgumentException if the type supplied is not defined in this factory's schema */
  public Message createFromBean(final MTI type, final Object bean) {
    Preconditions.checkArgument(messages.containsKey(type), "Message not defined for MTI=" + type);
    return createByNumbers(type,
        Maps.transformEntries(messages.get(type).getFields(), mapBeanValues(bean)));
  }

  /** @return the template registered against <code>type</code> */
  public MessageTemplate getTemplate(final MTI type) {
    return messages.get(type);
  }

  /**
   * @param formatter
   */
  public void addFormatter(final String type, TypeFormatter<?> formatter) {
    formatters.setFormatter(type, formatter);
  }

  /** @return the formatter for message <code>type</code> */
  TypeFormatter<?> getFormatter(final String type) {
    try {
      return formatters.getFormatter(type);
    } catch (final Exception e) {
      throw new MessageException(
          "No formatter registered for field type=[" + type + "] in " + formatters, e);
    }
  }

  /** @return a message parsed from the supplied <code>bytes</code> array (message data)
   * @throws java.text.ParseException
   * @throws java.io.IOException */
  public Message parse(final byte[] bytes) throws ParseException, IOException {
    return this.parse(new ByteArrayInputStream(bytes));
  }

  /** @return A message representation, parsed from the supplied input stream
   * @param input stream from which an ISO8583 message can be read
   *
   * @throws java.text.ParseException           if the input message is not well-formed or does not
   *                                  conform to the message specification configured
   * @throws IllegalArgumentException if the input stream supplied is null
   * @throws java.io.IOException              when an error occurs reading from the input stream */
  public Message parse(final InputStream input) throws ParseException, IOException {
    Preconditions.checkNotNull(input, "Input stream cannot be null");
    final DataInputStream dis;
    if (!(input instanceof DataInputStream)) {
      dis = new DataInputStream(input);
    } else {
      dis = (DataInputStream) input;
    }
    final Message result = parser.parse(dis);
    result.setTemplate(messages.get(result.getMTI()));
    return result;
  }

  /** @return an ISO8583 message of the type requested, setting the field values
   * from the supplied parameter map, matching the names in the
   * <code>&lt;iso:message&gt;</code> configuration for this message type
   * @param type   MTI of the message to be created
   * @param params map of message fields, keyed by names
   * @throws IllegalArgumentException if the type is not defined in this factory's schema */
  public Message createByNames(final MTI type, final Map<String, Object> params) {
    Preconditions.checkArgument(messages.containsKey(type), "Message not defined for MTI=" + type);
    // convert the name map supplied to a field f keyed map
    return createByNumbers(type,
        Maps.transformEntries(messages.get(type).getFields(), mapValuesByName(params)));
  }

  /** @return an empty ISO8583 message of the type requested, from the configured
   * <code>&lt;iso:message&gt;</code> template
   * @param mti type of message */
  public Message create(final MTI mti) {
    final MessageTemplate template = messages.get(mti);
    final Message result = new Message(mti, template.getHeader());
    result.setTemplate(template);
    return result;
  }

  /** @return a duplicate of <code>source</code>, but using the <code>messageType</code> specified
   * (usually a response), setting its fields from the other fields ("move-corresponding" semantics)
   * <p/>
   * Note: message unlikely to be valid until fields add/removed
   * @param messageType    type of target message
   * @param source message to duplicate
   * @throws IllegalArgumentException if the mti supplied is not defined in this factory's schema */
  public Message duplicate(final MTI messageType, final Message source) {
    Preconditions.checkArgument(messages.containsKey(messageType), "Message type [" + messageType + "] not defined in factory");
    final MessageTemplate template = messages.get(messageType);
    final Map<Integer, Object> fields = Maps.transformEntries(
        Maps.filterEntries(template.getFields(), fieldPresent(source)), mapValuesByNumber(source));
    return source.asType(messageType, template, fields);
  }

  /** @return predicate evaluating to true if field present in both <code>message</code> and <code>template</code> */
  private static Predicate<Entry<Integer, FieldTemplate>> fieldPresent(final Message message) {
    return new Predicate<Entry<Integer, FieldTemplate>>() {
      @Override
      public boolean apply(final Entry<Integer, FieldTemplate> entry) {
        return message.isFieldPresent(entry.getValue().getNumber());
      }
    };
  }

  private static EntryTransformer<Integer, FieldTemplate, Object> mapValuesByNumber(final Message message) {
    return new EntryTransformer<Integer, FieldTemplate, Object>() {
      @Override
      public Object transformEntry(final Integer number, final FieldTemplate field) {
        return message.getFieldValue(field.getNumber());
      }
    };
  }

  private static EntryTransformer<Integer, FieldTemplate, Object> mapValuesByName(final Map<String, Object> params) {
    return new EntryTransformer<Integer, FieldTemplate, Object>() {
      @Override
      public Object transformEntry(final Integer number, final FieldTemplate field) {
        return params.get(field.getName());
      }
    };
  }

  private static EntryTransformer<Integer, FieldTemplate, Object> mapBeanValues(final Object bean) {
    return new EntryTransformer<Integer, FieldTemplate, Object>() {
      @Override
      public Object transformEntry(final Integer number, final FieldTemplate field) {
        try {
          return PropertyUtils.getProperty(bean, field.getName());
        } catch (final Exception e) {
          // ignore, as this value may be set later as a protocol parameter
          return null;
        }
      }
    };
  }

  /** @return byte array of message data, either text or binary depending upon the
   * content type specified in the iso:schema in the configuration
   * @param message ISO8583 message to convert to a byte array
   * @throws MessageException if an error occurred creating the byte representation of the message */
  public byte[] getMessageData(final Message message) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      this.writeToStream(message, new DataOutputStream(baos));
      return baos.toByteArray();
    } catch (final IOException e) {
      throw new MessageException("Failed to translate message to byte stream", e);
    }
  }

  public void addMessages(final List<MessageTemplate> messages) {
    for (final MessageTemplate message : messages) {
      addMessage(message);
    }
  }

  public static Builder Builder() {
    return new Builder();
  }

  public boolean canBuild(final MTI messageType) {
    return messages.containsKey(messageType);
  }

  public static class Builder {
    private String id;
    private ContentType contentType;
    private BitmapType bitmapType;
    private String description;
    private String header;
    private Boolean strict;
    private CharEncoder charset;

    public Builder id(final String id) {
      this.id = id;
      return this;
    }

    public Builder contentType(final ContentType contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder bitmapType(final BitmapType bitmapType) {
      this.bitmapType = bitmapType;
      return this;
    }

    public Builder description(final String description) {
      this.description = description;
      return this;
    }

    public Builder header(final String header) {
      this.header = header;
      return this;
    }

    public Builder strict(final boolean strict) {
      this.strict = strict;
      return this;
    }

    public Builder charset(final CharEncoder charset) {
      this.charset = charset;
      return this;
    }


    public MessageFactory build() {
      Preconditions.checkNotNull(id);
      Preconditions.checkNotNull(contentType);
      Preconditions.checkNotNull(bitmapType);
      final MessageFactory result = new MessageFactory();
      result.setId(id);
      result.setContentType(contentType);
      result.setBitmapType(bitmapType);
      if (strict != null) {
        result.setStrict(strict);
      }
      if (description != null) {
        result.setDescription(description);
      }
      if (header != null) {
        result.setHeader(header);
      }
      if (charset != null) {
        result.setCharset(charset);
      }

      result.initialize();
      return result;
    }
  }

}
