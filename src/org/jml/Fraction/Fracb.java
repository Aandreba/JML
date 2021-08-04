package org.jml.Fraction;

import org.jml.Mathx.Mathd;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Fracb extends Number implements Comparable<Fracb> {
    final private BigInteger num;
    final private BigInteger denom;

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
    public BigInteger getNum() {
        return num;
    }

    public BigInteger getDenom() {
        return denom;
    }

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

    public Fracb inverse () {
        return new Fracb(this.denom, this.num);
    }

    @Override
    public int intValue() {
        return num.divide(denom).intValue();
    }

    @Override
    public long longValue() {
        return num.divide(denom).longValue();
    }

    @Override
    public float floatValue() {
        return decimalValue(MathContext.DECIMAL32).floatValue();
    }

    @Override
    public double doubleValue() {
        return decimalValue(MathContext.DECIMAL64).doubleValue();
    }

    public BigDecimal decimalValue (MathContext context) {
        return new BigDecimal(num).divide(new BigDecimal(denom), context);
    }

    @Override
    public int compareTo (Fracb o) {
        boolean sign1 = isSigned();
        boolean sign2 = o.isSigned();

        if (sign1 && !sign2) {
            return -1;
        } else if (!sign1 && sign2) {
            return 1;
        } else if (this.denom == o.denom) {
            int comp = this.num.compareTo(o.num);
            return sign1 ? comp : -comp;
        }

        return this.num.multiply(o.denom).compareTo(o.num.multiply(this.denom));
    }

    public Frac toInt() {
        return new Frac(num.intValue(), denom.intValue());
    }

    public Fracl toLong () {
        return new Fracl(num.longValue(), denom.longValue());
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
