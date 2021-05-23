package Mathx.Extra;

public class Bytex {
    public static void printBits (byte value) {
        for (int i=7;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }
}
