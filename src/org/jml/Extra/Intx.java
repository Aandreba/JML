package org.jml.Extra;

public class Intx {
    public static String bitString (int value) {
        StringBuilder builder = new StringBuilder();
        for (int i=31;i>=0;i--) {
            builder.append((value >> i) & 1);
        }

        return builder.toString();
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

    public static int leftMostBit (int a) {
        for (int i=31;i>=0;i--) {
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
