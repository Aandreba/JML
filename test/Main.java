import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.Mat;
import org.jml.Vector.Single.Veci;

public class Main {
    public static void main (String... args) {
        Mat mat = new Mat(new float[][]{ {1,2,5}, {3,4,6}, {7,8,9} });
        Veci eigvals = mat.eigvals();

        /*System.out.println(mat);
        System.out.println(eigvals);*/
        System.out.println(mat.eigvec(eigvals.get(1)));
    }
}
