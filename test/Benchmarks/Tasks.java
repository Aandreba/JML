package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Matrix.Double.MatCLd;
import org.jml.Matrix.Double.MatCUDAd;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.MatCL;
import org.jml.Matrix.Single.MatCUDA;

public class Tasks {
    public static void main (String... args) {
        int size, epochs;

        System.out.println("Insert matrix sizes");
        size = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        MatCUDAd[] a = new MatCUDAd[epochs];
        MatCUDAd[] b = new MatCUDAd[epochs];

        for (int i=0;i<epochs;i++) {
            a[i] = Rand.getMatd(size, size, -100, 100).toCUDA();
            b[i] = Rand.getMatd(size, size, -100, 100).toCUDA();
        }

        long time = Benchmark.time(epochs, i -> a[i].mul(b[i]));
        System.out.println("Time: "+(time * 0.000001f)+" ms");
    }

    public static Matd mul (Matd a, Matd b) {
        int rows = a.getRows();
        int cols = b.getCols();
        int dig = Math.min(a.getCols(), b.getRows());

        return Matd.foreach(rows, cols, (i, j) -> {
            double sum = 0;
            for (int k=0;k<dig;k++) {
                sum += a.get(i, k) * b.get(k, j);
            }

            return sum;
        });
    }
}
