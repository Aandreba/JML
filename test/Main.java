import Benchmarks.Benchmark;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.MatCL;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.VecCL;

import java.util.Arrays;

public class Main {
    public static void main(String... args) {
        MatCL a = Rand.getMat(4, 2).toCL();
        VecCL b = Rand.getVec(2).toCL();

        System.out.println(a.toCPU().add(b.toCPU()));
        System.out.println(a.add(b));
    }
}