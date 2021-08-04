package org.jml.Extra;

import org.jml.Mathx.Mathf;

public class Floatx {
    public static String bitString (float value) {
        return Intx.bitString(Float.floatToIntBits(value));
    }

    public static boolean isInteger (float x) {
        int exponent = Mathf.getExponent(x);
        int mantissaBits = Float.floatToIntBits(x) & 0x7fffff;
        return (mantissaBits << (exponent + 9)) == 0;
    }
}
