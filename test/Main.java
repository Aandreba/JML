import org.jml.Extra.Floatx;
import org.jml.Extra.Shortx;
import org.jml.Mathx.Mathf;
import org.jml.Mathx.Rand;
import org.jml.Number.Type.Half;

public class Main {
    public static void main (String... args) {
        for (int i=0;i<100;i++) {
            float a = Rand.getFloat();

            System.out.println(a);
            System.out.println(Floatx.bitString(a).substring(9));
            System.out.println(Floatx.bitString((float) Math.log(a)).substring(9));
            System.out.println();
        }
    }
}