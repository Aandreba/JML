package org.jml.Extra;

public class Longx {
    public static String bitString (long value) {
        StringBuilder builder = new StringBuilder();
        for (int i=63;i>=0;i--) {
            builder.append((value >> i) & 1);
        }

        return builder.toString();
    }
}
