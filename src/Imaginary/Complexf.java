package Imaginary;
import Mathx.Mathd;
import Mathx.Mathf;

public class Complexf {
    final public float real, imaginary;

    public Complexf (float real, float imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complexf() {
        this(0, 0);
    }

    // OPERATIONS
    public Complexf add (Complexf b) {
        return new Complexf(real + b.real, imaginary + b.imaginary);
    }

    public Complexf add (float b) {
        return new Complexf(real + b, imaginary);
    }

    public Complexf subtr (Complexf b) {
        return new Complexf(real - b.real, imaginary - b.imaginary);
    }

    public Complexf subtr (float b) {
        return new Complexf(real - b, imaginary);
    }

    public Complexf invSubtr (float b) {
        return new Complexf(b - real, imaginary);
    }

    public Complexf mul (Complexf b) {
        return new Complexf(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Complexf mul (float b) {
        return new Complexf(real * b, imaginary * b);
    }

    public Complexf div (Complexf b) {
        return mul(b.inverse());
    }

    public Complexf div (float b) {
        return mul(1 / b);
    }

    public Complexf invDiv (float b) {
        return inverse().mul(b);
    }

    // PROPERTIES
    public float modulus () {
        return Mathf.hypot(real, imaginary);
    }

    public float polarAngle () {
        return Mathf.atan2(imaginary, real);
    }

    public float polarRadius () {
        return modulus();
    }

    // FUNCTIONS
    public Complexf inverse () {
        float xy2 = real * real + imaginary * imaginary;
        return new Complexf(real / xy2, -imaginary / xy2);
    }

    public Complexf sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        float modulus = modulus();
        return new Complexf(Mathf.sqrt(modulus + real) / Mathf.SQRT2, Mathf.signum(imaginary) * Mathf.sqrt(modulus - real) / Mathf.SQRT2);
    }

    public Complexf exp () {
        if (imaginary == 0) {
            return new Complexf(Mathf.exp(real), 0);
        }

        float ex = Mathf.exp(real);
        return new Complexf(ex * Mathf.cos(imaginary), ex * Mathf.sin(imaginary));
    }

    public Complexf log () {
        if (imaginary == 0) {
            return new Complexf(Mathf.log(real), 0);
        }

        return new Complexf(Mathf.log(polarRadius()), polarAngle());
    }

    public Complexf pow (Complexf b) {
        return log().mul(b).exp();
    }

    public Complexf pow (float b) {
        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Complex toDouble () {
        return new Complex(real, imaginary);
    }

    @Override
    public String toString() {
        return real+" + "+imaginary+"i";
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
    public static Complexf fromPolar (float radius, float angle) {
        float tan = Mathf.tan(angle);
        float real = Mathf.sqrt(radius * radius / (1 + tan * tan));
        float i = tan * real;

        return new Complexf(real, i);
    }

    public static Complexf sqrt (Complexf x) {
        return x.sqrt();
    }

    public static Complexf sqrt (float i) {
        if (i >= 0) {
            return new Complexf(Mathf.sqrt(i), 0);
        }

        return new Complexf(0, Mathf.sqrt(-i));
    }

    public static Complexf exp (Complexf x) {
        return x.exp();
    }

    public static Complexf exp (float x) {
        return new Complexf(Mathf.exp(x), 0);
    }

    public static Complexf log (Complexf x) {
        return x.log();
    }

    public static Complexf log (float x) {
        return new Complexf(Mathf.log(x), 0);
    }
}
