package Benchmarks;

import java.util.Scanner;
import java.util.function.Function;

public class Benchmark {
    final protected static Scanner in = new Scanner(System.in);

    interface Epoch {
        void apply (int i);
    }

    public static long time (int epochs, Epoch run) {
        long start = System.nanoTime();
        for (int i=0;i<epochs;i++) {
            run.apply(i);
        }
        long end = System.nanoTime();

        return end - start;
    }

    public static int getInt (Function<Integer,Boolean> condition) {
        while (true) {
            try {
                int val = in.nextInt();
                if (condition.apply(val)) {
                    return val;
                }
            } catch (Exception ignore){}

            System.out.println("Insert correct value");
        }
    }

    public static int getInt () {
        return getInt(i -> true);
    }
}
