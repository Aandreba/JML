import Imaginary.Complex;
import Imaginary.Complexf;
import Mathx.Mathf;

public class Main {
    public static void main (String... args) {
        Complexf f = Complexf.fromPolar(5, Mathf.toRadians(45));
        Complex d = Complex.fromPolar(2, Math.toRadians(90));

        System.out.println(f+", "+d);
        System.out.println(d.pow(f.toDouble()));
    }
}
