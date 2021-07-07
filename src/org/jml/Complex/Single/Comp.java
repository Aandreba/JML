package org.jml.Complex.Single;
import org.jml.Complex.Complex;
import org.jml.Complex.Double.Compd;
import org.jml.Mathx.Mathf;
import jcuda.cuComplex;

import java.util.Objects;

public class Comp implements Complex {
    final public static Comp ZERO = new Comp();
    final public static Comp ONE = new Comp(1,0);
    final public static Comp ONEI = new Comp(0,1);
    final public static Comp MONE = new Comp(-1,0);

    final public float real, imaginary;

    public Comp(float real, float imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Comp(cuComplex cuda) {
        this.real = cuda.x;
        this.imaginary = cuda.y;
    }

    public Comp() {
        this(0, 0);
    }

    // OPERATIONS
    public Comp add (Comp b) {
        return new Comp(real + b.real, imaginary + b.imaginary);
    }

    public Comp add (float b) {
        return new Comp(real + b, imaginary);
    }

    public Comp subtr (Comp b) {
        return new Comp(real - b.real, imaginary - b.imaginary);
    }

    public Comp subtr (float b) {
        return new Comp(real - b, imaginary);
    }

    public Comp invSubtr (float b) {
        return new Comp(b - real, imaginary);
    }

    public Comp mul (Comp b) {
        return new Comp(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Comp mul (float b) {
        return new Comp(real * b, imaginary * b);
    }

    public Comp div (Comp b) {
        float div = b.real * b.real + b.imaginary * b.imaginary;
        return new Comp((real * b.real + imaginary * b.imaginary) / div, (imaginary * b.real - real * b.imaginary) / div);
    }

    public Comp div (float b) {
        return new Comp(real / b, imaginary / b);
    }

    public Comp invDiv (float b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public boolean isReal () {
        return imaginary == 0;
    }

    public boolean isNan () {
        return Float.isNaN(real) || Float.isNaN(imaginary);
    }

    public boolean isInfinite () {
        return Float.isInfinite(real) || Float.isInfinite(imaginary);
    }

    public float abs() {
        return Mathf.hypot(real, imaginary);
    }

    public float polarAngle () {
        return Mathf.atan2(imaginary, real);
    }

    public float polarRadius () {
        return abs();
    }

    // FUNCTIONS
    public Comp inverse () {
        float xy2 = real * real + imaginary * imaginary;
        return new Comp(real / xy2, -imaginary / xy2);
    }

    public Comp sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        float modulus = abs();
        return new Comp(Mathf.sqrt(modulus + real) / Mathf.SQRT2, Mathf.signum(imaginary) * Mathf.sqrt(modulus - real) / Mathf.SQRT2);
    }

    public Comp cbrt () {
        final float pi4 = 2 * Mathf.PI2;

        if (imaginary == 0) {
            return new Comp(Mathf.cbrt(real), 0);
        }

        float angle = polarAngle();
        float radius = polarRadius();

        float alpha = Mathf.cbrt(radius);
        float beta = (angle + pi4) / 3;

        return new Comp(alpha * Mathf.cos(beta), alpha * Mathf.sin(beta));
    }

    public Comp exp () {
        if (imaginary == 0) {
            return new Comp(Mathf.exp(real), 0);
        }

        float ex = Mathf.exp(real);
        return new Comp(ex * Mathf.cos(imaginary), ex * Mathf.sin(imaginary));
    }

    public Comp log () {
        if (imaginary == 0) {
            return new Comp(Mathf.log(real), 0);
        }

        return new Comp(Mathf.log(polarRadius()), polarAngle());
    }

    public Comp conj () {
        return new Comp(real, -imaginary);
    }

    public Comp pow (Comp b) {
        if (b.isReal() && b.real == 2) {
            return new Comp(real * real - imaginary * imaginary, 2 * real * imaginary);
        }

        return log().mul(b).exp();
    }

    public Comp pow (float b) {
        if (imaginary == 0) {
            return new Comp(Mathf.pow(real, b), 0);
        } else if (b == 2) {
            return new Comp(real * real - imaginary * imaginary, 2 * real * imaginary);
        }

        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Compd toDouble () {
        return new Compd(real, imaginary);
    }

    @Override
    public Comp toFloat() {
        return this;
    }

    public cuComplex toCUDA () {
        return cuComplex.cuCmplx(real, imaginary);
    }

    @Override
    public Comp clone() {
        return new Comp(real, imaginary);
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
        Comp comp = (Comp) o;
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
        return new Comp(Mathf.log(x), 0);
    }
}
