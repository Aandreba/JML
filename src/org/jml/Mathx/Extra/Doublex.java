package org.jml.Mathx.Extra;

public class Doublex {
    public static String bitString (double value) {
        return Longx.bitString(Double.doubleToLongBits(value));
    }

    public static boolean isInteger (double x) {
        int exponent = Math.getExponent(x);
        long mantissaBits = Double.doubleToLongBits(x) & 0xfffffffffffffL;
        return (mantissaBits << (exponent + 12)) == 0;
    }
}
