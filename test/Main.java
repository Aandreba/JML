import org.jml.Calculus.Derivative;
import org.jml.Calculus.Integral;
import org.jml.Function.Real.DoubleReal;
import org.jml.Function.Real.RealFunction;

public class Main {
    public static void main(String... args) {
        DoubleReal func = (double x) -> x + Math.sin(x);

        System.out.println(func.apply(1));
        System.out.println(func.deriv(1));
        System.out.println(Integral.integ(0, 1, func));
    }
}