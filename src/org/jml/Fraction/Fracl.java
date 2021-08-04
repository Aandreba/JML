package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Fracl extends Number implements Comparable<Fracl> {
    final public static Fracl PI = new Fracl(Mathf.PI);
    final public static Fracl E = new Fracl(Mathf.E);

    final private long num;
    final private long denom;

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
    public long getNum() {
        return num;
    }

    public long getDenom() {
        return denom;
    }

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
        if (this.denom == b.denom) {
            return new Fracl(this.num + b.num, this.denom);
        }

        return new Fracl(this.num * b.denom + b.num * this.denom, this.denom * b.denom);
    }

    public Fracl add (int b) {
        return new Fracl(this.num + b * this.denom, this.denom);
    }

    public Fracl subtr (Fracl b) {
        if (this.denom == b.denom) {
            return new Fracl(this.num - b.num, this.denom);
        }

        return new Fracl(this.num * b.denom - b.num * this.denom, this.denom * b.denom);
    }

    public Fracl subtr (int b) {
        return new Fracl(this.num - b * this.denom, this.denom);
    }

    public Fracl invSubtr (int b) {
        return new Fracl(b * this.denom - this.num, this.denom);
    }

    public Fracl mul (Fracl b) {
        return new Fracl(this.num * b.num, this.denom * b.denom);
    }

    public Fracl mul (int b) {
        return new Fracl(this.num * b, this.denom);
    }

    public Fracl div (Fracl b) {
        return new Fracl(this.num * b.denom, this.denom * b.num);
    }

    public Fracl div (int b) {
        return new Fracl(this.num, this.denom * b);
    }

    public Fracl invDiv (int b) {
        return new Fracl(b * this.denom, this.num);
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

    public Fracl inverse () {
        return new Fracl(this.denom, this.num);
    }

    @Override
    public int intValue() {
        return (int) (num / denom);
    }

    @Override
    public long longValue() {
        return num / denom;
    }

    @Override
    public float floatValue() {
        return (float) num / denom;
    }

    @Override
    public double doubleValue() {
        return (double) num / denom;
    }

    public BigDecimal decimalValue (MathContext context) {
        return BigDecimal.valueOf(num).divide(BigDecimal.valueOf(denom), context);
    }

    @Override
    public int compareTo (Fracl o) {
        boolean sign1 = isSigned();
        boolean sign2 = o.isSigned();

        if (sign1 && !sign2) {
            return -1;
        } else if (!sign1 && sign2) {
            return 1;
        } else if (this.denom == o.denom) {
            int comp = Long.compare(this.num, o.num);
            return sign1 ? comp : -comp;
        }

        return Long.compare(this.num * o.denom, o.num * this.denom);
    }

    public Frac toInt() {
        return new Frac((int) num, (int) denom);
    }

    public Fracb toBig () {
        return new Fracb(BigInteger.valueOf(num), BigInteger.valueOf(denom));
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
