package org.jml.Calculus.Function.Linear;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Cbrt implements Func {
    final private static double root = 2d / 3;
    final private static float rootf = (float) root;

    final public static Cbrt CBRT = new Cbrt();
    private Cbrt() {}

    @Override
    public double apply(double x) {
        return Math.cbrt(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.cbrt(x);
    }

    @Override
    public double deriv(double x) {
        return 1 / (3 * Math.pow(x, root));
    }

    @Override
    public float deriv(float x) {
        return 1 / (3 * Mathf.pow(x, rootf));
    }
}
