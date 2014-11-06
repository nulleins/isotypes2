package org.nulleins.formats.iso8583;

import com.google.common.base.Preconditions;
import org.nulleins.formats.iso8583.types.Dimension;
import org.nulleins.formats.iso8583.types.MTI;

import java.text.ParseException;

/** Definition of an ISO8583 message field, capable for formatting and parsing message
  * fields, based upon its configuration
  * <p/>
  * Example:</br>
  * <code>
  * &lt;field number="2" type="LLVAR" name="CardNumber" description="Payment Card Number" /&gt;
  * </code>
  * @author phillipsr */
public class FieldTemplate {
  private String type;
  private Dimension dimension;
  private int number;
  private String name;
  private String description;
  private String defaultValue;
  private String autogenSpec;
  private boolean optional;
  private MTI messageType;
  private MessageTemplate message;

  private FieldTemplate(final int number, final String type, final Dimension dimension, final String name, final String description) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(dimension);
    Preconditions.checkNotNull(name);
    this.number = number;
    this.type = type;
    setDimension(dimension);
    this.name = name;
    this.description = description != null ? description : "";
  }

  public FieldTemplate() {
  }

  public int getNumber() {
    return number;
  }

  public String getMessageType() {
    return messageType.toString();
  }

  public void setMessageType(final String type) {
    this.messageType = MTI.create(type);
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public Dimension getDimension() {
    return dimension;
  }

  public void setDimension(final Dimension dimension) {
    this.dimension = dimension;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getAutogen() {
    return autogenSpec;
  }

  public void setAutogen(final String autogenSpec) {
    this.autogenSpec = autogenSpec;
  }

  public void setNumber(final int number) {
    this.number = number;
  }

  public boolean isOptional() {
    return optional;
  }

  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  /**
   * Use this field definition to format the data supplied
   * @param value
   * @return data
   * @throws MessageException if the formatter failed to create a field of the correct size
   */
  public byte[]
  format(final Object value) {
    final String result;
    try {
      result = new String(
          message.getFormatter(type).format(type, value, this.dimension));
    } catch (Exception e) {
      throw new IllegalStateException("Could not format data [" + value + "] for field " + this, e);
    }

    if (this.dimension.getType() == Dimension.Type.FIXED && result.length() != dimension.getLength()) {
      throw new MessageException(this + ": Formatter did not format fixed field to specified length, value=[" + result + "]");
    }
    if (this.dimension.getType() == Dimension.Type.VARIABLE && result.length() > dimension.getLength() + dimension.getVSize()) {
      throw new MessageException(this + ": Formatter exceeded maximum length for variable field; value=[" + result + "]");
    }
    return result.getBytes();
  }

  @Override
  public String toString() {
    return "Field nb=" + this.getNumber()
        + " name=" + this.getName()
        + " type=" + this.getType()
        + " dim=" + dimension
        + (autogenSpec != null ? (" autogen=[" + autogenSpec + "]") : "")
        + (defaultValue != null ? (" default=[" + defaultValue + "]") : "");
  }

  public Object parse(final byte[] data)
      throws ParseException {
    return message.getFormatter(type).parse(type, dimension, data.length, data);
  }

  /**
   * @param messageTemplate
   */
  public void setMessage(final MessageTemplate messageTemplate) {
    this.message = messageTemplate;
  }

  /**
   * @param value
   * @return
   */
  public boolean validValue(final Object value) {
    return message.getFormatter(type).isValid(value, type, dimension);
  }

  public static Builder Builder() { return new Builder(); }
  public static class Builder {
    private int number;
    private String type;
    private Dimension dimension;
    private String name;
    private String description = "";
    private String defaultValue;

    public Builder number(final int number) {
      this.number = number;
      return this;
    }
    public Builder type(final String type) {
      this.type = type;
      return this;
    }

    public Builder dimension(final Dimension dimension) {
      this.dimension = dimension;
      return this;
    }
    public Builder dimension(final String dimension) {
      return dimension(Dimension.parse(dimension));
    }
    public Builder name(final String name) {
      this.name = name;
      return this;
    }
    public Builder description(final String description) {
      this.description = description;
      return this;
    }
    public Builder defaultValue(final String value) {
      this.defaultValue = value;
      return this;
    }

    public FieldTemplate build() {
      final FieldTemplate result = new FieldTemplate(number, type, dimension, name, description);
      result.setDefaultValue(defaultValue);
      return result;
    }
  }
}
