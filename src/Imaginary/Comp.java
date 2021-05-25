package Imaginary;
import Mathx.Mathd;

public class Comp {
    final public double real, imaginary;

    public Comp(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Comp() {
        this(0, 0);
    }

    // OPERATIONS
    public Comp add (Comp b) {
        return new Comp(real + b.real, imaginary + b.imaginary);
    }

    public Comp add (double b) {
        return new Comp(real + b, imaginary);
    }

    public Comp subtr (Comp b) {
        return new Comp(real - b.real, imaginary - b.imaginary);
    }

    public Comp subtr (double b) {
        return new Comp(real - b, imaginary);
    }

    public Comp invSubtr (double b) {
        return new Comp(b - real, imaginary);
    }

    public Comp mul (Comp b) {
        return new Comp(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Comp mul (double b) {
        return new Comp(real * b, imaginary * b);
    }

    public Comp div (Comp b) {
        return mul(b.inverse());
    }

    public Comp div (double b) {
        return mul(1 / b);
    }

    public Comp invDiv (double b) {
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
    public Comp inverse () {
        double xy2 = real * real + imaginary * imaginary;
        return new Comp(real / xy2, -imaginary / xy2);
    }

    public Comp sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        double modulus = modulus();
        return new Comp(Math.sqrt(modulus + real) / Mathd.SQRT2, Math.signum(imaginary) * Math.sqrt(modulus - real) / Mathd.SQRT2);
    }

    public Comp exp () {
        if (imaginary == 0) {
            return new Comp(Math.exp(real), 0);
        }

        double ex = Math.exp(real);
        return new Comp(ex * Math.cos(imaginary), ex * Math.sin(imaginary));
    }

    public Comp log () {
        if (imaginary == 0) {
            return new Comp(Math.log(real), 0);
        }

        return new Comp(Math.log(polarRadius()), polarAngle());
    }

    public Comp pow (Comp b) {
        return log().mul(b).exp();
    }

    public Comp pow (double b) {
        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Compf toFloat () {
        return new Compf((float) real, (float) imaginary);
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
    public static Comp fromPolar (double radius, double angle) {
        double tan = Math.tan(angle);
        double real = Math.sqrt(radius * radius / (1 + tan * tan));
        double i = tan * real;

        return new Comp(real, i);
    }

    public static Comp sqrt (Comp x) {
        return x.sqrt();
    }

    public static Comp sqrt (double i) {
        if (i >= 0) {
            return new Comp(Math.sqrt(i), 0);
        }

        return new Comp(0, Math.sqrt(-i));
    }

    public static Comp exp (Comp x) {
        return x.exp();
    }

    public static Comp exp (double x) {
        return new Comp(Math.exp(x), 0);
    }

    public static Comp log (Comp x) {
        return x.log();
    }

    public static Comp log (double x) {
        return new Comp(Math.log(x), 0);
    }
}
