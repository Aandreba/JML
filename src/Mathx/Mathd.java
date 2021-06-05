package Mathx;

final public class Mathd {
    final public static double SQRT2 = Math.sqrt(2);
    final public static double HALFPI = Math.PI / 2;
    final public static double PI2 = 2 * Math.PI;

    public static float factorial (int x) {
        if (x < 0) {
            return factorial((float) x);
        }

        float y = 1;
        for (int i=1;i<=x;i++) {
            y *= i;
        }

        return y;
    }

    public static float factorial (float x) {
        float y = 1;
        float last = 0;

        long k = 1;
        while (y != last) {
            last = y;
            y *= Mathf.pow((k + 1f) / k, x) * k/(x + k);
            k++;
        }

        return y;
    }

    public static double stirling (double x) {
        if (x == 0) {
            return 1;
        }

        return Math.sqrt(PI2 * x) * Math.pow(x / Math.E, x);
    }
}
