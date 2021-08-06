import org.jml.Calculus.Integral;
import org.jml.Function.Real.DecimalReal;
import org.jml.Function.Real.DoubleReal;
import org.jml.Function.Real.FloatReal;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.MathContext;

public class Main {
    public static void main (String... args) {
        DoubleReal func = x -> 2 * Math.exp(-Math.pow(x - 0.5, 2) / 0.08);
        MathContext context = MathContext.DECIMAL32;

        System.out.println(func.integ(0, 0.5f));
    }
}