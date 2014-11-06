package org.nulleins.formats.iso8583;

/**
 * @author phillipsr
 */
public interface AutoGenerator {

  /** @return an auto-generated value for the <code>field</code> supplied,
    * using the named <code>autogen</code> */
  Object generate(String autogen, FieldTemplate field);

}
