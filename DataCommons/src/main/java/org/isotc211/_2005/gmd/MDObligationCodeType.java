//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.24 at 09:50:34 AM AEDT 
//


package org.isotc211._2005.gmd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MD_ObligationCode_Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MD_ObligationCode_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="mandatory"/>
 *     &lt;enumeration value="optional"/>
 *     &lt;enumeration value="conditional"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MD_ObligationCode_Type")
@XmlEnum
public enum MDObligationCodeType {

    @XmlEnumValue("mandatory")
    MANDATORY("mandatory"),
    @XmlEnumValue("optional")
    OPTIONAL("optional"),
    @XmlEnumValue("conditional")
    CONDITIONAL("conditional");
    private final String value;

    MDObligationCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MDObligationCodeType fromValue(String v) {
        for (MDObligationCodeType c: MDObligationCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
