package Mathx.Extra;

public class Floatx {
    public static void printBits (float value) {
        Intx.printBits(Float.floatToIntBits(value));
    }
}
