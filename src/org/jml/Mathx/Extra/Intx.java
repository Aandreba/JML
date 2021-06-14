package org.jml.Mathx.Extra;

public class Intx {
    public static void printBits (int value) {
        for (int i=31;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }

    public static boolean getBit (int pos, int bits) {
        return ((bits >> pos) & 1) == 1;
    }

    public static int rightMostBit (int a) {
        for (int i=0;i<32;i++) {
            if (getBit(i, a)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isOdd (int x) {
        return (x & 1) == 1;
    }

    public static boolean isEven (int x) {
        return !isOdd(x);
    }
}
