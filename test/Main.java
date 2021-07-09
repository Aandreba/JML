import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Exponential.Exp;
import org.jml.Calculus.Derivative.Function.Linear.Linear;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Rand;

public class Main {
    public static void main(String... args) {
        Func func = Linear.CBRT.applyTo(Linear.SQRT);
        Func one = func.deriv();
        Func two = one.deriv();
        Func alpha = func.deriv(2);

        float x = Rand.getFloat();
        System.out.println(x);
        System.out.println(func.applyTo(x));
        System.out.println(one.applyTo(x));
    }
}