package org.jml.Complex.Big;

import jcuda.cuComplex;
import org.jml.Complex.Double.Compd;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class Compb implements Serializable {
    final private static long serialVersionUID = -7388043871083482711L;

    final public static Compb ZERO = new Compb();
    final public static Compb ONE = new Compb(Mathb.ONE, Mathb.ZERO);
    final public static Compb ONEI = new Compb(Mathb.ZERO, Mathb.ONE);
    final public static Compb MONE = new Compb(Mathb.ONE.negate(), Mathb.ZERO);

    final public BigDecimal re, im;
    final public MathContext context;

    public Compb (BigDecimal real, BigDecimal imaginary, MathContext context) {
        this.re = real;
        this.im = imaginary;
        this.context = context;
    }

    public Compb (BigDecimal real, BigDecimal imaginary) {
        this.re = real;
        this.im = imaginary;
        this.context = MathContext.DECIMAL128;
    }

    public Compb() {
        this(Mathb.ZERO, Mathb.ZERO);
    }

    // OPERATIONS
    private MathContext operationCtx (Compb b) {
        return context.getPrecision() >= b.context.getPrecision() ? context : b.context;
    }

    public Compb add (Compb b) {
        return new Compb(re.add(b.re), im.add(b.im), operationCtx(b));
    }

    public Compb add (BigDecimal b) {
        return new Compb(re.add(b), im, context);
    }

    public Compb subtr (Compb b) {
        return new Compb(re.subtract(b.re), im.subtract(b.im), operationCtx(b));
    }

    public Compb subtr (BigDecimal b) {
        return new Compb(re.subtract(b), im, context);
    }

    public Compb invSubtr (BigDecimal b) {
        return new Compb(b.subtract(re), im, context);
    }

    public Compb mul (Compb b) {
        return new Compb(re.multiply(b.re).subtract(im.multiply(b.im)), re.multiply(b.im).add(b.re.multiply(im)), operationCtx(b));
    }

    public Compb mul (BigDecimal b) {
        return new Compb(re.multiply(b), im.multiply(b), context);
    }

    public Compb div (Compb b, MathContext ctx) {
        BigDecimal div = b.re.multiply(b.re).add(b.im.multiply(b.im));
        return new Compb(re.multiply(b.re).add(im.multiply(b.im)).divide(div, ctx), im.multiply(b.re).subtract(re.multiply(b.im)).divide(div, ctx));
    }

    public Compb div (Compb b) {
        return div(b, operationCtx(b));
    }

    public Compb div (BigDecimal b, MathContext ctx) {
        return new Compb(re.divide(b, ctx), im.divide(b, ctx));
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
        return new Compb(re.divide(xy2, ctx), im.negate().divide(xy2, ctx));
    }

    public Compb inverse () { return inverse(context); }

    public Compb sqrt (MathContext ctx) {
        if (isReal()) {
            return sqrt(re, ctx);
        }

        BigDecimal modulus = abs(ctx);
        BigDecimal sqrt2 = Mathb.TWO.sqrt(ctx);
        return new Compb(modulus.add(re).sqrt(ctx).divide(sqrt2, ctx), BigDecimal.valueOf(im.signum()).multiply(modulus.subtract(re).sqrt(ctx).divide(sqrt2, ctx)));
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
            return new Compb(Mathb.log(re, ctx), BigDecimal.ZERO);
        }

        return new Compb(Mathb.log(abs(), ctx), polarAngle(ctx));
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
            return new Compb(Mathb.pow(re, b), Mathb.ZERO);
        } else if (b == 2) {
            return new Compb(re * re - im * im, 2 * re * im);
        }

        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Compd toDouble () {
        return new Compd(re, im);
    }

    public Compb toFloat() {
        return this;
    }

    public cuComplex toCUDA () {
        return cuComplex.cuCmplx(re, im);
    }

    @Override
    public Compb clone() {
        return new Compb(re, im);
    }

    @Override
    public String toString() {
        if (Float.isNaN(re) || Float.isNaN(im)) {
            return "NaN";
        } else if (Float.isInfinite(re) || Float.isInfinite(im)) {
            return "Infinity";
        } else if (re == 0) {
            return im +"i";
        } else if (im == 0) {
            return Float.toString(re);
        } else if (im >= 0) {
            return re + " + " + im + "i";
        }

        return re + " - " + -im + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compb comp = (Compb) o;
        return Float.compare(comp.re, re) == 0 && Float.compare(comp.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    public String polarRadString() {
        float radius = polarRadius();
        float angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    public String polarDegString() {
        float radius = polarRadius();
        float angle = polarAngle();

        return radius+" + "+Mathf.toDegrees(angle)+" deg";
    }

    // STATIC FUNCTIONS
    public static Compb fromPolar (float radius, float angle) {
        float tan = Mathf.tan(angle);
        float real = Mathf.sqrt(radius * radius / (1 + tan * tan));
        float i = tan * real;

        return new Compb(real, i);
    }

    public static Compb sqrt (Compb x) {
        return x.sqrt();
    }

    public static Compb sqrt (float i) {
        if (i >= 0) {
            return new Compb(Mathf.sqrt(i), 0);
        }

        return new Compb(0, Mathf.sqrt(-i));
    }

    public static Compb exp (Compb x) {
        return x.exp();
    }

    public static Compb exp (float x) {
        return new Compb(Mathf.exp(x), 0);
    }

    public static Compb expi (BigDecimal x, MathContext ctx) {
        return new Compb(Mathb.cos(x, ctx), Mathb.sin(x, ctx));
    }

    public static Compb log (Compb x) {
        return x.log();
    }

    public static Compb log (float x) {
        return x > 0 ? new Compb(Mathf.log(x), 0) : new Compb(x, 0).log();
    }
}
