package org.jml.Complex.Single;
import org.jml.Complex.Double.Compd;
import org.jml.Mathx.Mathf;
import jcuda.cuComplex;

import java.io.Serializable;
import java.util.Objects;

public class Comp implements Serializable {
    final private static long serialVersionUID = -4811064611324460886L;

    final public static Comp ZERO = new Comp();
    final public static Comp ONE = new Comp(1,0);
    final public static Comp ONEI = new Comp(0,1);
    final public static Comp MONE = new Comp(-1,0);

    final public static Comp PI = new Comp(Mathf.PI, 0);
    final public static Comp E = new Comp(Mathf.E, 0);

    final public float re, im;

    public Comp(float real, float imaginary) {
        this.re = real;
        this.im = imaginary;
    }

    public Comp(cuComplex cuda) {
        this.re = cuda.x;
        this.im = cuda.y;
    }

    public Comp() {
        this(0, 0);
    }

    // OPERATIONS
    public Comp add (Comp b) {
        return new Comp(re + b.re, im + b.im);
    }

    public Comp add (float b) {
        return new Comp(re + b, im);
    }

    public Comp subtr (Comp b) {
        return new Comp(re - b.re, im - b.im);
    }

    public Comp subtr (float b) {
        return new Comp(re - b, im);
    }

    public Comp invSubtr (float b) {
        return new Comp(b - re, im);
    }

    public Comp mul (Comp b) {
        return new Comp(re * b.re - im * b.im, re * b.im + b.re * im);
    }

    public Comp mul (float b) {
        return new Comp(re * b, im * b);
    }

    public Comp div (Comp b) {
        float div = b.re * b.re + b.im * b.im;
        return new Comp((re * b.re + im * b.im) / div, (im * b.re - re * b.im) / div);
    }

    public Comp div (float b) {
        return new Comp(re / b, im / b);
    }

    public Comp invDiv (float b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public boolean isReal () {
        return im == 0;
    }

    public boolean isNan () {
        return Float.isNaN(re) || Float.isNaN(im);
    }

    public boolean isInfinite () {
        return Float.isInfinite(re) || Float.isInfinite(im);
    }

    public float abs() {
        return Mathf.hypot(re, im);
    }

    public float polarAngle () {
        return Mathf.atan2(im, re);
    }

    // FUNCTIONS
    public Comp inverse () {
        float xy2 = re * re + im * im;
        return new Comp(re / xy2, -im / xy2);
    }

    public Comp sqrt () {
        if (im == 0) {
            return sqrt(re);
        }

        float modulus = abs();
        return new Comp(Mathf.sqrt(modulus + re) / Mathf.SQRT2, Mathf.signum(im) * Mathf.sqrt(modulus - re) / Mathf.SQRT2);
    }

    public Comp cbrt () {
        final float pi4 = 2 * Mathf.PI2;

        if (im == 0) {
            return new Comp(Mathf.cbrt(re), 0);
        }

        float angle = polarAngle();
        float radius = abs();

        float alpha = Mathf.cbrt(radius);
        float beta = (angle + pi4) / 3;

        return Comp.expi(beta).mul(alpha);
    }

    public Comp exp () {
        if (im == 0) {
            return new Comp(Mathf.exp(re), 0);
        }

        float ex = Mathf.exp(re);
        return Comp.expi(im).mul(ex);
    }

    public Comp log () {
        if (im == 0 && re > 0) {
            return new Comp(Mathf.log(re), 0);
        }

        return new Comp(Mathf.log(abs()), polarAngle());
    }

    public Comp conj () {
        return new Comp(re, -im);
    }

    public Comp pow (Comp b) {
        if (b.isReal() && b.re == 2) {
            return new Comp(re * re - im * im, 2 * re * im);
        }

        return log().mul(b).exp();
    }

    public Comp pow (float b) {
        if (im == 0) {
            return new Comp(Mathf.pow(re, b), 0);
        } else if (b == 2) {
            return new Comp(re * re - im * im, 2 * re * im);
        }

        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Compd toDouble () {
        return new Compd(re, im);
    }

    public Comp toFloat() {
        return this;
    }

    public cuComplex toCUDA () {
        return cuComplex.cuCmplx(re, im);
    }

    @Override
    public Comp clone() {
        return new Comp(re, im);
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
        Comp comp = (Comp) o;
        return Float.compare(comp.re, re) == 0 && Float.compare(comp.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    public String polarRadString() {
        float radius = abs();
        float angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    public String polarDegString() {
        float radius = abs();
        float angle = polarAngle();

        return radius+" + "+Mathf.toDegrees(angle)+" deg";
    }

    // STATIC FUNCTIONS
    public static Comp fromPolar (float radius, float angle) {
        float tan = Mathf.tan(angle);
        float real = Mathf.sqrt(radius * radius / (1 + tan * tan));
        float i = tan * real;

        return new Comp(real, i);
    }

    public static Comp sqrt (Comp x) {
        return x.sqrt();
    }

    public static Comp sqrt (float i) {
        if (i >= 0) {
            return new Comp(Mathf.sqrt(i), 0);
        }

        return new Comp(0, Mathf.sqrt(-i));
    }

    public static Comp exp (Comp x) {
        return x.exp();
    }

    public static Comp exp (float x) {
        return new Comp(Mathf.exp(x), 0);
    }

    public static Comp expi (float x) {
        return new Comp(Mathf.cos(x), Mathf.sin(x));
    }

    public static Comp log (Comp x) {
        return x.log();
    }

    public static Comp log (float x) {
        return x > 0 ? new Comp(Mathf.log(x), 0) : new Comp(x, 0).log();
    }
}
