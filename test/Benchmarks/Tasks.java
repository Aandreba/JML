package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Matrix.Double.Matd;
import org.jml.Vector.Single.Vec;

public class Tasks {
    public static void main (String... args) {
        int size, epochs;

        System.out.println("Insert vector sizes");
        size = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        Vec[] a = new Vec[epochs];
        Vec[] b = new Vec[epochs];

        for (int i=0;i<epochs;i++) {
            a[i] = Rand.getVec(size, -100, 100);
            b[i] = Rand.getVec(size, -100, 100);
        }

        long time = Benchmark.time(epochs, i -> a[i].add(b[i]));
        System.out.println("Time: "+(time * 0.000001f)+" ms");
    }

    public static Matd mul (Matd a, Matd b) {
        int rows = a.rows();
        int cols = b.cols();
        int dig = Math.min(a.cols(), b.rows());

        return Matd.foreach(rows, cols, (i, j) -> {
            double sum = 0;
            for (int k=0;k<dig;k++) {
                sum += a.get(i, k) * b.get(k, j);
            }

            return sum;
        });
    }
}
