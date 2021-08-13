package org.jml.Number.Type;

import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;

import java.math.BigDecimal;
import java.math.MathContext;

public class Float32 extends DecimalNumber {
    final public float value;

    public Float32 (float value) {
        this.value = value;
    }

    @Override
    public RealNumber add(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Float32(value + ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Float64(doubleValue() + ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value + ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value + ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new Decimal(decimalValue().add(b.decimalValue()));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().add(((Decimal) b).value));
        }

        return b.add(this);
    }

    @Override
    public RealNumber subtr(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Float32(value - ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Float64(doubleValue() - ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value - ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value - ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new Decimal(decimalValue().subtract(b.decimalValue()));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().subtract(((Decimal) b).value));
        }

        return b.subtr(this).neg();
    }

    @Override
    public RealNumber mul(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Float32(value * ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Float64(doubleValue() * ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value * ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value * ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new Decimal(decimalValue().multiply(b.decimalValue()));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().multiply(((Decimal) b).value));
        }

        return b.mul(this);
    }

    @Override
    public RealNumber div(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Float32(value / ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Float64(doubleValue() / ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value / ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value / ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().divide(b.decimalValue(), MathContext.DECIMAL128));
        } else if (type.equals(Decimal.class)) {
            MathContext context = ((Decimal) b).context;
            return new Decimal(context, decimalValue().divide(((Decimal) b).value, context));
        }

        return b.div(this).inv();
    }

    public Float32 add (float b) {
        return new Float32(value + b);
    }

    public Float32 subtr (float b) {
        return new Float32(value - b);
    }

    public Float32 mul (float b) {
        return new Float32(value * b);
    }

    public Float32 div (float b) {
        return new Float32(value / b);
    }

    @Override
    public int compareTo (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return Float.compare(value, ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return Double.compare(value, ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return Float.compare(value, ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return Double.compare(value, ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return decimalValue().compareTo(b.decimalValue());
        } else if (type.equals(Decimal.class)) {
            return decimalValue().compareTo(((Decimal) b).value);
        }

        return -b.compareTo(this);
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public Float32 neg() {
        return new Float32(-value);
    }

    @Override
    public Float32 abs() {
        return new Float32(Math.abs(value));
    }

    @Override
    public Float32 inv() {
        return new Float32(1f / value);
    }
}
