package com.droitfintech.dataframes;

import com.droitfintech.exceptions.DroitException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.droitfintech.regulatory.Tenor;


/**
 * Created by christopherwhinds on 7/7/16 from the OLDBox code base
 */
public class Value implements Comparable<Object> {

    public enum ValueType {
        STRING (String.class),
        BOOLEAN (Boolean.class),
        INTEGER (Integer.class),
        DATE (Date.class),
        DECIMAL (BigDecimal.class),
        TENOR (Tenor.class);

        private Class<?> clazz;
        ValueType(Class<?> clazz) {
            this.clazz = clazz;
        }
        public Class<?> getClazz() {
            return this.clazz;
        }
    }

    private Object boxedValue;
    private ValueType boxedType;

    public Value(String value, ValueType type) {
        this.boxedValue = ValueConverter.getConverter(type.getClazz()).convert(value);
        this.boxedType = type;
    }

    public Value(Object value, ValueType type) {
        if (value != null && !type.getClazz().isInstance(value)) {
            throw new DroitException("Error creating DroitValue: Expected " + type.getClazz().getSimpleName() + ", got " + value.getClass().getSimpleName());
        }
        this.boxedValue = value;
        this.boxedType = type;
    }

    public Object getUnderlying() {
        return this.boxedValue;
    }

    public ValueType getType() {
        return this.boxedType;
    }

    public String toString() {
        if (boxedValue != null) {
            return ValueConverter.toStringStatic(boxedValue);
        }
        else {
            return "!NULL";
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(15, 31)
                .append(this.boxedValue)
                .append(this.boxedType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if(obj instanceof Value) {
            if (obj.getClass() != getClass()) {
                return false;
            }
            Value rhs = (Value) obj;
            return new EqualsBuilder()
                    .append(this.boxedValue, rhs.getUnderlying())
                    .append(this.boxedType, rhs.getType())
                    .isEquals();
        } else {
            return boxedValue.equals(obj);
        }
    }

    public static List<Value> makeValueList(String values, ValueType type) {
        Set<?> s = ValueConverter.makeSet(type.getClazz(), values);
        return makeValueList(new LinkedList<Object>(s), type);
    }

    public static List<Value> makeValueList(List<Object> values, ValueType type) {
        List<Value> results = new LinkedList<Value>();
        for (Object underlying : values) {
            results.add(new Value(underlying, type));
        }
        return results;
    }

    public static String toString(List<Value> values) {
        return values.toString();
    }

    @SuppressWarnings("unchecked")
     public int compareTo(Object object) {
        if (object instanceof Value) {
            Value value = (Value) object;
            if (value.getType() != boxedType) {
                throw new DroitException("Attempted to compare " + value.getType() + " against " + boxedType);
            }
            return ((Comparable<Object>) boxedValue).compareTo(value.getUnderlying());
        } else {
            return ((Comparable<Object>) boxedValue).compareTo(object);
        }
    }

    public static ValueType getValueTypeForObject(Object object) {
        ValueType [] vals = ValueType.values();

        for(int idx = 0; idx < vals.length; idx++) {
            if(vals[idx].getClazz() == object.getClass()) {
                return vals[idx];
            }
        }
        return null;
    }
}
