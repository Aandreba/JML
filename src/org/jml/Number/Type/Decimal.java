package org.jml.Number.Type;

import org.jml.Mathx.Mathb;
import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Decimal extends DecimalNumber {
    final public MathContext context;
    final public BigDecimal value;

    public Decimal (BigDecimal value) {
        this.value = value;
        this.context = MathContext.DECIMAL128;
    }

    public Decimal (MathContext context, BigDecimal value) {
        this.context = context;
        this.value = value;
    }

    public static MathContext getContext (MathContext a, MathContext b) {
        return a.getPrecision() > b.getPrecision() ? a : b;
    }

    private MathContext getContext (Decimal other) {
        return getContext(context, other.context);
    }

    @Override
    public Decimal add (RealNumber b) {
        MathContext context = b instanceof Decimal ? getContext((Decimal) b) : this.context;
        return new Decimal(context, value.add(b.decimalValue()));
    }

    @Override
    public Decimal subtr(RealNumber b) {
        MathContext context = b instanceof Decimal ? getContext((Decimal) b) : this.context;
        return new Decimal(context, value.subtract(b.decimalValue()));
    }

    @Override
    public RealNumber mul(RealNumber b) {
        MathContext context = b instanceof Decimal ? getContext((Decimal) b) : this.context;
        return new Decimal(context, value.multiply(b.decimalValue()));
    }

    @Override
    public RealNumber div (RealNumber b) {
        MathContext context = b instanceof Decimal ? getContext((Decimal) b) : this.context;
        return new Decimal(context, value.divide(b.decimalValue(), context));
    }

    @Override
    public Decimal inv () {
        return new Decimal(context, Mathb.ONE.divide(value, context));
    }

    @Override
    public int compareTo (RealNumber o) {
        return value.compareTo(o.decimalValue());
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
        return value.toBigIntegerExact();
    }

    @Override
    public BigDecimal decimalValue() {
        return value;
    }

    @Override
    public Decimal neg() {
        return new Decimal(context, value.negate());
    }

    @Override
    public DecimalNumber abs() {
        return new Decimal(context, value.abs());
    }
}
