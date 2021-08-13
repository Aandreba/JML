package org.jml.Number.Type;

import org.jml.Mathx.Mathb;
import org.jml.Number.DecimalNumber;
import org.jml.Number.IntegerNumber;
import org.jml.Number.RealNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class IntBig extends IntegerNumber {
    final public BigInteger value;

    public IntBig (BigInteger value) {
        this.value = value;
    }

    @Override
    public RealNumber add (RealNumber b) {
        if (b instanceof IntegerNumber) {
            return new IntBig(value.add(b.integerValue()));
        }

        return new Decimal(decimalValue().add(b.decimalValue()));
    }

    @Override
    public RealNumber subtr (RealNumber b) {
        if (b instanceof IntegerNumber) {
            return new IntBig(value.subtract(b.integerValue()));
        }

        return new Decimal(decimalValue().subtract(b.decimalValue()));
    }

    @Override
    public RealNumber mul(RealNumber b) {
        if (b instanceof IntegerNumber) {
            return new IntBig(value.multiply(b.integerValue()));
        }

        return new Decimal(decimalValue().multiply(b.decimalValue()));
    }

    @Override
    public RealNumber div (RealNumber b) {
        if (b instanceof IntegerNumber) {
            return new IntBig(value.divide(b.integerValue()));
        }

        MathContext context = b instanceof Decimal ? ((Decimal) b).context : MathContext.DECIMAL128;
        return new Decimal(context, decimalValue().divide(b.decimalValue(), context));
    }

    @Override
    public Decimal inv() {
        return new Decimal(MathContext.DECIMAL128, Mathb.ONE.divide(decimalValue(), MathContext.DECIMAL128));
    }

    @Override
    public int intValue() {
        return value.intValueExact();
    }

    @Override
    public long longValue() {
        return value.longValueExact();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public BigInteger integerValue() {
        return value;
    }

    @Override
    public IntBig neg() {
        return new IntBig(value.negate());
    }

    @Override
    public IntBig abs() {
        return new IntBig(value.abs());
    }

    @Override
    public int compareTo (RealNumber o) {
        if (o instanceof IntegerNumber) {
            return value.compareTo(o.integerValue());
        }

        return decimalValue().compareTo(o.decimalValue());
    }
}
