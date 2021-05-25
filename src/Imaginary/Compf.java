package Imaginary;
import Mathx.Mathf;

public class Compf {
    final public float real, imaginary;

    public Compf(float real, float imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Compf() {
        this(0, 0);
    }

    // OPERATIONS
    public Compf add (Compf b) {
        return new Compf(real + b.real, imaginary + b.imaginary);
    }

    public Compf add (float b) {
        return new Compf(real + b, imaginary);
    }

    public Compf subtr (Compf b) {
        return new Compf(real - b.real, imaginary - b.imaginary);
    }

    public Compf subtr (float b) {
        return new Compf(real - b, imaginary);
    }

    public Compf invSubtr (float b) {
        return new Compf(b - real, imaginary);
    }

    public Compf mul (Compf b) {
        return new Compf(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Compf mul (float b) {
        return new Compf(real * b, imaginary * b);
    }

    public Compf div (Compf b) {
        return mul(b.inverse());
    }

    public Compf div (float b) {
        return mul(1 / b);
    }

    public Compf invDiv (float b) {
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
    public Compf inverse () {
        float xy2 = real * real + imaginary * imaginary;
        return new Compf(real / xy2, -imaginary / xy2);
    }

    public Compf sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        float modulus = modulus();
        return new Compf(Mathf.sqrt(modulus + real) / Mathf.SQRT2, Mathf.signum(imaginary) * Mathf.sqrt(modulus - real) / Mathf.SQRT2);
    }

    public Compf exp () {
        if (imaginary == 0) {
            return new Compf(Mathf.exp(real), 0);
        }

        float ex = Mathf.exp(real);
        return new Compf(ex * Mathf.cos(imaginary), ex * Mathf.sin(imaginary));
    }

    public Compf log () {
        if (imaginary == 0) {
            return new Compf(Mathf.log(real), 0);
        }

        return new Compf(Mathf.log(polarRadius()), polarAngle());
    }

    public Compf pow (Compf b) {
        return log().mul(b).exp();
    }

    public Compf pow (float b) {
        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Comp toDouble () {
        return new Comp(real, imaginary);
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
    public static Compf fromPolar (float radius, float angle) {
        float tan = Mathf.tan(angle);
        float real = Mathf.sqrt(radius * radius / (1 + tan * tan));
        float i = tan * real;

        return new Compf(real, i);
    }

    public static Compf sqrt (Compf x) {
        return x.sqrt();
    }

    public static Compf sqrt (float i) {
        if (i >= 0) {
            return new Compf(Mathf.sqrt(i), 0);
        }

        return new Compf(0, Mathf.sqrt(-i));
    }

    public static Compf exp (Compf x) {
        return x.exp();
    }

    public static Compf exp (float x) {
        return new Compf(Mathf.exp(x), 0);
    }

    public static Compf log (Compf x) {
        return x.log();
    }

    public static Compf log (float x) {
        return new Compf(Mathf.log(x), 0);
    }
}
