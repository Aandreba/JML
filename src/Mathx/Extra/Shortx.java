package Mathx.Extra;

public class Shortx {
    static {
        System.loadLibrary("shortx_win");
    }

    public static void printBits (short value) {
        for (int i=15;i>=0;i--) {
            System.out.print((value >> i) & 1);
        }

        System.out.println();
    }

    public native static short sum (short x, short y);
    public native static short subtr (short x, short y);
    public native static short mul (short x, short y);
    public native static short div (short x, short y);
}
