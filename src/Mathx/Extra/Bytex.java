package Mathx.Extra;

public class Bytex {
    static {
        System.loadLibrary("bytex_win");
    }

    public static void printBits (byte value) {
        for (int i=7;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }

    public native static byte sum (byte x, byte y);
    public native static byte subtr (byte x, byte y);
    public native static byte mul (byte x, byte y);
    public native static byte div (byte x, byte y);
}
