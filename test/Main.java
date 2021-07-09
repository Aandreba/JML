import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Exponential.Exp;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Rand;

public class Main {
    public static void main(String... args) {
        Func one = Exp.EXP.applyTo(Exp.LN);
        Func deriv = one.deriv();

        float x = Rand.getFloat();
        System.out.println(x);
        System.out.println(one.applyTo(x));
        System.out.println(deriv.applyTo(x));
    }
}