package Benchmarks;

import org.jml.Link.Single.Link2D;
import org.jml.Mathx.Mathf;
import org.jml.Mathx.Rand;
import org.jml.MT.TaskManager;
import org.jml.Matrix.Single.Mat;
import org.jml.Number.Type.Float32;
import org.jml.Number.Type.Half;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Single.Vec;

public class Floating {
    public static void main (String... args) {
        System.out.println();
        int epochs = 1000;

        for (int i=20;i<=300000;i+=20) {
            Vec a = Rand.getVec(i).abs().mul(10);
            Vecd b = a.toDouble();

            long single = Benchmark.time(epochs, k -> a.foreach(Floating::sqrt));
            long dob = Benchmark.time(epochs, k -> b.foreach(Math::sqrt));

            System.out.println(i);
            System.out.println("Single: "+(single)+" ns");
            System.out.println("Double: "+(dob)+" ns");
            System.out.println((float) single / dob);
            System.out.println();
        }
    }

    public static float sqrt (float number) {
        int i;
        float x2, y;
        final float threehalfs = 1.5f;

        x2 = number * 0.5F;
        y  = number;
        i  = Float.floatToIntBits(y);
        i  = 0x5f3759df - ( i >> 1 );
        y  = Float.intBitsToFloat(i);
        y  = y * ( threehalfs - ( x2 * y * y ) );
        return 1f / (y * ( threehalfs - ( x2 * y * y ) ));
    }
}
