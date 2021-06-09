package org.jml.Calculus.Function.Linear;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Sqrt implements Func {
    final public static Sqrt SQRT = new Sqrt();
    private Sqrt () {}

    @Override
    public double apply(double x) {
        return Math.sqrt(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.sin(x);
    }

    @Override
    public double deriv(double x) {
        return 1 / (2 * Math.sqrt(x));
    }

    @Override
    public float deriv(float x) {
        return 1 / (2 * Mathf.sqrt(x));
    }
}
