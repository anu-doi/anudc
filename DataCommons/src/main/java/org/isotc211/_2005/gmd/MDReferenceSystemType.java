//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.24 at 09:50:34 AM AEDT 
//


package org.isotc211._2005.gmd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.isotc211._2005.gco.AbstractObjectType;


/**
 * <p>Java class for MD_ReferenceSystem_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MD_ReferenceSystem_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="referenceSystemIdentifier" type="{http://www.isotc211.org/2005/gmd}RS_Identifier_PropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MD_ReferenceSystem_Type", propOrder = {
    "referenceSystemIdentifier"
})
public class MDReferenceSystemType
    extends AbstractObjectType
{

    protected RSIdentifierPropertyType referenceSystemIdentifier;

    /**
     * Gets the value of the referenceSystemIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link RSIdentifierPropertyType }
     *     
     */
    public RSIdentifierPropertyType getReferenceSystemIdentifier() {
        return referenceSystemIdentifier;
    }

    /**
     * Sets the value of the referenceSystemIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link RSIdentifierPropertyType }
     *     
     */
    public void setReferenceSystemIdentifier(RSIdentifierPropertyType value) {
        this.referenceSystemIdentifier = value;
    }

}
