package Testing;

import Vector.VecCL;

public class OpenCL {
    public static void main (String... args) {
        VecCL a = new VecCL(1, 2, 3);
        VecCL b = new VecCL(4, 5, 6);

        System.out.println(a.add(b));
        System.out.println(a.add(2f));
    }
}
