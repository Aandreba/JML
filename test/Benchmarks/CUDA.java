package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.MatCL;
import org.jml.Matrix.Single.MatCUDA;
import org.jml.Matrix.Single.Mat;

import java.text.NumberFormat;

public class CUDA {
    final private static NumberFormat pct = NumberFormat.getPercentInstance();
    static {
        pct.setMaximumFractionDigits(2);
    }

    public static void main (String... args) {
        int size, epochs;

        System.out.println("Insert matrix sizes");
        size = Benchmark.getInt(x -> x > 0);

        System.out.println("Insert epochs");
        epochs = Benchmark.getInt(x -> x > 0);

        Mat[] cpu1 = new Mat[epochs];
        Mat[] cpu2 = new Mat[epochs];
        MatCL[] cl1 = new MatCL[epochs];
        MatCL[] cl2 = new MatCL[epochs];
        MatCUDA[] cuda1 = new MatCUDA[epochs];
        MatCUDA[] cuda2 = new MatCUDA[epochs];

        for (int i=0;i<epochs;i++) {
            cpu1[i] = Rand.getMat(size, size, -100, 100);
            cl1[i] = cpu1[i].toCL();
            cuda1[i] = cpu1[i].toCUDA();

            cpu2[i] = Rand.getMat(size, size, -100, 100);
            cl2[i] = cpu2[i].toCL();
            cuda2[i] = cpu2[i].toCUDA();
        }

        long cpu = Benchmark.time(epochs, i -> cpu1[i].mul(cpu2[i]));
        long cl = Benchmark.time(epochs, i -> cl1[i].mul(cl2[i]));
        long cuda = Benchmark.time(epochs, i -> cuda1[i].mul(cuda2[i]));

        System.out.println("CPU:\t"+cpu+" ns");
        System.out.println("OpenCL:\t"+cl+" ns");
        System.out.println("CUDA:\t"+cuda+" ns");
        System.out.println();
        System.out.println("OpenCL v. CPU: "+percentage(cl,cpu));
        System.out.println("CUDA v. OpenCL: "+percentage(cuda,cl));
        System.out.println("CUDA v. CPU: "+percentage(cuda,cpu));
    }

    private static String percentage (long of, long over) {
        if (of < over) {
            float pct = of * 1f / over;
            return CUDA.pct.format(1 - pct)+" faster";
        }

        float pct = over * 1f / of;
        return CUDA.pct.format(1 - pct)+" slower";
    }
}
