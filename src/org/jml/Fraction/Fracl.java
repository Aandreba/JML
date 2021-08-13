package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;
import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;
import org.jml.Number.Type.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Fracl extends DecimalNumber {
    final public static Fracl PI = new Fracl(Mathf.PI);
    final public static Fracl E = new Fracl(Mathf.E);

    final public long num;
    final public long denom;

    public Fracl(long num, long denom) {
        this.num = num;
        this.denom = denom;
    }

    public Fracl(double value) {
        long alpha = (long) (value * 1e16);
        long beta = (long) 1e16;

        long gcd = Mathd.gcd(alpha, beta);
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

    public long remainder () {
        return num % denom;
    }

    // OPERATIONS
    public Fracl add (Fracl b) {
        Fracl result;
        if (this.denom == b.denom) {
            result = new Fracl(this.num + b.num, this.denom);
        } else {
            result = new Fracl(this.num * b.denom + b.num * this.denom, this.denom * b.denom);
        }

        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl add (long b) {
        Fracl result = new Fracl(this.num + b * this.denom, this.denom);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl subtr (Fracl b) {
        Fracl result;
        if (this.denom == b.denom) {
            result = new Fracl(this.num - b.num, this.denom);
        } else {
            result = new Fracl(this.num * b.denom - b.num * this.denom, this.denom * b.denom);
        }

        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl subtr (long b) {
        Fracl result = new Fracl(this.num - b * this.denom, this.denom);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl invSubtr (long b) {
        Fracl result = new Fracl(b * this.denom - this.num, this.denom);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl mul (Fracl b) {
        Fracl result = new Fracl(this.num * b.num, this.denom * b.denom);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl mul (long b) {
        Fracl result = new Fracl(this.num * b, this.denom);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl div (Fracl b) {
        Fracl result = new Fracl(this.num * b.denom, this.denom * b.num);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl div (long b) {
        Fracl result = new Fracl(this.num, this.denom * b);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracl invDiv (long b) {
        Fracl result = new Fracl(b * this.denom, this.num);
        return Frac.autoReduce ? result.reduced() : result;
    }

    public Fracb pow (int b) {
        if (b < 0) {
            return new Fracb(Mathd.pow(this.denom, -b), Mathd.pow(this.num, -b));
        }

        return new Fracb(Mathd.pow(this.num, b), Mathd.pow(this.denom, b));
    }

    public Fracl reduced () {
        long gcd = Mathd.gcd(num, denom);
        return new Fracl(num / gcd, denom / gcd);
    }

    @Override
    public Frac toInt() {
        return new Frac((int) num, (int) denom);
    }

    @Override
    public Fracl toLong() {
        return this;
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
            return this.add(((Int64) b).value);
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
            return this.add(((Frac) b).toLong());
        } else if (type.equals(Fracl.class)) {
            return this.add((Fracl) b);
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
            return this.subtr(((Int64) b).value);
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
            return subtr(((Frac) b).toLong());
        } else if (type.equals(Fracl.class)) {
            return this.subtr((Fracl) b);
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
            return this.mul(((Int64) b).value);
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
            return mul(((Frac) b).toLong());
        } else if (type.equals(Fracl.class)) {
            return this.mul((Fracl) b);
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
            return this.div(((Int64) b).value);
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
            return div(((Frac) b).toLong());
        } else if (type.equals(Fracl.class)) {
            return this.div((Fracl) b);
        } else if (type.equals(Fracb.class)) {
            return toBig().div((Fracb) b);
        }

        return b.div(this).inv();
    }

    @Override
    public int compareTo (RealNumber b) {
        Class<? extends RealNumber> type = b.getClass();

        if (type.equals(Int32.class)) {
            return Long.compare(num, denom * ((Int32) b).value);
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
            return Long.compare(num * ((Frac) b).denom, denom * ((Frac) b).num);
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
    public Fracl neg() {
        return new Fracl(-num, denom);
    }

    @Override
    public Fracl abs() {
        return new Fracl(Math.abs(num), Math.abs(denom));
    }

    public Fracl inv () {
        return new Fracl(this.denom, this.num);
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
