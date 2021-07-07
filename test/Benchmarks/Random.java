package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Vector.Single.Vec;

public class Random {
    public static void main (String... args) {
        long epochs = (long) 2e9;
        int[] results = new int[10];

        for (long i=0;i<epochs;i++) {
            int rand = Rand.getInt(0, 9);
            results[rand]++;
        }

        Vec vec = new Vec(10);
        for (int i=0;i<10;i++) {
            vec.set(i, results[i]);
        }

        System.out.println(vec.div(epochs).mul(100));
    }
}
