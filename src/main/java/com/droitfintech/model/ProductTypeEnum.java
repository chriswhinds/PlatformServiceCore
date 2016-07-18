package com.droitfintech.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for productTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="productTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MAC"/>
 *     &lt;enumeration value="IMM"/>
 *     &lt;enumeration value="Standard"/>
 *     &lt;enumeration value="Custom"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "productTypeEnum")
@XmlEnum
public enum ProductTypeEnum {

    MAC("MAC"),
    IMM("IMM"),
    @XmlEnumValue("Standard")
    STANDARD("Standard"),
    @XmlEnumValue("Custom")
    CUSTOM("Custom");
    private final String value;

    ProductTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProductTypeEnum fromValue(String v) {
        for (ProductTypeEnum c: ProductTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
