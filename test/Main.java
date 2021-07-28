import org.jml.Complex.Big.Compb;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.MathContext;

public class Main {
    public static void main (String... args) {
        float x = Mathf.sqrt(2) / 2;
        float last = 0;

        boolean sum = false;
        while (x != last) {
            float alpha = x * Mathf.sqrt(x + 2) / 2;

            last = x;
            x = sum ? x + alpha : x - alpha;
            sum = !sum;
        }

        System.out.println(x);
    }
}