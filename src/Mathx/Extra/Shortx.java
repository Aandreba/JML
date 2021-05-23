package Mathx.Extra;

public class Shortx {
    public static void printBits (short value) {
        for (int i=15;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }
}
