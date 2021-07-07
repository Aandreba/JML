package org.jml.Complex.Double;
import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathd;
import jcuda.cuDoubleComplex;

import java.util.Objects;

/**
 * The type Compd is the representation of a complex number in doubles
 */
public class Compd {
    final public static Compd ZERO = new Compd();
    final public static Compd ONE = new Compd(1,0);
    final public static Compd ONEI = new Compd(0,1);
    final public static Compd MONE = new Compd(-1,0);

    /**
     * The real part
     */
    final public double real, /**
     * The imaginary part
     */
    imaginary;

    public Compd(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Compd(cuDoubleComplex cuda) {
        this.real = cuda.x;
        this.imaginary = cuda.y;
    }

    public Compd() {
        this(0, 0);
    }

    /**
     * Add 2 complex numbers
     */
    // OPERATIONS
    public Compd add (Compd b) {
        return new Compd(real + b.real, imaginary + b.imaginary);
    }

    /**
     * Add a real number to a complex number
     */
    public Compd add (double b) {
        return new Compd(real + b, imaginary);
    }

    /**
     * Subtract 2 complex numbers
     */
    public Compd subtr (Compd b) {
        return new Compd(real - b.real, imaginary - b.imaginary);
    }

    /**
     * Subtract a real number from a complex number
     */
    public Compd subtr (double b) {
        return new Compd(real - b, imaginary);
    }

    /**
     * Subtract a complex number from a real number
     */
    public Compd invSubtr (double b) {
        return new Compd(b - real, imaginary);
    }

    /**
     * Multiply 2 complex numbers
     */
    public Compd mul (Compd b) {
        return new Compd(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    /**
     * Multiply a real number and a complex number
     */
    public Compd mul (double b) {
        return new Compd(real * b, imaginary * b);
    }

    /**
     * Divide 2 complex numbers
     */
    public Compd div (Compd b) {
        double div = b.real * b.real + b.imaginary * b.imaginary;
        return new Compd((real * b.real + imaginary * b.imaginary) / div, (imaginary * b.real - real * b.imaginary) / div);
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
        return imaginary == 0;
    }

    /**
     * Returns {@code true} if the specified number is a
     * Not-a-Number (NaN) value, {@code false} otherwise.
     */
    public boolean isNan () {
        return Double.isNaN(real) || Double.isNaN(imaginary);
    }

    /**
     * Returns {@code true} if the specified number is infinite, {@code false} otherwise.
     */
    public boolean isInfinite () {
        return Double.isInfinite(real) || Double.isInfinite(imaginary);
    }

    /**
     * Returns the complex number's modulus
     */
    public double modulus () {
        return Math.hypot(real, imaginary);
    }

    /**
     * Returns the complex number's angle in polar coordinates
     */
    public double polarAngle () {
        return Math.atan2(imaginary, real);
    }

    /**
     * Returns the complex number's radius in polar coordinates
     */
    public double polarRadius () {
        return modulus();
    }

    /**
     * Returns the inverse of the complex number (1 / z)
     */
    // FUNCTIONS
    public Compd inverse () {
        double xy2 = real * real + imaginary * imaginary;
        return new Compd(real / xy2, -imaginary / xy2);
    }

    /**
     * Returns the square root of the complex number
     */
    public Compd sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        double modulus = modulus();
        return new Compd(Math.sqrt(modulus + real) / Mathd.SQRT2, Math.signum(imaginary) * Math.sqrt(modulus - real) / Mathd.SQRT2);
    }

    /**
     * Returns the complex number's exponent
     */
    public Compd exp () {
        if (imaginary == 0) {
            return new Compd(Math.exp(real), 0);
        }

        double ex = Math.exp(real);
        return new Compd(ex * Math.cos(imaginary), ex * Math.sin(imaginary));
    }

    /**
     * Returns the complex number's natural logarithm
     */
    public Compd log () {
        if (imaginary == 0) {
            return new Compd(Math.log(real), 0);
        }

        return new Compd(Math.log(polarRadius()), polarAngle());
    }

    /**
     * Returns the complex number's conjugate
     */
    public Compd conj () {
        return new Compd(real, -imaginary);
    }

    /**
     * Returns the complex number powered to another complex number
     */
    public Compd pow (Compd b) {
        if (b.isReal() && b.real == 2) {
            return new Compd(real * real - imaginary * imaginary, 2 * real * imaginary);
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
        if (imaginary == 0) {
            return new Compd(Math.pow(real, b), 0);
        } else if (b == 2) {
            return new Compd(real * real - imaginary * imaginary, 2 * real * imaginary);
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
        return new Comp((float) real, (float) imaginary);
    }

    public Compd toDouble() {
        return this;
    }

    /**
     * To cuda cu double complex.
     *
     * @return the cu double complex
     */
    public cuDoubleComplex toCUDA () {
        return cuDoubleComplex.cuCmplx(real, imaginary);
    }

    @Override
    public Compd clone() {
        return new Compd(real, imaginary);
    }

    @Override
    public String toString() {
        if (Double.isNaN(real) || Double.isNaN(imaginary)) {
            return "NaN";
        } else if (Double.isInfinite(real) || Double.isInfinite(imaginary)) {
            return "Infinity";
        } else if (real == 0) {
            return imaginary+"i";
        } else if (imaginary == 0) {
            return Double.toString(real);
        } else if (imaginary >= 0) {
            return real + " + " + imaginary + "i";
        }

        return real + " - " + -imaginary + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compd compd = (Compd) o;
        return Double.compare(compd.real, real) == 0 && Double.compare(compd.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }

    /**
     * Polar rad string string.
     *
     * @return the string
     */
    public String polarRadString() {
        double radius = polarRadius();
        double angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    /**
     * Polar string string.
     *
     * @return the string
     */
    public String polarString () {
        double radius = polarRadius();
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
        return new Compd(Math.log(x), 0);
    }
}
