package Mathx.Extra;

import Mathx.Mathf;

public class Doublex {
    public static void printBits (double value) {
        long bits = Double.doubleToLongBits(value);
        for (int i=63;i>=0;i--) {
            System.out.print((bits >> i) & 1);
        }

        System.out.println();
    }

    public static boolean isInteger (double x) {
        int exponent = Math.getExponent(x);
        long mantissaBits = Double.doubleToLongBits(x) & 0xfffffffffffffL;
        return (mantissaBits << (exponent + 12)) == 0;
    }
}
