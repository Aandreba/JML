import Benchmarks.Benchmark;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.MatCUDA;
import org.jml.Vector.Single.VecCL;
import org.jml.Vector.Single.VecCUDA;

public class Main {
    public static void main(String... args) {
        MatCUDA a = Rand.getMat(3, 3).toCUDA();
        MatCUDA b = Rand.getMat(3, 3).toCUDA();

        System.out.println(a.toCPU().scalMul(b.toCPU()));
        System.out.println(a.scalMul(b));
    }
}