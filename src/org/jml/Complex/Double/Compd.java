package org.jml.Complex.Double;
import org.jml.Complex.Decimal.Compb;
import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathd;
import jcuda.cuDoubleComplex;
import org.jml.Mathx.Mathf;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * The type Compd is the representation of a complex number in doubles
 */
public class Compd implements Serializable {
    final private static long serialVersionUID = -1317134012145987898L;

    final public static Compd ZERO = new Compd();
    final public static Compd ONE = new Compd(1,0);
    final public static Compd ONEI = new Compd(0,1);
    final public static Compd MONE = new Compd(-1,0);

    final public static Compd PI = new Compd(Math.PI, 0);
    final public static Compd E = new Compd(Math.E, 0);

    /**
     * The real part
     */
    final public double re, /**
     * The imaginary part
     */
    im;

    public Compd(double real, double imaginary) {
        this.re = real;
        this.im = imaginary;
    }

    public Compd(cuDoubleComplex cuda) {
        this.re = cuda.x;
        this.im = cuda.y;
    }

    public Compd() {
        this(0, 0);
    }

    /**
     * Add 2 complex numbers
     */
    // OPERATIONS
    public Compd add (Compd b) {
        return new Compd(re + b.re, im + b.im);
    }

    /**
     * Add a real number to a complex number
     */
    public Compd add (double b) {
        return new Compd(re + b, im);
    }

    /**
     * Subtract 2 complex numbers
     */
    public Compd subtr (Compd b) {
        return new Compd(re - b.re, im - b.im);
    }

    /**
     * Subtract a real number from a complex number
     */
    public Compd subtr (double b) {
        return new Compd(re - b, im);
    }

    /**
     * Subtract a complex number from a real number
     */
    public Compd invSubtr (double b) {
        return new Compd(b - re, im);
    }

    /**
     * Multiply 2 complex numbers
     */
    public Compd mul (Compd b) {
        return new Compd(re * b.re - im * b.im, re * b.im + b.re * im);
    }

    /**
     * Multiply a real number and a complex number
     */
    public Compd mul (double b) {
        return new Compd(re * b, im * b);
    }

    /**
     * Divide 2 complex numbers
     */
    public Compd div (Compd b) {
        double div = b.re * b.re + b.im * b.im;
        return new Compd((re * b.re + im * b.im) / div, (im * b.re - re * b.im) / div);
    }

    /**
     * Divide a complex number by a real number
     */
    public Compd div (double b) {
        return mul(1 / b);
    }

    /**
     * Divide a real number by a complex number
     */
    public Compd invDiv (double b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public boolean isReal () {
        return im == 0;
    }

    /**
     * Returns {@code true} if the specified number is a
     * Not-a-Number (NaN) value, {@code false} otherwise.
     */
    public boolean isNan () {
        return Double.isNaN(re) || Double.isNaN(im);
    }

    /**
     * Returns {@code true} if the specified number is infinite, {@code false} otherwise.
     */
    public boolean isInfinite () {
        return Double.isInfinite(re) || Double.isInfinite(im);
    }

    /**
     * Returns the complex number's modulus
     */
    public double abs () {
        return Math.hypot(re, im);
    }

    /**
     * Returns the complex number's angle in polar coordinates
     */
    public double polarAngle () {
        return Math.atan2(im, re);
    }

    /**
     * Returns the inverse of the complex number (1 / z)
     */
    // FUNCTIONS
    public Compd inverse () {
        double xy2 = re * re + im * im;
        return new Compd(re / xy2, -im / xy2);
    }

    /**
     * Returns the square root of the complex number
     */
    public Compd sqrt () {
        if (im == 0) {
            return sqrt(re);
        }

        double modulus = abs();
        return new Compd(Math.sqrt(modulus + re) / Mathd.SQRT2, Math.signum(im) * Math.sqrt(modulus - re) / Mathd.SQRT2);
    }

    /**
     * Returns the complex number's exponent
     */
    public Compd exp () {
        if (im == 0) {
            return new Compd(Math.exp(re), 0);
        }

        double ex = Math.exp(re);
        return Compd.expi(im).mul(ex);
    }

    /**
     * Returns the complex number's natural logarithm
     */
    public Compd log () {
        if (im == 0 && re > 0) {
            return new Compd(Math.log(re), 0);
        }

        return new Compd(Math.log(abs()), polarAngle());
    }

    /**
     * Returns the complex number's conjugate
     */
    public Compd conj () {
        return new Compd(re, -im);
    }

    /**
     * Returns the complex number powered to another complex number
     */
    public Compd pow (Compd b) {
        if (b.isReal() && b.re == 2) {
            return new Compd(re * re - im * im, 2 * re * im);
        }

        return log().mul(b).exp();
    }

    /**
     * Pow compd.
     *
     * @param b the b
     * @return the compd
     */
    public Compd pow (double b) {
        if (im == 0) {
            return new Compd(Math.pow(re, b), 0);
        } else if (b == 2) {
            return new Compd(re * re - im * im, 2 * re * im);
        }

        return log().mul(b).exp();
    }

    /**
     * To float comp.
     *
     * @return the comp
     */
    // JAVA FUNCTIONS
    public Comp toFloat () {
        return new Comp((float) re, (float) im);
    }

    public Compb toDecimal(MathContext ctx) {
        return new Compb(BigDecimal.valueOf(re).round(ctx), BigDecimal.valueOf(im).round(ctx), ctx);
    }

    /**
     * To cuda cu double complex.
     *
     * @return the cu double complex
     */
    public cuDoubleComplex toCUDA () {
        return cuDoubleComplex.cuCmplx(re, im);
    }

    @Override
    public Compd clone() {
        return new Compd(re, im);
    }

    @Override
    public String toString() {
        if (Double.isNaN(re) || Double.isNaN(im)) {
            return "NaN";
        } else if (Double.isInfinite(re) || Double.isInfinite(im)) {
            return "Infinity";
        } else if (re == 0) {
            return im +"i";
        } else if (im == 0) {
            return Double.toString(re);
        } else if (im >= 0) {
            return re + " + " + im + "i";
        }

        return re + " - " + -im + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compd compd = (Compd) o;
        return Double.compare(compd.re, re) == 0 && Double.compare(compd.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    /**
     * Polar rad string string.
     *
     * @return the string
     */
    public String polarRadString() {
        double radius = abs();
        double angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    /**
     * Polar string string.
     *
     * @return the string
     */
    public String polarString () {
        double radius = abs();
        double angle = polarAngle();

        return radius+" + "+ Math.toDegrees(angle)+" deg";
    }

    /**
     * From polar compd.
     *
     * @param radius the radius
     * @param angle  the angle
     * @return the compd
     */
    // STATIC FUNCTIONS
    public static Compd fromPolar (double radius, double angle) {
        double tan = Math.tan(angle);
        double real = Math.sqrt(radius * radius / (1 + tan * tan));
        double i = tan * real;

        return new Compd(real, i);
    }

    /**
     * Sqrt compd.
     *
     * @param x the x
     * @return the compd
     */
    public static Compd sqrt (Compd x) {
        return x.sqrt();
    }

    /**
     * Sqrt compd.
     *
     * @param i the
     * @return the compd
     */
    public static Compd sqrt (double i) {
        if (i >= 0) {
            return new Compd(Math.sqrt(i), 0);
        }

        return new Compd(0, Math.sqrt(-i));
    }

    public Compd cbrt () {
        final double pi4 = 2 * Mathd.PI2;

        if (im == 0) {
            return new Compd(Math.cbrt(re), 0);
        }

        double angle = polarAngle();
        double radius = abs();

        double alpha = Math.cbrt(radius);
        double beta = (angle + pi4) / 3;

        return Compd.expi(beta).mul(alpha);
    }

    /**
     * Exp compd.
     *
     * @param x the x
     * @return the compd
     */
    public static Compd exp (Compd x) {
        return x.exp();
    }

    /**
     * Exp compd.
     *
     * @param x the x
     * @return the compd
     */
    public static Compd exp (double x) {
        return new Compd(Math.exp(x), 0);
    }

    public static Compd expi (double x) {
        return new Compd(Math.cos(x), Math.sin(x));
    }

    /**
     * Log compd.
     *
     * @param x the x
     * @return the compd
     */
    public static Compd log (Compd x) {
        return x.log();
    }

    /**
     * Log compd.
     *
     * @param x the x
     * @return the compd
     */
    public static Compd log (double x) {
        return x > 0 ? new Compd(Math.log(x), 0) : new Compd(x, 0).log();
    }
}
