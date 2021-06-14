package org.jml.Mathx.Extra;

public class Shortx {
    public static void printBits (short value) {
        for (int i=15;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }

    public static boolean getBit (int pos, short bits) {
        return ((bits >> pos) & 1) == 1;
    }

    public static byte numberOfLeadingZeros (short a) {
        byte r = 0;
        for (byte i=15;i>=0;i--) {
            if (getBit(i, a)) {
                return r;
            } else {
                r++;
            }
        }

        return r;
    }
}
