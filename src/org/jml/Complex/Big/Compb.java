package org.jml.Complex.Big;

import jcuda.cuComplex;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
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

    final public BigDecimal real, imaginary;
    final public MathContext context;

    public Compb (BigDecimal real, BigDecimal imaginary, MathContext context) {
        this.real = real;
        this.imaginary = imaginary;
        this.context = context;
    }

    public Compb(BigDecimal real, BigDecimal imaginary) {
        this.real = real;
        this.imaginary = imaginary;
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
        return new Compb(real.add(b.real), imaginary.add(b.imaginary), operationCtx(b));
    }

    public Compb add (BigDecimal b) {
        return new Compb(real.add(b), imaginary, context);
    }

    public Compb subtr (Compb b) {
        return new Compb(real.subtract(b.real), imaginary.subtract(b.imaginary), operationCtx(b));
    }

    public Compb subtr (BigDecimal b) {
        return new Compb(real.subtract(b), imaginary, context);
    }

    public Compb invSubtr (BigDecimal b) {
        return new Compb(b.subtract(real), imaginary, context);
    }

    public Compb mul (Compb b) {
        return new Compb(real.multiply(b.real).subtract(imaginary.multiply(b.imaginary)), real.multiply(b.imaginary).add(b.real.multiply(imaginary), operationCtx(b));
    }

    public Compb mul (BigDecimal b) {
        return new Compb(real.multiply(b), imaginary.multiply(b), context);
    }

    public Compb div (Compb b) {
        MathContext ctx = operationCtx(b);
        BigDecimal div = b.real.multiply(b.real).add(b.imaginary.multiply(b.imaginary));

        return new Compb(real.multiply(b.real).add(imaginary.multiply(b.imaginary)).divide(div, ctx), imaginary.multiply(b.real).subtract(real.multiply(b.imaginary)).divide(div, ctx));
    }

    public Compb div (BigDecimal b, MathContext ctx) {
        return new Compb(real.divide(b, ctx), imaginary.divide(b, ctx));
    }

    public Compb invDiv (BigDecimal b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public boolean isReal () {
        return imaginary.equals(Mathb.ZERO);
    }

    public BigDecimal abs() {
        return Mathb.hypot(real, imaginary, context);
    }

    public float polarAngle () {
        return Mathf.atan2(imaginary, real);
    }

    public float polarRadius () {
        return abs();
    }

    // FUNCTIONS
    public Compb inverse () {
        float xy2 = real * real + imaginary * imaginary;
        return new Compb(real / xy2, -imaginary / xy2);
    }

    public Compb sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        float modulus = abs();
        return new Compb(Mathf.sqrt(modulus + real) / Mathf.SQRT2, Mathf.signum(imaginary) * Mathf.sqrt(modulus - real) / Mathf.SQRT2);
    }

    public Compb cbrt () {
        final float pi4 = 2 * Mathf.PI2;

        if (imaginary == 0) {
            return new Compb(Mathf.cbrt(real), 0);
        }

        float angle = polarAngle();
        float radius = polarRadius();

        float alpha = Mathf.cbrt(radius);
        float beta = (angle + pi4) / 3;

        return new Compb(alpha * Mathf.cos(beta), alpha * Mathf.sin(beta));
    }

    public Compb exp () {
        if (imaginary == 0) {
            return new Compb(Mathf.exp(real), 0);
        }

        float ex = Mathf.exp(real);
        return new Compb(ex * Mathf.cos(imaginary), ex * Mathf.sin(imaginary));
    }

    public Compb log () {
        if (imaginary == 0 && real > 0) {
            return new Compb(Mathf.log(real), 0);
        }

        return new Compb(Mathf.log(polarRadius()), polarAngle());
    }

    public Compb conj () {
        return new Compb(real, -imaginary);
    }

    public Compb pow (Compb b) {
        if (b.isReal() && b.real == 2) {
            return new Compb(real * real - imaginary * imaginary, 2 * real * imaginary);
        }

        return log().mul(b).exp();
    }

    public Compb pow (float b) {
        if (imaginary == 0) {
            return new Compb(Mathf.pow(real, b), 0);
        } else if (b == 2) {
            return new Compb(real * real - imaginary * imaginary, 2 * real * imaginary);
        }

        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Compd toDouble () {
        return new Compd(real, imaginary);
    }

    public Compb toFloat() {
        return this;
    }

    public cuComplex toCUDA () {
        return cuComplex.cuCmplx(real, imaginary);
    }

    @Override
    public Compb clone() {
        return new Compb(real, imaginary);
    }

    @Override
    public String toString() {
        if (Float.isNaN(real) || Float.isNaN(imaginary)) {
            return "NaN";
        } else if (Float.isInfinite(real) || Float.isInfinite(imaginary)) {
            return "Infinity";
        } else if (real == 0) {
            return imaginary+"i";
        } else if (imaginary == 0) {
            return Float.toString(real);
        } else if (imaginary >= 0) {
            return real + " + " + imaginary + "i";
        }

        return real + " - " + -imaginary + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compb comp = (Compb) o;
        return Float.compare(comp.real, real) == 0 && Float.compare(comp.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
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

    public static Compb expi (float x) {
        return new Compb(Mathf.cos(x), Mathf.sin(x));
    }

    public static Compb log (Compb x) {
        return x.log();
    }

    public static Compb log (float x) {
        return x > 0 ? new Compb(Mathf.log(x), 0) : new Compb(x, 0).log();
    }
}
