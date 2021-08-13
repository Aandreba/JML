package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;
import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;
import org.jml.Number.Type.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Fracb extends DecimalNumber {
    final public BigInteger num;
    final public BigInteger denom;

    public Fracb (BigInteger num, BigInteger denom) {
        this.num = num;
        this.denom = denom;
    }

    public Fracb (BigDecimal value) {
        BigInteger alpha = value.unscaledValue();
        BigInteger beta = BigInteger.TEN.pow(value.scale());

        BigInteger gcd = alpha.gcd(beta);
        this.num = alpha.divide(gcd);
        this.denom = beta.divide(gcd);
    }

    // PROPERTIES
    public boolean isSigned () {
        return (num.signum() < 0) ^ (denom.signum() < 0);
    }

    public boolean isPositive () {
        return !isSigned();
    }

    public BigInteger remainder () {
        return num.remainder(denom);
    }

    // OPERATIONS
    public Fracb add (Fracb b) {
        if (this.denom.equals(b.denom)) {
            return new Fracb(this.num.add(b.num), this.denom);
        }

        return new Fracb(this.num.multiply(b.denom).add(b.num.multiply(this.denom)), this.denom.multiply(b.denom));
    }

    public Fracb add (BigInteger b) {
        return new Fracb(this.num.add(b.multiply(this.denom)), this.denom);
    }

    public Fracb subtr (Fracb b) {
        if (this.denom.equals(b.denom)) {
            return new Fracb(this.num.subtract(b.num), this.denom);
        }

        return new Fracb(this.num.multiply(b.denom).add(b.num.multiply(this.denom)), this.denom.multiply(b.denom));
    }

    public Fracb subtr (BigInteger b) {
        return new Fracb(this.num.subtract(b.multiply(this.denom)), this.denom);
    }

    public Fracb invSubtr (BigInteger b) {
        return new Fracb(b.multiply(this.denom).subtract(this.num), this.denom);
    }

    public Fracb mul (Fracb b) {
        return new Fracb(this.num.multiply(b.num), this.denom.multiply(b.denom));
    }

    public Fracb mul (BigInteger b) {
        return new Fracb(this.num.multiply(b), this.denom);
    }

    public Fracb div (Fracb b) {
        return new Fracb(this.num.multiply(b.denom), this.denom.multiply(b.num));
    }

    public Fracb div (BigInteger b) {
        return new Fracb(this.num, this.denom.multiply(b));
    }

    public Fracb invDiv (BigInteger b) {
        return new Fracb(b.multiply(this.denom), this.num);
    }

    public Fracb reduced () {
        BigInteger gcd = num.gcd(denom);
        return new Fracb(num.divide(gcd), denom.divide(gcd));
    }

    @Override
    public Frac toInt() {
        return new Frac(num.intValueExact(), denom.intValueExact());
    }

    @Override
    public Fracl toLong() {
        return new Fracl(num.longValueExact(), denom.longValueExact());
    }

    public Fracb toBig () {
        return this;
    }

    // DecimalValue Override
    @Override
    public RealNumber add (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.add(b.integerValue());
        } else if (type.equals(Int64.class)) {
            return this.add(b.integerValue());
        } else if (type.equals(Float32.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().add(b.decimalValue()));
        } else if (type.equals(Float64.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().add(b.decimalValue()));
        } else if (type.equals(IntBig.class)) {
            return this.add(b.integerValue());
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().add(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return this.add(((Frac) b).toBig());
        } else if (type.equals(Fracl.class)) {
            return this.add(((Fracl) b).toBig());
        } else if (type.equals(Fracb.class)) {
            return this.add((Fracb) b);
        }

        return b.add(this);
    }

    @Override
    public RealNumber subtr (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.subtr(b.integerValue());
        } else if (type.equals(Int64.class)) {
            return this.subtr(b.integerValue());
        } else if (type.equals(Float32.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().subtract(b.decimalValue()));
        } else if (type.equals(Float64.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().subtract(b.decimalValue()));
        } else if (type.equals(IntBig.class)) {
            return this.subtr(b.integerValue());
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().subtract(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return this.subtr(((Frac) b).toBig());
        } else if (type.equals(Fracl.class)) {
            return this.subtr(((Fracl) b).toBig());
        } else if (type.equals(Fracb.class)) {
            return this.subtr((Fracb) b);
        }

        return b.subtr(this).neg();
    }

    @Override
    public RealNumber mul (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.mul(b.integerValue());
        } else if (type.equals(Int64.class)) {
            return this.mul(b.integerValue());
        } else if (type.equals(Float32.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().multiply(b.decimalValue()));
        } else if (type.equals(Float64.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().multiply(b.decimalValue()));
        } else if (type.equals(IntBig.class)) {
            return this.mul(b.integerValue());
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().multiply(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return this.mul(((Frac) b).toBig());
        } else if (type.equals(Fracl.class)) {
            return this.mul(((Fracl) b).toBig());
        } else if (type.equals(Fracb.class)) {
            return this.mul((Fracb) b);
        }

        return b.mul(this);
    }

    @Override
    public RealNumber div(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.div(b.integerValue());
        } else if (type.equals(Int64.class)) {
            return this.div(b.integerValue());
        } else if (type.equals(Float32.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().divide(b.decimalValue(), MathContext.DECIMAL128));
        } else if (type.equals(Float64.class)) {
            return new Decimal(MathContext.DECIMAL128, decimalValue().divide(b.decimalValue(), MathContext.DECIMAL128));
        } else if (type.equals(IntBig.class)) {
            return this.div(b.integerValue());
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().divide(((Decimal) b).value, context));
        } else if (type.equals(Frac.class)) {
            return this.div(((Frac) b).toBig());
        } else if (type.equals(Fracl.class)) {
            return this.div(((Fracl) b).toBig());
        } else if (type.equals(Fracb.class)) {
            return this.div((Fracb) b);
        }

        return b.div(this).inv();
    }

    @Override
    public int compareTo (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return num.compareTo(denom.multiply(b.integerValue()));
        } else if (type.equals(Int64.class)) {
            return num.compareTo(denom.multiply(b.integerValue()));
        } else if (type.equals(Float32.class)) {
            return new BigDecimal(num).compareTo(new BigDecimal(denom).multiply(b.decimalValue()));
        } else if (type.equals(Float64.class)) {
            return new BigDecimal(num).compareTo(new BigDecimal(denom).multiply(b.decimalValue()));
        } else if (type.equals(IntBig.class)) {
            return num.compareTo(denom.multiply(b.integerValue()));
        } else if (type.equals(Decimal.class)) {
            return new BigDecimal(num).compareTo(new BigDecimal(denom).multiply(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return num.multiply(BigInteger.valueOf(((Frac) b).denom)).compareTo(denom.multiply(BigInteger.valueOf(((Frac) b).num)));
        } else if (type.equals(Fracl.class)) {
            return num.multiply(BigInteger.valueOf(((Fracl) b).denom)).compareTo(denom.multiply(BigInteger.valueOf(((Fracl) b).num)));
        } else if (type.equals(Fracb.class)) {
            return num.multiply(((Fracb) b).denom).compareTo(denom.multiply(((Fracb) b).num));
        }

        return -b.compareTo(this);
    }

    @Override
    public float floatValue() {
        return num.floatValue() / denom.floatValue();
    }

    @Override
    public double doubleValue() {
        return num.doubleValue() / denom.doubleValue();
    }

    @Override
    public BigDecimal decimalValue () {
        return decimalValue(MathContext.DECIMAL128);
    }

    public BigDecimal decimalValue (MathContext context) {
        return new BigDecimal(num).divide(new BigDecimal(denom), context);
    }

    @Override
    public Fracb neg() {
        return new Fracb(num.negate(), denom);
    }

    @Override
    public Fracb abs() {
        return new Fracb(num.abs(), denom.abs());
    }

    public Fracb inv () {
        return new Fracb(this.denom, this.num);
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
