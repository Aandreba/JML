import Mathx.Extra.Floatx;
import Mathx.Number.Half;
import Mathx.Mathf;

public class Main {
    public static void main (String... args) {
        float x = 0.15625f;
        Half half = new Half(x);

        Floatx.printBits(Mathf.PI);
        System.out.println(half.floatValue());
    }
}
