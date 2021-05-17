package Imaginary;
import Mathx.Mathd;
import Mathx.Mathf;
import Matrix.Mat;

public class Complex {
    final public double real, imaginary;

    public Complex (double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex () {
        this(0, 0);
    }

    // OPERATIONS
    public Complex add (Complex b) {
        return new Complex(real + b.real, imaginary + b.imaginary);
    }

    public Complex add (double b) {
        return new Complex(real + b, imaginary);
    }

    public Complex subtr (Complex b) {
        return new Complex(real - b.real, imaginary - b.imaginary);
    }

    public Complex subtr (double b) {
        return new Complex(real - b, imaginary);
    }

    public Complex invSubtr (double b) {
        return new Complex(b - real, imaginary);
    }

    public Complex mul (Complex b) {
        return new Complex(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Complex mul (double b) {
        return new Complex(real * b, imaginary * b);
    }

    public Complex div (Complex b) {
        return mul(b.inverse());
    }

    public Complex div (double b) {
        return mul(1 / b);
    }

    public Complex invDiv (double b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public double modulus () {
        return Math.hypot(real, imaginary);
    }

    public double polarAngle () {
        return Math.atan2(imaginary, real);
    }

    public double polarRadius () {
        return modulus();
    }

    // FUNCTIONS
    public Complex inverse () {
        double xy2 = real * real + imaginary * imaginary;
        return new Complex(real / xy2, -imaginary / xy2);
    }

    public Complex sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        double modulus = modulus();
        return new Complex(Math.sqrt(modulus + real) / Mathd.SQRT2, Math.signum(imaginary) * Math.sqrt(modulus - real) / Mathd.SQRT2);
    }

    public Complex exp () {
        if (imaginary == 0) {
            return new Complex(Math.exp(real), 0);
        }

        double ex = Math.exp(real);
        return new Complex(ex * Math.cos(imaginary), ex * Math.sin(imaginary));
    }

    public Complex log () {
        if (imaginary == 0) {
            return new Complex(Math.log(real), 0);
        }

        return new Complex(Math.log(polarRadius()), polarAngle());
    }

    public Complex pow (Complex b) {
        return log().mul(b).exp();
    }

    public Complex pow (double b) {
        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Complexf toFloat () {
        return new Complexf((float) real, (float) imaginary);
    }

    @Override
    public String toString() {
        return real+" + "+imaginary+"i";
    }

    public String polarRadString() {
        double radius = polarRadius();
        double angle = polarAngle();

        return radius+" + "+angle+" rad";
    }

    public String polarString () {
        double radius = polarRadius();
        double angle = polarAngle();

        return radius+" + "+ Math.toDegrees(angle)+" deg";
    }

    // STATIC FUNCTIONS
    public static Complex fromPolar (double radius, double angle) {
        double tan = Math.tan(angle);
        double real = Math.sqrt(radius * radius / (1 + tan * tan));
        double i = tan * real;

        return new Complex(real, i);
    }

    public static Complex sqrt (Complex x) {
        return x.sqrt();
    }

    public static Complex sqrt (double i) {
        if (i >= 0) {
            return new Complex(Math.sqrt(i), 0);
        }

        return new Complex(0, Math.sqrt(-i));
    }

    public static Complex exp (Complex x) {
        return x.exp();
    }

    public static Complex exp (double x) {
        return new Complex(Math.exp(x), 0);
    }

    public static Complex log (Complex x) {
        return x.log();
    }

    public static Complex log (double x) {
        return new Complex(Math.log(x), 0);
    }
}
