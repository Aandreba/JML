import org.jml.Calculus.Integral;
import org.jml.Function.Real.DecimalReal;
import org.jml.Function.Real.DoubleReal;
import org.jml.Function.Real.FloatReal;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.MathContext;

public class Main {
    public static void main (String... args) {
        DecimalReal func = (x,c) -> Mathb.ONE.divide(x.pow(2), c);
        MathContext context = MathContext.DECIMAL32;

        System.out.println(Mathf.erf(1));
    }
}