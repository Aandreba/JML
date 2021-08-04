package org.jml.Complex.Decimal;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class Compb implements Serializable {
    final private static long serialVersionUID = -7388043871083482711L;

    final public static Compb ZERO = new Compb(Mathb.ZERO, Mathb.ZERO, MathContext.UNLIMITED);
    final public static Compb ONE = new Compb(Mathb.ONE, Mathb.ZERO, MathContext.UNLIMITED);
    final public static Compb ONEI = new Compb(Mathb.ZERO, Mathb.ONE, MathContext.UNLIMITED);
    final public static Compb MONE = new Compb(Mathb.ONE.negate(), Mathb.ZERO, MathContext.UNLIMITED);

    final public BigDecimal re, im;
    final public MathContext context;

    public Compb (BigDecimal real, BigDecimal imaginary, MathContext context) {
        this.re = real;
        this.im = imaginary;
        this.context = context;
    }

    // OPERATIONS
    private MathContext operationCtx (Compb b) {
        return context.getPrecision() >= b.context.getPrecision() ? context : b.context;
    }

    public Compb add (Compb b, MathContext ctx) {
        return new Compb(re.add(b.re, ctx), im.add(b.im, ctx), ctx);
    }

    public Compb add (Compb b) {
        return add(b, operationCtx(b));
    }

    public Compb add (BigDecimal b, MathContext ctx) {
        return new Compb(re.add(b, ctx), im.round(ctx), ctx);
    }

    public Compb add (BigDecimal b) {
        return add(b, context);
    }

    public Compb subtr (Compb b, MathContext ctx) {
        return new Compb(re.subtract(b.re, ctx), im.subtract(b.im, ctx), ctx);
    }

    public Compb subtr (Compb b) {
        return subtr(b, operationCtx(b));
    }

    public Compb subtr (BigDecimal b, MathContext ctx) {
        return new Compb(re.subtract(b, ctx), im.round(ctx), ctx);
    }

    public Compb subtr (BigDecimal b) {
        return subtr(b, context);
    }

    public Compb invSubtr (BigDecimal b, MathContext ctx) {
        return new Compb(b.subtract(re, ctx), im.round(ctx), ctx);
    }

    public Compb invSubtr (BigDecimal b) {
        return invSubtr(b, context);
    }

    public Compb mul (Compb b, MathContext ctx) {
        return new Compb(re.multiply(b.re).subtract(im.multiply(b.im), ctx), re.multiply(b.im).add(b.re.multiply(im), ctx), ctx);
    }

    public Compb mul (Compb b) {
        return mul(b, operationCtx(b));
    }

    public Compb mul (BigDecimal b, MathContext ctx) {
        return new Compb(re.multiply(b, ctx), im.multiply(b, ctx), ctx);
    }

    public Compb mul (BigDecimal b) {
        return mul(b, context);
    }

    public Compb div (Compb b, MathContext ctx) {
        BigDecimal div = b.re.multiply(b.re).add(b.im.multiply(b.im));
        return new Compb(re.multiply(b.re).add(im.multiply(b.im)).divide(div, ctx), im.multiply(b.re).subtract(re.multiply(b.im)).divide(div, ctx), ctx);
    }

    public Compb div (Compb b) {
        return div(b, operationCtx(b));
    }

    public Compb div (BigDecimal b, MathContext ctx) {
        return new Compb(re.divide(b, ctx), im.divide(b, ctx), ctx);
    }

    public Compb div (BigDecimal b) {
        return div(b, context);
    }

    public Compb invDiv (BigDecimal b, MathContext ctx) {
        return inverse(ctx).mul(b);
    }

    public Compb invDiv (BigDecimal b) { return invDiv(b, context); }

    // PROPERTIES
    public boolean isReal () {
        return im.equals(Mathb.ZERO);
    }

    public BigDecimal abs (MathContext ctx) {
        return Mathb.hypot(re, im, ctx);
    }

    public BigDecimal abs () { return abs(context); }

    public BigDecimal polarAngle (MathContext ctx) {
        return Mathb.atan2(im, re, ctx);
    }

    public BigDecimal polarAngle () { return polarAngle(context); }

    // FUNCTIONS
    public Compb inverse (MathContext ctx) {
        BigDecimal xy2 = re.multiply(re).add(im.multiply(im));
        return new Compb(re.divide(xy2, ctx), im.negate().divide(xy2, ctx), ctx);
    }

    public Compb inverse () { return inverse(context); }

    public Compb sqrt (MathContext ctx) {
        if (isReal()) {
            return sqrt(re, ctx);
        }

        BigDecimal modulus = abs(ctx);
        BigDecimal sqrt2 = Mathb.TWO.sqrt(ctx);
        return new Compb(modulus.add(re).sqrt(ctx).divide(sqrt2, ctx), BigDecimal.valueOf(im.signum()).multiply(modulus.subtract(re).sqrt(ctx).divide(sqrt2, ctx)), ctx);
    }

    public Compb sqrt () {
        return sqrt(context);
    }

    public Compb cbrt (MathContext ctx) {
        if (isReal()) {
            return new Compb(Mathb.cbrt(re, ctx), BigDecimal.ZERO, ctx);
        }

        final BigDecimal pi4 = Mathb.FOUR.multiply(Mathb.pi(ctx));

        BigDecimal angle = polarAngle(ctx);
        BigDecimal radius = abs(ctx);

        BigDecimal alpha = Mathb.cbrt(radius, ctx);
        BigDecimal beta = angle.add(pi4).divide(Mathb.THREE, ctx);

        return Compb.expi(beta, ctx).mul(alpha);
    }

    public Compb cbrt () {
        return cbrt(context);
    }

    public Compb exp (MathContext ctx) {
        if (isReal()) {
            return new Compb(Mathb.exp(re, ctx), BigDecimal.ZERO, ctx);
        }

        BigDecimal ex = Mathb.exp(re, ctx);
        return  Compb.expi(im, ctx).mul(ex);
    }

    public Compb exp () {
        return exp(context);
    }

    public Compb log (MathContext ctx) {
        if (isReal() && Mathb.greaterThan(re, Mathb.ZERO)) {
            return new Compb(Mathb.log(re, ctx), BigDecimal.ZERO, ctx);
        }

        return new Compb(Mathb.log(abs(), ctx), polarAngle(ctx), ctx);
    }

    public Compb log () { return log(context); }

    public Compb conj () {
        return new Compb(re, im.negate(), context);
    }

    public Compb pow (Compb b, MathContext ctx) {
        if (b.isReal() && b.re.equals(Mathb.TWO)) {
            return new Compb(re.multiply(re).subtract(im.multiply(im)), Mathb.TWO.multiply(re).multiply(im), ctx);
        }

        return log(ctx).mul(b).exp(ctx);
    }

    public Compb pow (Compb b) { return pow(b, context); }

    public Compb pow (BigDecimal b, MathContext ctx) {
        if (isReal()) {
            return new Compb(Mathb.pow(re, b, ctx), Mathb.ZERO, ctx);
        } else if (b.equals(Mathb.TWO)) {
            return new Compb(re.multiply(re).subtract(im.multiply(im)), Mathb.TWO.multiply(re).multiply(im), ctx);
        }

        return log(ctx).mul(b).exp(ctx);
    }

    public Compb pow (BigDecimal b) {
        return pow(b, context);
    }

    // JAVA FUNCTIONS
    public Compb toContext (MathContext ctx) {
        return new Compb(re.round(ctx), im.round(ctx), ctx);
    }

    public Compd toDouble () {
        return new Compd(re.doubleValue(), im.doubleValue());
    }

    public Comp toFloat() {
        return new Comp(re.floatValue(), im.floatValue());
    }

    @Override
    public Compb clone() {
        return new Compb(re, im, context);
    }

    @Override
    public String toString() {
        if (re.equals(Mathb.ZERO)) {
            return im +"i";
        } else if (isReal()) {
            return re.toString();
        } else if (Mathb.greaterOrEqual(im, Mathb.ZERO)) {
            return re + " + " + im + "i";
        }

        return re + " - " + im.negate() + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compb compb = (Compb) o;
        return re.equals(compb.re) &&
                im.equals(compb.im);
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    public String polarRadString() {
        BigDecimal radius = abs();
        BigDecimal angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    public String polarDegString() {
        BigDecimal radius = abs();
        BigDecimal angle = polarAngle();

        return radius+" + "+Mathb.toDegrees(angle, context)+" deg";
    }

    // STATIC FUNCTIONS
    public static Compb fromPolar (BigDecimal radius, BigDecimal angle, MathContext ctx) {
        BigDecimal tan = Mathb.tan(angle, ctx);
        BigDecimal real = Mathb.sqrt(radius.multiply(radius).divide(Mathb.ONE.add(tan.multiply(tan)), ctx), ctx);
        BigDecimal i = tan.multiply(real);

        return new Compb(real, i, ctx);
    }

    public static Compb sqrt (Compb i, MathContext ctx) {
        return i.sqrt(ctx);
    }

    public static Compb sqrt (Compb i) {
        return i.sqrt(i.context);
    }

    public static Compb sqrt (BigDecimal i, MathContext ctx) {
        if (Mathb.greaterOrEqual(i, Mathb.ZERO)) {
            return new Compb(Mathb.sqrt(i, ctx), Mathb.ZERO, ctx);
        }

        return new Compb(Mathb.ZERO, Mathb.sqrt(i.negate(), ctx), ctx);
    }

    public static Compb exp (Compb x, MathContext ctx) {
        return x.exp(ctx);
    }

    public static Compb exp (Compb x) {
        return x.exp();
    }

    public static Compb exp (BigDecimal x, MathContext ctx) {
        return new Compb(Mathb.exp(x, ctx), Mathb.ZERO, ctx);
    }

    public static Compb expi (BigDecimal x, MathContext ctx) {
        return new Compb(Mathb.cos(x, ctx), Mathb.sin(x, ctx), ctx);
    }

    public static Compb log (Compb x, MathContext ctx) {
        return x.log(ctx);
    }

    public static Compb log (Compb x) {
        return x.log();
    }

    public static Compb log (BigDecimal x, MathContext ctx) {
        return Mathb.greaterThan(x, Mathb.ZERO) ? new Compb(Mathb.log(x, ctx), Mathb.ZERO, ctx) : new Compb(x, Mathb.ZERO, ctx).log();
    }
}
