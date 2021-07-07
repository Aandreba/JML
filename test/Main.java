import Benchmarks.Benchmark;
import org.jml.Mathx.Rand;
import org.jml.Vector.Single.VecCL;
import org.jml.Vector.Single.VecCUDA;

public class Main {
    public static void main(String... args) {
        VecCL a = Rand.getVec(3).toCL();
        VecCL b = Rand.getVec(3).toCL();

        System.out.println(a.toCPU().mul(b.toCPU()));
        System.out.println(a.mul(b));
    }
}