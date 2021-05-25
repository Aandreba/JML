package Testing.Benchmarks;

import Mathx.Rand;
import Matrix.Double.Mat;
import Matrix.Single.MatCLf;

public class CL {
    public static void main (String... args) {
        int size, epochs;

        System.out.println("Insert matrix sizes");
        size = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        Mat[] cpu1 = new Mat[epochs];
        Mat[] cpu2 = new Mat[epochs];
        MatCLf[] gpu1 = new MatCLf[epochs];
        MatCLf[] gpu2 = new MatCLf[epochs];

        for (int i=0;i<epochs;i++) {
            cpu1[i] = Rand.getMat(size, size, -100, 100);
            gpu1[i] = cpu1[i].toFloat().toCL();

            cpu2[i] = Rand.getMat(size, size, -100, 100);
            gpu2[i] = cpu2[i].toFloat().toCL();
        }

        long cpu = Benchmark.time(epochs, i -> cpu1[i].mul(cpu2[i]));
        long gpu = Benchmark.time(epochs, i -> gpu1[i].mul(gpu2[i]));

        System.out.println("CPU: "+cpu+" ns");
        System.out.println("GPU: "+gpu+" ns");
        System.out.println();
        System.out.println("CPU Ratio: "+cpu*1f/gpu);
        System.out.println("GPU Ratio: "+gpu*1f/cpu);
    }
}
