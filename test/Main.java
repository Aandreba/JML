import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Exponential.Exp;
import org.jml.Calculus.Derivative.Function.Linear.Linear;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Calculus.Derivative.Function.Regular.Var;
import org.jml.Mathx.Rand;

public class Main {
    public static void main(String... args) {
        Const mass = new Const(0.5);
        Var speed = new Var("v");

        Func func = Const.HALF.mul(mass).mul(Pow.TWO.applyTo(speed));
        Func deriv = func.deriv(speed);

        System.out.println(func); // TODO
        System.out.println(deriv);
    }
}