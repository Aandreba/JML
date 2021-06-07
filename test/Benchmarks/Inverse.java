package Benchmarks;

import Mathx.Rand;
import Matrix.Single.Mat;

public class Inverse {
    public static void main (String... args) {
        int n, epochs;

        System.out.println("Insert matrix size");
        n = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        Mat[] a = new Mat[epochs];
        for (int i=0;i<epochs;i++) {
            a[i] = Rand.getMat(n, n);
        }

        long adj = Benchmark.time(epochs, (i) -> {
            a[i].inverse();
        });

        /*long rref = Benchmark.time(epochs, (i) -> {
            a[i].rrefInverse();
        });

        System.out.println("Adj: "+adj+" ms");
        System.out.println("RRef: "+rref+" ms");*/
    }
}
