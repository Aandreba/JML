package Mathx;

final public class Mathd {
    final public static double SQRT2 = Math.sqrt(2);
    final public static double HALFPI = Math.PI / 2;
    final public static double PI2 = 2 * Math.PI;

    public static double stirling (double x) {
        if (x == 0) {
            return 1;
        }

        return Math.sqrt(PI2 * x) * Math.pow(x / Math.E, x);
    }
}
