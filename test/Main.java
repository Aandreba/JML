import org.jml.Calculus.Func;
import org.jml.Calculus.Function.Regular.Pow;
import org.jml.Calculus.Function.Trigo.Sin;
import org.jml.Mathx.Rand;

public class Main {
    public static void main (String... args) {
        Func func = new Pow(4).add(new Pow(2).apply(Sin.SIN)).;

        double rand = Rand.getDouble();
        System.out.println();
        System.out.println(rand);
        System.out.println(func.apply(rand));
        System.out.println(func.deriv(rand));
    }
}
