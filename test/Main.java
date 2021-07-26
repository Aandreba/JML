import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.Mathf;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main (String... args) throws IOException {
        Path2D path = new Path2D.Float();
        path.moveTo(50, 200);
        path.quadTo(200, 300, 395, 1);
        path.closePath();

        ComplexFunction function = FourierSeries.pathToFunction(path.getPathIterator(null));
        BufferedImage img = new BufferedImage(400, 450, BufferedImage.TYPE_INT_RGB);

        for (float t=0;t<=1;t+=0.001f) {
            Comp point = function.apply(t);
            System.out.println(t+": "+point);

            int x = (int) Mathf.clamp(point.re, 0, 399);
            int y = (int) Mathf.clamp(point.im, 0, 449);
            img.setRGB(x, y, Color.WHITE.getRGB());
        }

        ImageIO.write(img, "png", new File("out.png"));
    }
}