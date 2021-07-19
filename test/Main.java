import org.jml.Complex.Single.Comp;
import org.jml.Mathx.FourierSeries;
import org.jml.Vector.Single.Veci;

public class Main {
    public static void main(String... args) {
        Veci series = FourierSeries.calculate(10, (float t) -> t >= 0.5f ? Comp.ONE : Comp.MONE);

        for (float t=0;t<=1;t+=0.001f) {
            System.out.println(t+": "+FourierSeries.compute(t, series));
        }
    }
}