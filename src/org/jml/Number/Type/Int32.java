package org.jml.Number.Type;

import org.jml.Number.IntegerNumber;
import org.jml.Number.RealNumber;

import java.math.BigInteger;
import java.math.MathContext;

public class Int32 extends IntegerNumber {
    final public int value;

    public Int32(int value) {
        this.value = value;
    }

    @Override
    public RealNumber add (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Int32(value + ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Int64(value + ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value + ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value + ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new IntBig(integerValue().add(((IntBig) b).value));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().add(((Decimal) b).value));
        }

        return b.add(this);
    }

    @Override
    public RealNumber subtr (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Int32(value - ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Int64(value - ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value - ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value - ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new IntBig(integerValue().subtract(((IntBig) b).value));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().subtract(((Decimal) b).value));
        }

        return b.subtr(this).neg();
    }

    @Override
    public RealNumber mul (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Int32(value * ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Int64(value * ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value * ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value * ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new IntBig(integerValue().multiply(((IntBig) b).value));
        } else if (type.equals(Decimal.class)) {
            return new Decimal(((Decimal) b).context, decimalValue().multiply(((Decimal) b).value));
        }

        return b.mul(this);
    }

    @Override
    public RealNumber div (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Int32(value / ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return new Int64(value / ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float32(value / ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(value / ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return new IntBig(integerValue().divide(((IntBig) b).value));
        } else if (type.equals(Decimal.class)) {
            MathContext context = ((Decimal) b).context;
            return new Decimal(context, decimalValue().divide(((Decimal) b).value, context));
        }

        return b.div(this).inv();
    }

    public Int32 add (int b) {
        return new Int32(value + b);
    }

    public Int32 subtr (int b) {
        return new Int32(value - b);
    }

    public Int32 mul (int b) {
        return new Int32(value * b);
    }

    public Int32 div (int b) {
        return new Int32(value / b);
    }

    @Override
    public Int32 neg() {
        return new Int32(-value);
    }

    @Override
    public Int32 abs() {
        return new Int32(Math.abs(value));
    }

    @Override
    public Float32 inv() {
        return new Float32(1f / value);
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public BigInteger integerValue() {
        return BigInteger.valueOf(value);
    }

    @Override
    public int compareTo (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return Integer.compare(value, ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return Long.compare(value, ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return Float.compare(value, ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return Double.compare(value, ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return integerValue().compareTo(((IntBig) b).value);
        } else if (type.equals(Decimal.class)) {
            return decimalValue().compareTo(((Decimal) b).value);
        }

        return -b.compareTo(this);
    }
}
