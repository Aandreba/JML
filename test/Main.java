import org.jml.Function.Real.DecimalReal;
import org.jml.Function.Real.RealFunction;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;

public class Main {
    public static void main (String... args) {
        System.out.println(RealFunction.SQRT.integ(0, 1));
    }
}