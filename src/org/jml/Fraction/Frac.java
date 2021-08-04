package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Frac extends Number implements Comparable<Frac> {
    final public static Frac PI = new Frac(Mathf.PI);
    final public static Frac E = new Frac(Mathf.E);

    final private int num, denom;

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
    public int getNum() {
        return num;
    }

    public int getDenom() {
        return denom;
    }

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
        if (this.denom == b.denom) {
            return new Frac(this.num + b.num, this.denom);
        }

        return new Frac(this.num * b.denom + b.num * this.denom, this.denom * b.denom);
    }

    public Frac add (int b) {
        return new Frac(this.num + b * this.denom, this.denom);
    }

    public Frac subtr (Frac b) {
        if (this.denom == b.denom) {
            return new Frac(this.num - b.num, this.denom);
        }

        return new Frac(this.num * b.denom - b.num * this.denom, this.denom * b.denom);
    }

    public Frac subtr (int b) {
        return new Frac(this.num - b * this.denom, this.denom);
    }

    public Frac invSubtr (int b) {
        return new Frac(b * this.denom - this.num, this.denom);
    }

    public Frac mul (Frac b) {
        return new Frac(this.num * b.num, this.denom * b.denom);
    }

    public Frac mul (int b) {
        return new Frac(this.num * b, this.denom);
    }

    public Frac div (Frac b) {
        return new Frac(this.num * b.denom, this.denom * b.num);
    }

    public Frac div (int b) {
        return new Frac(this.num, this.denom * b);
    }

    public Frac invDiv (int b) {
        return new Frac(b * this.denom, this.num);
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

    public Frac inverse () {
        return new Frac(this.denom, this.num);
    }

    @Override
    public int intValue() {
        return num / denom;
    }

    @Override
    public long longValue() {
        return (long) num / denom;
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
    public int compareTo (Frac o) {
        boolean sign1 = isSigned();
        boolean sign2 = o.isSigned();

        if (sign1 && !sign2) {
            return -1;
        } else if (!sign1 && sign2) {
            return 1;
        } else if (this.denom == o.denom) {
            int comp = Integer.compare(this.num, o.num);
            return sign1 ? comp : -comp;
        }

        return Integer.compare(this.num * o.denom, o.num * this.denom);
    }

    public Fracl toLong () {
        return new Fracl(num, denom);
    }

    public Fracb toBig () {
        return new Fracb(BigInteger.valueOf(num), BigInteger.valueOf(denom));
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
