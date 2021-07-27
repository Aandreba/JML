import org.jml.Calculus.Integral;
import org.jml.Function.Real.SingleReal;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

public class Main {
    public static void main (String... args) throws IOException {
        System.out.println(Mathb.log(Mathb.FIVE, MathContext.DECIMAL128));
    }
}