package Testing;

import Matrix.Matf;
import Vector.Vecf;

public class Main {
    public static void main (String... args) {
        Matf target = new Matf(new Vecf(0), new Vecf(1), new Vecf(1), new Vecf(0));
        System.out.println(target);
    }
}
