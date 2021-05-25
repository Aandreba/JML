package Testing;

import Mathx.Rand;
import Vector.Single.Vecf;

public class OpenCL {
    public static void main (String... args) {
        Vecf a = Rand.getVecf(5);
        Vecf b = Rand.getVecf(5);

        System.out.println(a.add(b));
        System.out.println(a.toCUDA().add(b.toCUDA()));
    }
}
