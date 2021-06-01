package Complex;
import Mathx.Mathd;
import jcuda.cuDoubleComplex;

public class Compd {
    final public static Compd ZERO = new Compd();
    final public static Compd ONE = new Compd(1,0);
    final public static Compd MONE = new Compd(-1,0);

    final public double real, imaginary;

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

    // OPERATIONS
    public Compd add (Compd b) {
        return new Compd(real + b.real, imaginary + b.imaginary);
    }

    public Compd add (double b) {
        return new Compd(real + b, imaginary);
    }

    public Compd subtr (Compd b) {
        return new Compd(real - b.real, imaginary - b.imaginary);
    }

    public Compd subtr (double b) {
        return new Compd(real - b, imaginary);
    }

    public Compd invSubtr (double b) {
        return new Compd(b - real, imaginary);
    }

    public Compd mul (Compd b) {
        return new Compd(real * b.real - imaginary * b.imaginary, real * b.imaginary + b.real * imaginary);
    }

    public Compd mul (double b) {
        return new Compd(real * b, imaginary * b);
    }

    public Compd div (Compd b) {
        return mul(b.inverse());
    }

    public Compd div (double b) {
        return mul(1 / b);
    }

    public Compd invDiv (double b) {
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
    public Compd inverse () {
        double xy2 = real * real + imaginary * imaginary;
        return new Compd(real / xy2, -imaginary / xy2);
    }

    public Compd sqrt () {
        if (imaginary == 0) {
            return sqrt(real);
        }

        double modulus = modulus();
        return new Compd(Math.sqrt(modulus + real) / Mathd.SQRT2, Math.signum(imaginary) * Math.sqrt(modulus - real) / Mathd.SQRT2);
    }

    public Compd exp () {
        if (imaginary == 0) {
            return new Compd(Math.exp(real), 0);
        }

        double ex = Math.exp(real);
        return new Compd(ex * Math.cos(imaginary), ex * Math.sin(imaginary));
    }

    public Compd log () {
        if (imaginary == 0) {
            return new Compd(Math.log(real), 0);
        }

        return new Compd(Math.log(polarRadius()), polarAngle());
    }

    public Compd pow (Compd b) {
        return log().mul(b).exp();
    }

    public Compd pow (double b) {
        return log().mul(b).exp();
    }

    // JAVA FUNCTIONS
    public Comp toFloat () {
        return new Comp((float) real, (float) imaginary);
    }

    public cuDoubleComplex toCUDA () {
        return cuDoubleComplex.cuCmplx(real, imaginary);
    }

    @Override
    public String toString() {
        if (real == 0) {
            return imaginary+"i";
        } else if (imaginary == 0) {
            return Double.toString(real);
        } else if (imaginary >= 0) {
            return real + " + " + imaginary + "i";
        }

        return real + " - " + -imaginary + "i";
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
    public static Compd fromPolar (double radius, double angle) {
        double tan = Math.tan(angle);
        double real = Math.sqrt(radius * radius / (1 + tan * tan));
        double i = tan * real;

        return new Compd(real, i);
    }

    public static Compd sqrt (Compd x) {
        return x.sqrt();
    }

    public static Compd sqrt (double i) {
        if (i >= 0) {
            return new Compd(Math.sqrt(i), 0);
        }

        return new Compd(0, Math.sqrt(-i));
    }

    public static Compd exp (Compd x) {
        return x.exp();
    }

    public static Compd exp (double x) {
        return new Compd(Math.exp(x), 0);
    }

    public static Compd log (Compd x) {
        return x.log();
    }

    public static Compd log (double x) {
        return new Compd(Math.log(x), 0);
    }
}
