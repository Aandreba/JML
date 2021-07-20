import org.jml.Complex.Single.Comp;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.Mathf;
import org.jml.Vector.Single.Veci;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class Main {
    public static void main(String... args) {
        Path2D.Float triangle = new Path2D.Float();
        triangle.moveTo(-1, -1);
        triangle.lineTo(0, 1);
        triangle.lineTo(1, -1);
        triangle.lineTo(-1, -1);

        Veci series = FourierSeries.calculate(10, triangle.getPathIterator(null));
        for (float t=0;t<=1;t+=0.01f) {
            System.out.println(t+": "+FourierSeries.compute(t, series));
        }
    }
}