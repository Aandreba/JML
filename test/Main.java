import org.jml.Calculus.Fourier;
import org.jml.Mathx.Extra.Pi;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.MathContext;

public class Main {
    public static void main (String... args) {
        BigDecimal pi = Pi.ramanujan(34);

        System.out.println(pi);
        System.out.println(Mathb.exp(pi, MathContext.DECIMAL128));
    }
}