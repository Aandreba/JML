import org.jml.Fraction.Frac;
import org.jml.Fraction.Fracb;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.MathContext;

public class Main {
    public static void main (String... args) {
        Fracb pi = new Fracb(Mathb.pi(MathContext.DECIMAL128));
        System.out.println(pi);
        System.out.println(pi.doubleValue());
    }
}