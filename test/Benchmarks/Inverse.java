package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Single.MatCL;

public class Inverse {
    public static void main (String... args) {
        int size, epochs;

        System.out.println("Insert matrix sizes");
        size = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        Matd[] cpu1 = new Matd[epochs];
        MatCL[] gpu1 = new MatCL[epochs];

        for (int i=0;i<epochs;i++) {
            cpu1[i] = Rand.getMatd(size, size, -100, 100);
            gpu1[i] = cpu1[i].toFloat().toCL();
        }

        long cpu = Benchmark.time(epochs, i -> cpu1[i].inverse());
        long gpu = Benchmark.time(epochs, i -> gpu1[i].inverse());

        System.out.println("CPU: "+cpu+" ns");
        System.out.println("GPU: "+gpu+" ns");
        System.out.println();
        System.out.println("CPU Ratio: "+cpu*1f/gpu);
        System.out.println("GPU Ratio: "+gpu*1f/cpu);
    }
}
