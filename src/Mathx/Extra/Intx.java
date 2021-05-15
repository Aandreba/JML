package Mathx.Extra;

public class Intx {
    public static void printBits (int value) {
        for (int i=31;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }
}
