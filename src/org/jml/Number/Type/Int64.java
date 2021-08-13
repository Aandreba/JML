package org.jml.Number.Type;

import org.jml.Number.IntegerNumber;
import org.jml.Number.RealNumber;

import java.math.BigInteger;
import java.math.MathContext;

public class Int64 extends IntegerNumber {
    final public long value;

    public Int64 (long value) {
        this.value = value;
    }

    @Override
    public RealNumber add (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return new Int64(value + ((Int32) b).value);
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
            return new Int64(value - ((Int32) b).value);
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
            return new Int64(value * ((Int32) b).value);
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
            return new Int64(value / ((Int32) b).value);
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

    public Int64 add (long b) {
        return new Int64(value + b);
    }

    public Int64 subtr (long b) {
        return new Int64(value - b);
    }

    public Int64 mul (long b) {
        return new Int64(value * b);
    }

    public Int64 div (long b) {
        return new Int64(value / b);
    }

    @Override
    public Int64 neg() {
        return new Int64(-value);
    }

    @Override
    public Int64 abs() {
        return new Int64(Math.abs(value));
    }

    @Override
    public Float64 inv() {
        return new Float64(1d / value);
    }

    @Override
    public int intValue() {
        return (int) value;
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
            return Long.compare(value, ((Int32) b).value);
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
