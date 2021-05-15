package Mathx.Extra;

public class Doublex {
    public static void printBits (double value) {
        long bits = Double.doubleToLongBits(value);
        for (int i=63;i>=0;i--) {
            System.out.print((bits >> i) & 1);
        }

        System.out.println();
    }
}
