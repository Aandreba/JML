package org.jml.Mathx.Extra;

import org.jml.Mathx.Mathf;

public class Floatx {
    public static void printBits (float value) {
        Intx.printBits(Float.floatToIntBits(value));
    }

    public static boolean isInteger (float x) {
        int exponent = Mathf.getExponent(x);
        int mantissaBits = Float.floatToIntBits(x) & 0x7fffff;
        return (mantissaBits << (exponent + 9)) == 0;
    }
}
