import org.jml.Calculus.Integral;
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
        triangle.curveTo(-1, -1, 1, 1, -1, 1);

        Integral.ComplexFunction func = FourierSeries.pathToFunction(triangle.getPathIterator(null));
        for (float t=0;t<=1;t+=0.01f) {
            System.out.println(t+": "+func.apply(t).real);
        }

        /*Veci series = FourierSeries.calculate(10, triangle.getPathIterator(null));
        for (float t=0;t<=1;t+=0.01f) {
            System.out.println(t+": "+FourierSeries.compute(t, series));
        }*/
    }
}