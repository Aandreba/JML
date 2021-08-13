package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;
import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;
import org.jml.Number.Type.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Frac extends DecimalNumber {
    public static boolean autoReduce = true;

    final public static Frac PI = new Frac(Mathf.PI);
    final public static Frac E = new Frac(Mathf.E);

    final public int num, denom;

    public Frac (int num, int denom) {
        this.num = num;
        this.denom = denom;
    }

    public Frac (float value) {
        int alpha = (int) (value * 1e7);
        int beta = (int) 1e7;

        int gcd = Mathf.gcd(alpha, beta);
        this.num = alpha / gcd;
        this.denom = beta / gcd;
    }

    // PROPERTIES
    public boolean isSigned () {
        return (num < 0) ^ (denom < 0);
    }

    public boolean isPositive () {
        return !isSigned();
    }

    public int remainder () {
        return num % denom;
    }

    // OPERATIONS
    public Frac add (Frac b) {
        Frac result;
        if (this.denom == b.denom) {
            result = new Frac(this.num + b.num, this.denom);
        } else {
            result = new Frac(this.num * b.denom + b.num * this.denom, this.denom * b.denom);
        }

        return autoReduce ? result.reduced() : result;
    }

    public Frac add (int b) {
        Frac result = new Frac(this.num + b * this.denom, this.denom);
        return autoReduce ? result.reduced() : result;
    }

    public Frac subtr (Frac b) {
        Frac result;
        if (this.denom == b.denom) {
            result = new Frac(this.num - b.num, this.denom);
        } else {
            result = new Frac(this.num * b.denom - b.num * this.denom, this.denom * b.denom);
        }

        return autoReduce ? result.reduced() : result;
    }

    public Frac subtr (int b) {
        Frac result = new Frac(this.num - b * this.denom, this.denom);
        return autoReduce ? result.reduced() : result;
    }

    public Frac invSubtr (int b) {
        Frac result = new Frac(b * this.denom - this.num, this.denom);
        return autoReduce ? result.reduced() : result;
    }

    public Frac mul (Frac b) {
        Frac result = new Frac(this.num * b.num, this.denom * b.denom);
        return autoReduce ? result.reduced() : result;
    }

    public Frac mul (int b) {
        Frac result = new Frac(this.num * b, this.denom);
        return autoReduce ? result.reduced() : result;
    }

    public Frac div (Frac b) {
        Frac result = new Frac(this.num * b.denom, this.denom * b.num);
        return autoReduce ? result.reduced() : result;
    }

    public Frac div (int b) {
        Frac result = new Frac(this.num, this.denom * b);
        return autoReduce ? result.reduced() : result;
    }

    public Frac invDiv (int b) {
        Frac result = new Frac(b * this.denom, this.num);
        return autoReduce ? result.reduced() : result;
    }

    public Fracb pow (int b) {
        if (b < 0) {
            return new Fracb(Mathd.pow(this.denom, -b), Mathd.pow(this.num, -b));
        }

        return new Fracb(Mathd.pow(this.num, b), Mathd.pow(this.denom, b));
    }

    public Frac reduced () {
        int gcd = Mathf.gcd(num, denom);
        return new Frac(num / gcd, denom / gcd);
    }

    @Override
    public Frac toInt() {
        return this;
    }

    @Override
    public Fracl toLong() {
        return new Fracl(num, denom);
    }

    public Fracb toBig () {
        return new Fracb(BigInteger.valueOf(num), BigInteger.valueOf(denom));
    }

    // DecimalValue Override
    @Override
    public RealNumber add (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.add(((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return toLong().add(((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float64(doubleValue() + ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(doubleValue() + ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return toBig().add(((IntBig) b).value);
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().add(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return add((Frac) b);
        } else if (type.equals(Fracl.class)) {
            return toLong().add((Fracl) b);
        } else if (type.equals(Fracb.class)) {
            return toBig().add((Fracb) b);
        }

        return b.add(this);
    }

    @Override
    public RealNumber subtr (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.subtr(((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return toLong().subtr(((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float64(doubleValue() - ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(doubleValue() - ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return toBig().subtr(((IntBig) b).value);
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().subtract(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return subtr((Frac) b);
        } else if (type.equals(Fracl.class)) {
            return toLong().subtr((Fracl) b);
        } else if (type.equals(Fracb.class)) {
            return toBig().subtr((Fracb) b);
        }

        return b.subtr(this).neg();
    }

    @Override
    public RealNumber mul (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.mul(((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return toLong().mul(((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float64(doubleValue() * ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(doubleValue() * ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return toBig().mul(((IntBig) b).value);
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().multiply(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return mul((Frac) b);
        } else if (type.equals(Fracl.class)) {
            return toLong().mul((Fracl) b);
        } else if (type.equals(Fracb.class)) {
            return toBig().mul((Fracb) b);
        }

        return b.mul(this);
    }

    @Override
    public RealNumber div(RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return this.div(((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return toLong().div(((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return new Float64(doubleValue() / ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return new Float64(doubleValue() / ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return toBig().div(((IntBig) b).value);
        } else if (type.equals(Decimal.class)) {
            MathContext context = Decimal.getContext(MathContext.DECIMAL128, ((Decimal) b).context);
            return new Decimal(context, decimalValue().divide(((Decimal) b).value, context));
        } else if (type.equals(Frac.class)) {
            return div((Frac) b);
        } else if (type.equals(Fracl.class)) {
            return toLong().div((Fracl) b);
        } else if (type.equals(Fracb.class)) {
            return toBig().div((Fracb) b);
        }

        return b.div(this).inv();
    }

    @Override
    public int compareTo (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return Integer.compare(num, denom * ((Int32) b).value);
        } else if (type.equals(Int64.class)) {
            return Long.compare(num, denom * ((Int64) b).value);
        } else if (type.equals(Float32.class)) {
            return Float.compare(num, denom * ((Float32) b).value);
        } else if (type.equals(Float64.class)) {
            return Double.compare(num, denom * ((Float64) b).value);
        } else if (type.equals(IntBig.class)) {
            return BigInteger.valueOf(num).compareTo(BigInteger.valueOf(denom).multiply(((IntBig) b).value));
        } else if (type.equals(Decimal.class)) {
            return BigDecimal.valueOf(num).compareTo(BigDecimal.valueOf(denom).multiply(((Decimal) b).value));
        } else if (type.equals(Frac.class)) {
            return Integer.compare(num * ((Frac) b).denom, denom * ((Frac) b).num);
        } else if (type.equals(Fracl.class)) {
            return Long.compare(num * ((Fracl) b).denom, denom * ((Fracl) b).num);
        } else if (type.equals(Fracb.class)) {
            return BigInteger.valueOf(num).multiply(((Fracb) b).denom).compareTo(BigInteger.valueOf(denom).multiply(((Fracb) b).num));
        }

        return -b.compareTo(this);
    }

    @Override
    public float floatValue() {
        return (float) num / denom;
    }

    @Override
    public double doubleValue() {
        return (double) num / denom;
    }

    @Override
    public BigDecimal decimalValue () {
        return decimalValue(MathContext.DECIMAL128);
    }

    public BigDecimal decimalValue(MathContext context) {
        return BigDecimal.valueOf(num).divide(BigDecimal.valueOf(denom), context);
    }

    @Override
    public Frac neg() {
        return new Frac(-num, denom);
    }

    @Override
    public Frac abs() {
        return new Frac(Math.abs(num), Math.abs(denom));
    }

    public Frac inv () {
        return new Frac(this.denom, this.num);
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
