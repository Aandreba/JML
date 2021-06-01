import Mathx.Mathf;
import Mathx.Rand;
import Matrix.Double.Matd;
import Matrix.Single.Mat;
import Vector.Single.Vec;

import java.io.IOException;

public class Main {
    private static float last = Rand.getFloat(-10, 10);
    public static void main (String... args) throws IOException {
        Mat a = Rand.getMat(1,1);

        System.out.println(a);
        System.out.println(a.log());
        System.out.println(Math.log(a.get(0,0)));
    }
}
