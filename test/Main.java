import Mathx.Rand;
import Matrix.Single.Mat;
import Matrix.Single.Mati;

public class Main {
    public static void main (String... args) {
        Mati a = Rand.getMati(2,2);

        System.out.println(a);
        System.out.println();
        System.out.println(a.exp());
        System.out.println(a.sqrt());
        System.out.println(a.sqrt().pow(2));
    }
}
