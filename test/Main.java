import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Single.Veci;

public class Main {
    public static void main (String... args) {
        System.out.println();

        Mat m = Rand.getMat(4,4);
        Veci vals = m.eigvals();
        Mati vecs = m.eigvecs(vals);

        System.out.println(m);
        System.out.println();
        System.out.println(vals);
        System.out.println();
        System.out.println(vecs);
    }
}
