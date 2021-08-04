package org.jml.Extra;

public class Bytex {
    public static String bitString (int value) {
        StringBuilder builder = new StringBuilder();
        for (int i=7;i>=0;i--) {
            builder.append((value >> i) & 1);
        }

        return builder.toString();
    }
}
