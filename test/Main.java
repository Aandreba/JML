import Benchmarks.Benchmark;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.MatCL;
import org.jml.Vector.Single.Vec;

import java.util.Arrays;

public class Main {
    public static void main(String... args) {
        MatCL a = Rand.getMat(2, 2).toCL();
        MatCL b = Rand.getMat(2, 2).toCL();
    }
}