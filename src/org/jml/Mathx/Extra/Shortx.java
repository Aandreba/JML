package org.jml.Mathx.Extra;

public class Shortx {
    public static String bitString (int value) {
        StringBuilder builder = new StringBuilder();
        for (int i=15;i>=0;i--) {
            builder.append((value >> i) & 1);
        }

        return builder.toString();
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
