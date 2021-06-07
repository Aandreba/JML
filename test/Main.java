import Mathx.Mathf;
import Mathx.Rand;
import Matrix.Double.Matd;
import Matrix.Single.Mat;
import Matrix.Single.Mati;
import Vector.Single.Vec;

import java.util.Arrays;

public class Main {
    public static void main (String... args) {
        Mat a = Rand.getMat(7,7, 1, 20);

        System.out.println(a);
        System.out.println(a.eigen());
    }
}
