package org.jml.Mathx;

import org.jml.Complex.Double.Compd;

final public class Mathd {
    final public static double SQRT2 = Math.sqrt(2);
    final public static double HALFPI = Math.PI / 2;
    final public static double PI2 = 2 * Math.PI;

    final public static double TO_RADIANS = Math.PI / 180;
    final public static double TO_DEGREES = 180 / Math.PI;

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

    public static Compd[] quadratic (double a, double b, double c) {
        Compd[] x = new Compd[2];
        Compd sqrt = Compd.sqrt(b * b - 4*a*c);

        double a2 = 2 * a;
        x[0] = sqrt.add(-b).div(a2);
        x[1] = sqrt.invSubtr(-b).div(a2);
        return x;
    }

    public static Compd[] cubic (double a, double b, double c, double d) {
        final Compd zeta = Compd.sqrt(-3).subtr(1).div(2);
        Compd[] x = new Compd[3];

        double b2 = b*b;
        double d0 = b2 - 3*a*c;
        double d1 = 2*b2*b - 9*a*b*c + 27*a*a*d;

        Compd C = Compd.sqrt(d1*d1 - 4*d0*d0*d0).add(d1).div(2).pow(1f / 3);
        Compd pow = Compd.ONE;
        double k = -1 / (3 * a);

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Compd u = pow.mul(C);

            x[i] = u.add(b).add(u.invDiv(d0)).mul(k);
        }

        return x;
    }
}
